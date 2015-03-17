package com.mycelia.sandbox.testapp;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycelia.common.framework.MyceliaSlaveNode;
import com.mycelia.common.framework.Task;

public class SlaveNode extends MyceliaSlaveNode
{
	private static Logger logger=LoggerFactory.getLogger(SlaveNode.class);
	
	@Override
	public void nodeStop()
	{
		//Do nothing
	}

	@Override
	public Set<Task> getTasks()
	{
		Set<Task> tasks=new HashSet<Task>();
		tasks.add(new Task("calculate"));
		
		return tasks;
	}

	@Override
	protected Serializable executeTask(Task task, Serializable[] parameters)
	{
		if(task.getName().equals("calculate"))
		{
			try
			{
				int base=(Integer)parameters[0];
				int exponent=(Integer)parameters[1];
			
				logger.info("Slave ID "+getNodeId()+" got calculate task with base "+base+" and exponent "+exponent);
				double result=Math.pow(base, exponent);
				logger.info("Slave ID "+getNodeId()+" "+base+"^"+exponent+"="+result);
				
				return result;
			}
			catch(ClassCastException e)
			{
				System.out.println(parameters[0].getClass());
				System.exit(-1);
				return null;
			}
		}
		else
		{
			return "Unkown task name: \""+task.getName()+"\"";
		}
	}
}
