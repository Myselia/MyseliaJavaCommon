package com.mycelia.common.runtime.network;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycelia.common.exception.NotImplementedException;
import com.mycelia.common.framework.MyceliaMasterNode;
import com.mycelia.common.framework.MyceliaSlaveNode;
import com.mycelia.common.framework.NodeType;
import com.mycelia.common.runtime.ApplicationRuntime;
import com.mycelia.common.runtime.LoadBalancer;
import com.mycelia.common.runtime.LoadBalancerFactory;
import com.mycelia.common.runtime.LoadBalancerStrategy;
import com.mycelia.common.runtime.NodeContainer;

public class NetworkApplicationRuntime implements ApplicationRuntime
{
	private static Logger logger=LoggerFactory.getLogger(NetworkApplicationRuntime.class);
	
	private Class<? extends MyceliaMasterNode> masterNodeClass;
	private Class<? extends MyceliaSlaveNode> slaveNodeClass;
	private LoadBalancerStrategy strategy;
	private NodeContainer nodeContainer;
	private LoadBalancer loadBalancer;
	private NodeType nodeType;
	private int daemonPort;
	
	public NetworkApplicationRuntime()
	{
		nodeType=null;
		daemonPort=-1;
	}
	
	@Override
	public <M extends MyceliaMasterNode, S extends MyceliaSlaveNode> void setNodeClasses(Class<M> masterNodeClass, Class<S> slaveNodeClass)
	{
		this.masterNodeClass=masterNodeClass;
		this.slaveNodeClass=slaveNodeClass;
	}

	@Override
	public void setLoadBalancerStrategy(LoadBalancerStrategy strategy)
	{
		this.strategy=strategy;
	}

	@Override
	public void setOptions(Map<String, Object> options)
	{
		if(options==null)
			throw new IllegalArgumentException("Needs options node type and daemon port number");
		
		try
		{
			for(String key: options.keySet())
			{
				if(NetworkRuntimeOptions.LOCAL_NODE_TYPE.equals(key))
				{
					nodeType=(NodeType)options.get(key);
				}
				else if(NetworkRuntimeOptions.DAEMON_PORT.equals(key))
				{
					daemonPort=(Integer)options.get(key);
				}
			}
		}
		catch(IllegalArgumentException | ClassCastException e)
		{
			logger.error("Illegal arguments", e); 
			
			throw new IllegalArgumentException("Illegal arguments.");
		}
		
		if(nodeType==null||daemonPort==-1)
			throw new IllegalArgumentException("Needs options node type and daemon port number");
	}

	@Override
	public void initialize()
	{
		loadBalancer=LoadBalancerFactory.getInstance().getLoadBalancer(strategy);
		nodeContainer=new NetworkNodeContainer(daemonPort);
		
		nodeContainer.setNodeClasses(masterNodeClass, slaveNodeClass);
		nodeContainer.setLoadBalancer(loadBalancer);
	}
	
	public void startLocalNode()
	{
		if(nodeType==NodeType.SLAVE)
		{
			
			SlaveNetworkNode networkNode;
			
			try
			{
				MyceliaSlaveNode node=slaveNodeClass.newInstance();
				node.setNodeId("localID");
				node.setLoadBalancer(loadBalancer);
				
				networkNode=new SlaveNetworkNode(daemonPort, node);
				
				node.setCommunicationDevice(networkNode);
			}
			catch(IllegalAccessException | InstantiationException e)
			{
				logger.error("Error creating local node", e);
				return;
			}
			
			try
			{
				networkNode.start();
			}
			catch(IOException e)
			{
				logger.error("Error starting local node", e);
			}
		}
		else
			throw new NotImplementedException();
	}

	@Override
	public NodeContainer getNodeContainer()
	{
		return nodeContainer;
	}

	@Override
	public LoadBalancer getLoadBalancer()
	{
		return loadBalancer;
	}
}
