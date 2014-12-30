package com.mycelia.common.runtime;

import com.mycelia.common.exception.MyceliaRuntimeException;
import com.mycelia.common.framework.MyceliaMasterNode;
import com.mycelia.common.framework.MyceliaSlaveNode;

/**
 * This class is used by the Mycelia Runtime to find, instantiate and setup
 * a specific Runtime for a given RuntimeType.
 * 
 * Each Runtime needs to be provided the user defined (subclassed) MyceliaNode classes
 * and a load balancer strategy. 
 */
public class ApplicationRuntimeFactory
{
	private static ApplicationRuntimeFactory instance;
	
	public static ApplicationRuntimeFactory getInstance()
	{
		if(instance==null)
			instance=new ApplicationRuntimeFactory();
		
		return instance;
	}
	
	private ApplicationRuntimeFactory()
	{
		//Do nothing
	}
	
	/**
	 * Find, instantiate and setup a specific Runtime for a given RuntimeType.
	 * 
	 * @param runtimeType
	 * 			The wanted ApplicationRuntimeType.
	 * 
	 * @param strategy
	 * 			The LoadBalancerStrategy to use in this Mycelia Application.
	 * 
	 * @param masterNodeClass
	 * 			The user defined MyceliaMasterNode sublcass.
	 * 
	 * @param slaveNodeClass
	 * 			The user defined MyceliaSlaveNode subclass.
	 * 
	 * @return
	 * 			An instantiated MyceliaRuntime that was already setup and is
	 * 			ready to be used by the Mycelia Application.
	 */
	public <M extends MyceliaMasterNode, S extends MyceliaSlaveNode>
		ApplicationRuntime getApplicationRuntime(ApplicationRuntimeType runtimeType, LoadBalancerStrategy strategy,
				Class<M> masterNodeClass, Class<S> slaveNodeClass)
	{
		if(strategy==null)
			throw new IllegalArgumentException("strategy cannot be null");

		Class<? extends ApplicationRuntime> runtimeClass;
		
		try
		{
			runtimeClass=(Class<? extends ApplicationRuntime>)Class.forName(runtimeType.getClassName());
		}
		catch(ClassNotFoundException e)
		{
			throw new MyceliaRuntimeException("You do not have the required implementation package for Mycelia Runtime Type "+runtimeType);
		}
		catch(ClassCastException e)
		{
			throw new MyceliaRuntimeException("The provider of the following class did not implement ApplicationRuntime (mandatory): "+runtimeType.getClassName());
		}
		
		try
		{
			ApplicationRuntime runtime=runtimeClass.newInstance();
			
			runtime.setLoadBalancerStrategy(strategy);
			runtime.setNodeClasses(masterNodeClass, slaveNodeClass);
			
			runtime.initialize();
			
			return runtime;
		}
		catch(InstantiationException|IllegalAccessException e)
		{
			throw new MyceliaRuntimeException(e);
		}
	}
}
