package com.mycelia.common.runtime.local;

import java.util.Map;

import com.mycelia.common.framework.MyceliaMasterNode;
import com.mycelia.common.framework.MyceliaSlaveNode;
import com.mycelia.common.runtime.ApplicationRuntime;
import com.mycelia.common.runtime.LoadBalancer;
import com.mycelia.common.runtime.LoadBalancerFactory;
import com.mycelia.common.runtime.LoadBalancerStrategy;
import com.mycelia.common.runtime.NodeContainer;

public class LocalApplicationRuntime implements ApplicationRuntime
{
	private Class<? extends MyceliaMasterNode> masterClass;
	private Class<? extends MyceliaSlaveNode> slaveClass;
	private LoadBalancerStrategy strategy;
	private NodeContainer nodeContainer;
	private LoadBalancer loadBalancer;
	
	public LocalApplicationRuntime()
	{
		//Do nothing
	}
	
	@Override
	public <M extends MyceliaMasterNode, S extends MyceliaSlaveNode>
		void setNodeClasses(Class<M> masterClass, Class<S> slaveClass)
	{
		this.masterClass=masterClass;
		this.slaveClass=slaveClass;
	}
	
	@Override
	public void setLoadBalancerStrategy(LoadBalancerStrategy strategy)
	{
		this.strategy=strategy;
	}
	
	@Override
	public void initialize()
	{
		loadBalancer=LoadBalancerFactory.getInstance().getLoadBalancer(strategy);
		nodeContainer=new LocalNodeContainer();
		
		nodeContainer.setLoadBalancer(loadBalancer);
		nodeContainer.setNodeClasses(masterClass, slaveClass);
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

	@Override
	public void setOptions(Map<String, Object> options)
	{
		//Do Nothing (no runtime specific options for LOCAL runtime)
	}
}
