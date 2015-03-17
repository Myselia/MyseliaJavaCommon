package com.mycelia.sandbox.testapp;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycelia.common.framework.MyceliaMasterNode;
import com.mycelia.common.framework.Task;
import com.mycelia.common.framework.TaskInstance;
import com.mycelia.common.generic.GenericUtil;

public class MasterNode extends MyceliaMasterNode
{
	/**
	 * Time to wait between assigning new task.
	 */
	private static int WAIT_TIME=1000;
	
	private static Logger logger=LoggerFactory.getLogger(MasterNode.class);
	
	private class DoWorkThread extends Thread
	{
		@Override
		public void run()
		{
			Random random=new Random();
			int base;
			int exponent;
			double result;
			TaskInstance taskInstance;
			Set<TaskInstance> startedTasks=new HashSet<TaskInstance>();
			
			try
			{
				while(true)
				{
					if(isInterrupted())
						throw new InterruptedException();
					
					for(int i=0; i<5; i++)
					{
						base=random.nextInt(100);
						exponent=random.nextInt(100);
						
						taskInstance=startTaskOnAnyNode(new Task("calculate"), base, exponent);
						logger.info("Started task "+taskInstance.getNodeId()+"-"+taskInstance.getInstanceId()+
								" calculate with base "+base+" and exponent "+exponent);
						
						startedTasks.add(taskInstance);
					}
					
					Thread.sleep(500); //sleep for half a second
					
					for(TaskInstance task: startedTasks)
					{
						result=(Double)getRemoteSlaveNode(task.getNodeId()).getTaskResult(task.getInstanceId());
						logger.info("Task "+task.getNodeId()+"-"+task.getInstanceId()+" yields result: "+result);
					}
					
					sleep(WAIT_TIME);
				}
			}
			catch(InterruptedException e)
			{
				//Let the thread finish if we receive interrupt.
				logger.debug("DoWorkThread interrupted, here we die.");
			}
		}
	}
	
	private DoWorkThread thread;
	
	@Override
	public void start()
	{
		createSlaveNodes(5);
		
		thread=new DoWorkThread();
		thread.setName("Master DoWorkThread");
		thread.start();
	}

	@Override
	public void stop()
	{
		thread.interrupt();
		
		GenericUtil.joindIgnoreInterrupts(thread);
	}
}
