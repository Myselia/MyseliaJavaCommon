package com.mycelia.common.runtime.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mycelia.common.communication.bean.Atom;
import com.mycelia.common.communication.bean.Transmission;
import com.mycelia.common.constants.SandboxOpcodes;
import com.mycelia.common.exception.MyceliaRuntimeException;
import com.mycelia.common.framework.MyceliaMasterNode;
import com.mycelia.common.framework.MyceliaNode;
import com.mycelia.common.framework.MyceliaSlaveNode;
import com.mycelia.common.framework.RemoteSlaveNode;
import com.mycelia.common.generic.GenericUtil;
import com.mycelia.common.runtime.LoadBalancer;
import com.mycelia.common.runtime.NodeContainer;

public class LocalNodeContainer implements NodeContainer
{
	private static int DEFAULT_SLEEP_TIME=100;
	
	private Class<? extends MyceliaMasterNode> masterNodeClass;
	private Class<? extends MyceliaSlaveNode> slaveNodeClass;
	private ThreadGroup nodeThreadGroup;
	private int lastNodeId;
	private String masterNodeId;
	private LoadBalancer loadBalancer;
	
	/**
	 * Key: node ID, Value: ThreadNode
	 */
	private Map<String, ThreadNode> nodeMap;
	
	public LocalNodeContainer()
	{
		nodeThreadGroup=new ThreadGroup("Mycelia Nodes");
		nodeMap=new HashMap<String, ThreadNode>();
		lastNodeId=0;
	}
	
	public void routeTransmission(Transmission transmission)
	{
		ThreadNode remoteThreadNode=getThreadNode(transmission.getTo());
		
		remoteThreadNode.acceptTransmission(transmission);
	}
	
	@Override
	public <M extends MyceliaMasterNode, S extends MyceliaSlaveNode> void setNodeClasses(Class<M> masterNodeClass, Class<S> slaveNodeClass)
	{
		this.masterNodeClass=masterNodeClass;
		this.slaveNodeClass=slaveNodeClass;
	}
	
	private String getNewNodeId()
	{
		lastNodeId++;
		
		return Integer.toString(lastNodeId);
	}
	
	private ThreadNode getThreadNode(String nodeId)
	{
		ThreadNode threadNode=nodeMap.get(nodeId);
		
		if(threadNode==null)
			throw new MyceliaRuntimeException("Node ID "+nodeId+" not found.");
		
		return threadNode;
	}
	
	private <N extends MyceliaNode> N createNode(Class<N> nodeClass) throws InstantiationException, IllegalAccessException
	{
		N node=nodeClass.newInstance();
		node.setNodeId(getNewNodeId());
		node.setNodeContainer(this);
		node.setLoadBalancer(loadBalancer);
		
		return node;
	}
	
	@Override
	public String createAndStartMasterNode() throws MyceliaRuntimeException
	{
		try
		{
			MyceliaMasterNode masterNode=createNode(masterNodeClass);
			
			//Create Daemon wrapper 
			MasterThreadNode masterThread=new MasterThreadNode(nodeThreadGroup, this, masterNode);
			masterNodeId=masterNode.getNodeId();
			//creates bridge between MyceliaMasterNode and the Daemon.
			masterNode.setCommunicationDevice(masterThread);
			
			//Start Master node
			nodeMap.put(masterNodeId, masterThread);
			masterThread.start();
			
			return masterNode.getNodeId();
		}
		catch(Exception e)
		{
			throw new MyceliaRuntimeException(e);
		}
	}

	@Override
	public void stopMasterAndDeleteAllNodes(String localNodeId)
	{
		if(localNodeId==null)
		{
			localNodeId=masterNodeId;
		}
		
		ThreadNode localThreadNode=getThreadNode(localNodeId);
		ThreadNode remoteThreadNode=getThreadNode(masterNodeId);
		
		localThreadNode.sendTransmission(SandboxOpcodes.STOP, masterNodeId, new ArrayList<Atom>(0));
		
		GenericUtil.joindIgnoreInterrupts(remoteThreadNode);
		
		deleteAllSlaveNodes(localNodeId);
	}

	@Override
	public RemoteSlaveNode createSlaveNode(String localNodeId)
	{
		try
		{
			MyceliaSlaveNode slaveNode=createNode(slaveNodeClass);
			
			//Create Daemon wrapper 
			SlaveThreadNode slaveThread=new SlaveThreadNode(nodeThreadGroup, this, slaveNode);
			
			//creates bridge between MyceliaSlaveNode and the Daemon.
			slaveNode.setCommunicationDevice(slaveThread);
			
			//Start Slave node
			nodeMap.put(slaveNode.getNodeId(), slaveThread);
			slaveThread.start();
			
			ThreadNode localThreadNode=getThreadNode(localNodeId);
			
			loadBalancer.addNode(slaveNode.getNodeId()); 
			
			return new RemoteSlaveNodeImpl(localThreadNode, slaveNode.getNodeId());
		}
		catch(Exception e)
		{
			throw new MyceliaRuntimeException(e);
		}
	}

	@Override
	public Set<RemoteSlaveNode> createSlaveNodes(String localNodeId, int numberOfNodes)
	{
		Set<RemoteSlaveNode> nodes=new HashSet<RemoteSlaveNode>();
		
		for(int i=0; i<numberOfNodes; i++)
			nodes.add(createSlaveNode(localNodeId));
		
		return nodes;
	}

	@Override
	public RemoteSlaveNode getRemoteSlaveNode(String localNodeId, String remoteNodeId)
	{
		ThreadNode localThreadNode=getThreadNode(localNodeId);
		ThreadNode remoteThreadNode=getThreadNode(remoteNodeId);
		
		return new RemoteSlaveNodeImpl(localThreadNode, remoteThreadNode.getNodeId());
	}

	@Override
	public void deleteSlaveNode(String localNodeId, String remoteNodeId)
	{
		ThreadNode localThreadNode=getThreadNode(localNodeId);
		ThreadNode remoteThreadNode=getThreadNode(remoteNodeId);
		
		loadBalancer.removeNode(remoteNodeId);
		
		localThreadNode.sendTransmission(SandboxOpcodes.STOP, remoteNodeId, new ArrayList<Atom>(0));
		
		GenericUtil.joindIgnoreInterrupts(remoteThreadNode);
	}

	@Override
	public void deleteSlaveNodes(String localNodeId, int numberOfNodes)
	{
		ThreadNode localThreadNode=getThreadNode(localNodeId);
		int i=0;
		List<ThreadNode> stoppedNodes=new ArrayList<ThreadNode>(numberOfNodes);
		String nodeId;
		
		for(Entry<String, ThreadNode> entry: nodeMap.entrySet())
		{
			if(!entry.getValue().isAlive())
				continue;
			
			nodeId=entry.getValue().getNodeId();
			
			if(nodeId.equals(masterNodeId))
				continue;
			
			loadBalancer.removeNode(nodeId);
			
			localThreadNode.sendTransmission(SandboxOpcodes.STOP, nodeId, new ArrayList<Atom>(0));
			stoppedNodes.add(entry.getValue());
			i++;
			
			if(i==numberOfNodes)
				break;
		}
		
		for(ThreadNode threadNode: stoppedNodes)
			GenericUtil.joindIgnoreInterrupts(threadNode);
	}

	@Override
	public void deleteAllSlaveNodes(String localNodeId)
	{
		ThreadNode localThreadNode=getThreadNode(localNodeId);
		String nodeId;
		
		for(Entry<String, ThreadNode> entry: nodeMap.entrySet())
		{
			if(!entry.getValue().isAlive())
				continue;
			
			nodeId=entry.getValue().getNodeId();
			
			if(nodeId.equals(masterNodeId))
				continue;
			
			loadBalancer.removeNode(nodeId);
			
			localThreadNode.sendTransmission(SandboxOpcodes.STOP, nodeId, new ArrayList<Atom>(0));
		}
		
		while(nodeThreadGroup.activeCount()>1)
		{
			try
			{
				Thread.sleep(DEFAULT_SLEEP_TIME);
			}
			catch(InterruptedException e)
			{
				//Do nothing
			}
		}
	}

	@Override
	public String getMasterNodeId()
	{
		return masterNodeId;
	}

	@Override
	public Set<String> getAllSlaveNodeIds()
	{
		Set<String> allNodes=nodeMap.keySet();
		
		allNodes.remove(masterNodeId);
		
		return allNodes;
	}

	@Override
	public void setLoadBalancer(LoadBalancer loadBalancer)
	{
		this.loadBalancer=loadBalancer;
	}
}
