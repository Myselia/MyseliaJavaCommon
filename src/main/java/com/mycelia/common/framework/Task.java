package com.mycelia.common.framework;

/**
 * A specific task that can be performed on a MyceliaSlaveNode.
 * 
 * A Task represents a computation algorithm or user defined action
 * that can be performed by a MyceliaSlaveNode.
 * Different arguments can be provided at runtime for the same task.
 */
public class Task
{
	/**
	 * The task's name; this needs to uniquely identify the task on this node. 
	 */
	private String name;
	
	public Task()
	{
		//Do nothing
	}
	
	public Task(String name)
	{
		this.name=name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return "[name: \""+name+"\"]";
	}
}
