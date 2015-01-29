package com.mycelia.common.runtime.network;

import java.util.Set;

import com.mycelia.common.exception.MyceliaRuntimeException;
import com.mycelia.common.exception.NotImplementedException;
import com.mycelia.common.framework.MyceliaMasterNode;
import com.mycelia.common.framework.MyceliaSlaveNode;
import com.mycelia.common.framework.RemoteSlaveNode;
import com.mycelia.common.runtime.LoadBalancer;
import com.mycelia.common.runtime.NodeContainer;

public class NetworkNodeContainer implements NodeContainer
{
	private Class<? extends MyceliaMasterNode> masterNodeClass;
	private Class<? extends MyceliaSlaveNode> slaveNodeClass;
	private LoadBalancer loadBalancer;
	private int daemonPort;
	
	public NetworkNodeContainer(int daemonPort)
	{
		this.daemonPort=daemonPort;
	}
	
	@Override
	public <M extends MyceliaMasterNode, S extends MyceliaSlaveNode> void setNodeClasses(Class<M> masterNodeClass, Class<S> slaveNodeClass)
	{
		this.masterNodeClass=masterNodeClass;
		this.slaveNodeClass=slaveNodeClass;
	}

	@Override
	public void setLoadBalancer(LoadBalancer loadBalancer)
	{
		this.loadBalancer=loadBalancer;
	}

	@Override
	public String createAndStartMasterNode() throws MyceliaRuntimeException
	{
		//TODO
		throw new NotImplementedException();
	}

	@Override
	public void stopMasterAndDeleteAllNodes(String localNodeId)
	{
		//TODO
		throw new NotImplementedException();
	}

	@Override
	public String getMasterNodeId()
	{
		//TODO
		throw new NotImplementedException();
	}

	@Override
	public Set<String> getAllSlaveNodeIds()
	{
		//TODO
		throw new NotImplementedException();
	}

	@Override
	public RemoteSlaveNode createSlaveNode(String localNodeId)
	{
		//TODO
		throw new NotImplementedException();
	}
	
	public void createAndStartLocalSlaveNode()
	{
		//asd
	}

	@Override
	public Set<RemoteSlaveNode> createSlaveNodes(String localNodeId, int numberOfNodes)
	{
		//TODO
		throw new NotImplementedException();
	}

	@Override
	public RemoteSlaveNode getRemoteSlaveNode(String localNodeId, String remoteNodeId)
	{
		//TODO
		throw new NotImplementedException();
	}

	@Override
	public void deleteSlaveNode(String localNodeId, String remoteNodeId)
	{
		//TODO
		throw new NotImplementedException();
	}

	@Override
	public void deleteSlaveNodes(String localNodeId, int numberOfNodes)
	{
		//TODO
		throw new NotImplementedException();
	}

	@Override
	public void deleteAllSlaveNodes(String localNodeId)
	{
		//TODO
		throw new NotImplementedException();
	}

}
