package com.mycelia.common.framework;

import com.mycelia.common.runtime.ApplicationRuntime;
import com.mycelia.common.runtime.ApplicationRuntimeFactory;
import com.mycelia.common.runtime.ApplicationRuntimeType;
import com.mycelia.common.runtime.LoadBalancerStrategy;

/**
 * Mycelia application.
 * 
 * Provided a MyceliaMasterNode class and a MyceliaSlaveNode class,
 * this class will construct a Mycelia application that can be executed
 * by the Mycelia Runtime. 
 *
 * @param <M> Application's MyceliaMasterNode type. 
 * @param <S> Application's MyceliaSlaveNode type.
 */
public class MyceliaApplication<M extends MyceliaMasterNode, S extends MyceliaSlaveNode>
{
	private Class<M> masterModule;
	private Class<S> slaveModule;
	private ApplicationRuntimeType runtimeType;
	private LoadBalancerStrategy strategy;
	private ApplicationRuntime applicationRuntime;
	
	/**
	 * Creates a Mycelia application given a MyceliaMasterNode class and a MyceliaSlaveNode class.
	 * 
	 * This will create a Mycelia application with runtime type Local and
	 * load balancer strategy Round robin.
	 * 
	 * @param masterModule
	 * 			Application's MyceliaMasterNode class.
	 * 
	 * @param slaveModule
	 * 			Application's MyceliaSlaveNode class.
	 */
	public MyceliaApplication(Class<M> masterModule, Class<S> slaveModule)
	{
		this.masterModule=masterModule;
		this.slaveModule=slaveModule;
		
		runtimeType=ApplicationRuntimeType.LOCAL;
		strategy=LoadBalancerStrategy.ROUND_ROBIN;
	}
	
	/**
	 * Sets the runtime type that must be used to run this application.
	 */
	public void setRuntimeType(ApplicationRuntimeType runtimeType)
	{
		if(runtimeType==null)
			throw new IllegalArgumentException("runtimeType cannot be null");
		
		this.runtimeType=runtimeType;
	}
	
	/**
	 * Sets the load balancing strategy to be used in this application.
	 */
	public void setLoadBalancerStrategy(LoadBalancerStrategy strategy)
	{
		if(strategy==null)
			throw new IllegalArgumentException("strategy cannot be null");
		
		this.strategy=strategy;
	}
	
	/**
	 * Starts the Mycelia application.
	 * 
	 * This will create and start the master node.
	 */
	public void start()
	{
		if(applicationRuntime==null)
		{
			applicationRuntime=ApplicationRuntimeFactory.getInstance().getApplicationRuntime(
					runtimeType, strategy, masterModule, slaveModule);
		}
		
		applicationRuntime.getNodeContainer().createAndStartMasterNode();
	}
	
	/**
	 * Stops the Mycelia application.
	 * 
	 * This will delete the master node and all slave nodes;
	 * stopping all nodes and releasing all resources. 
	 */
	public void stop()
	{
		applicationRuntime.getNodeContainer().stopMasterAndDeleteAllNodes(null);
	}
}
