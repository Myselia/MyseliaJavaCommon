package com.mycelia.common.framework;

import java.io.Serializable;

/**
 * An instance of a particular task that was started on the current node.
 * 
 * This is where a task is executed with specific arguments at a specific point in time,
 * yielding a specific result. Once the TaskInstance is finished executing the result generated
 * is stored for later retrieval by the Mycelia Application.  
 */
public class TaskInstance implements Serializable
{
	private static final long serialVersionUID=-6183684795324760980L;
	
	/**
	 * The node ID this TaskInstance is running on. 
	 */
	private String nodeId;
	
	/**
	 * ID specific for this particular TaskInsance on this node.
	 */
	private int instanceId;
	
	/**
	 * The task that was started with specific arguments.
	 */
	private Task task;
	
	public TaskInstance()
	{
		//Do nothing
	}

	public int getInstanceId()
	{
		return instanceId;
	}

	public void setInstanceId(int instanceId)
	{
		this.instanceId=instanceId;
	}

	public Task getTask()
	{
		return task;
	}

	public void setTask(Task task)
	{
		this.task=task;
	}

	public String getNodeId()
	{
		return nodeId;
	}

	public void setNodeId(String nodeId)
	{
		this.nodeId = nodeId;
	}
	
	@Override
	public String toString()
	{
		return "[nodeId: \""+nodeId+"\", instance ID: "+instanceId+", task: "+task+" ]";
	}
}
