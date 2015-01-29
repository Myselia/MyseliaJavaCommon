package com.mycelia.common.runtime;

import java.util.Map;

import com.mycelia.common.framework.MyceliaMasterNode;
import com.mycelia.common.framework.MyceliaSlaveNode;

/**
 * A Mycelia Application Runtime.
 * 
 * This is what is responsible for setuping and running a Mycelia Application.
 * There is multiple concrete types of Mycelia Runtime, enumerated in ApplicationRuntimeType.
 * 
 * All classes implementing this interface needs to have a 0 argument constructor.
 */
public interface ApplicationRuntime
{
	/**
	 * Sets the required user defined Mycelia classes.
	 * 
	 * @param masterNodeClass
	 * 			The user class subclassing the MyceliaMasterNode class.
	 *  
	 * @param slaveNodeClass
	 * 			The user class subclassing the MyceliaSlaveNode class.
	 */
	public <M extends MyceliaMasterNode, S extends MyceliaSlaveNode>
		void setNodeClasses(Class<M> masterNodeClass, Class<S> slaveNodeClass);
	
	/**
	 * Sets the load balancer strategy for this application.
	 */
	public void setLoadBalancerStrategy(LoadBalancerStrategy strategy);
	
	/**
	 * Sets the runtime specific options.
	 * 
	 * @param options
	 * 			Runtime specific options. null if none specified.
	 */
	public void setOptions(Map<String, Object> options);
	
	/**
	 * Initializes the runtime.
	 * 
	 * This perform some setup operation only needed once prior to running the framework.
	 * 
	 * This method is executed after ApplicationRuntime.setLoadBalancerStrategy(strategy) and
	 * ApplicationRuntime.setNodeClasses(masterNodeClass, slaveNodeClass) during runtime initialization. 
	 */
	public void initialize();
	
	/**
	 * Gets the NodeContainer responsible for managing the creating and deletion of concrete
	 * node's instances in this Mycelia Application. 
	 */
	public NodeContainer getNodeContainer();
	
	/**
	 * Gets the LoadBalancer responsible for balancing task execution request generated by the Mycelia nodes
	 * during runtime.
	 */
	public LoadBalancer getLoadBalancer();
}
