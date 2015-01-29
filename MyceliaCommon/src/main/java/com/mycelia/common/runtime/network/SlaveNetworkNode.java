package com.mycelia.common.runtime.network;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.mycelia.common.communication.AtomConverter;
import com.mycelia.common.communication.bean.Atom;
import com.mycelia.common.communication.bean.Transmission;
import com.mycelia.common.constants.SandboxOpcodes;
import com.mycelia.common.framework.MyceliaSlaveNode;
import com.mycelia.common.framework.Task;
import com.mycelia.common.framework.TaskInstance;

public class SlaveNetworkNode extends NetworkNode
{
	private MyceliaSlaveNode node;
	private AtomConverter atomConverter;
	
	public SlaveNetworkNode(int daemonPort, MyceliaSlaveNode node)
	{
		super(daemonPort);
		
		this.node=node;
		
		atomConverter=new AtomConverter();
	}

	@Override
	protected void nodeStart()
	{
		node.nodeStart();
	}

	@Override
	protected void nodeStop()
	{
		node.nodeStop();
	}

	@Override
	protected void executeFrameworkAction(Transmission transmission)
	{
		if(transmission.getOpcode().equals(SandboxOpcodes.START_TASK_REQUEST_SLAVE))
			startTaskFrameworkAction(transmission);
		else if(transmission.getOpcode().equals(SandboxOpcodes.GET_RESULT_REQUEST_SLAVE))
			getTaskResultFrameworkAction(transmission);
		else if(transmission.getOpcode().equals(SandboxOpcodes.GET_TASK_INSTANCE_REQUEST_SLAVE))
			getTaskInstanceFrameworkAction(transmission);
		else if(transmission.getOpcode().equals(SandboxOpcodes.IS_TASK_DONE_REQUEST_SLAVE))
			isTaskDoneFrameworkAction(transmission);
		else if(transmission.getOpcode().equals(SandboxOpcodes.GET_TASKS_REQUEST_SLAVE))
			getTasksFrameworkAction(transmission);
		else if(transmission.getOpcode().equals(SandboxOpcodes.GET_RUNNING_TASKS_REQUEST_SLAVE))
			getRunningTasksFrameworkAction(transmission);
	}
	
	private void sendGenericError(String toNodeId, String message)
	{
		sendTransmission(SandboxOpcodes.GENERIC_ERROR, toNodeId, atomConverter.toAtom(message));
	}
	
	private void getRunningTasksFrameworkAction(Transmission transmission)
	{
		Set<TaskInstance> tasks=node.getRunningTaskInstances();
	
		try
		{
			//Answer back task instance ID
			Transmission answer=new Transmission();
			answer.setOpcode(SandboxOpcodes.GET_RESULT_ANSWER_SLAVE);
			answer.setTo(transmission.getFrom());
			
			for(TaskInstance task: tasks)
			{
				answer.addAtom(atomConverter.toAtom(task));
			}
			
			sendTransmission(answer);
		}
		catch(IOException e)
		{
			sendGenericError(transmission.getFrom(), "Exception: "+e.getMessage());
		}
	}
	
	private void getTasksFrameworkAction(Transmission transmission)
	{
		Set<Task> tasks=node.getTasks();
		
		//Answer back task instance ID
		Transmission answer=new Transmission();
		answer.setOpcode(SandboxOpcodes.GET_RESULT_ANSWER_SLAVE);
		answer.setTo(transmission.getFrom());
		
		for(Task task: tasks)
		{
			answer.addAtom(atomConverter.toAtom(task.getName()));
		}
		
		sendTransmission(answer);
	}
	
	private void isTaskDoneFrameworkAction(Transmission transmission)
	{
		int taskInstanceId=atomConverter.getAsInt(transmission.getAtoms().get(0));
		
		boolean isDone=node.isTaskInstanceDone(taskInstanceId);
		
		//Answer back task instance ID
		Transmission answer=new Transmission();
		answer.setOpcode(SandboxOpcodes.IS_TASK_DONE_ANSWER_SLAVE);
		answer.setTo(transmission.getFrom());
		answer.addAtom(atomConverter.toAtom(isDone));
		
		sendTransmission(answer);
	}
	
	private void getTaskInstanceFrameworkAction(Transmission transmission)
	{
		int taskInstanceId=atomConverter.getAsInt(transmission.getAtoms().get(0));
		
		TaskInstance taskInstance=node.getTaskInstance(taskInstanceId);
		
		//Answer back task instance ID
		Transmission answer=new Transmission();
		answer.setOpcode(SandboxOpcodes.GET_TASK_INSTANCE_ANSWER_SLAVE);
		answer.setTo(transmission.getFrom());
		answer.addAtom(atomConverter.toAtom(taskInstance.getTask().getName()));
		
		sendTransmission(answer);
	}
	
	private void getTaskResultFrameworkAction(Transmission transmission)
	{
		int taskInstanceId=atomConverter.getAsInt(transmission.getAtoms().get(0));
		
		Serializable result=node.getTaskResult(taskInstanceId);
		
		//Answer back task instance ID
		try
		{
			Transmission answer=new Transmission();
			answer.setOpcode(SandboxOpcodes.GET_RESULT_ANSWER_SLAVE);
			answer.setTo(transmission.getFrom());
			answer.addAtom(atomConverter.toAtomInferType(result));
			
			sendTransmission(answer);
		}
		catch(IOException e)
		{
			sendGenericError(transmission.getFrom(), "Exception: "+e.getMessage());
		}
	}
	
	private void startTaskFrameworkAction(Transmission transmission)
	{
		List<Atom> atoms=transmission.getAtoms();
		String taskName=atomConverter.getAsString(atoms.get(0));
		Serializable[] arguments=new Serializable[atoms.size()-1];
	
		//Deserialize transmission
		try
		{
			for(int i=1; i<atoms.size(); i++)
				arguments[i-1]=atomConverter.fromAtomInferType(atoms.get(i));
		}
		catch(ClassNotFoundException | IOException e)
		{
			sendGenericError(transmission.getFrom(), "Exception: "+e.getMessage());
			return;
		}
		
		//Find task to start from task name.
		Task taskToStart=null;
		
		for(Task task: node.getTasks())
		{
			if(task.getName().equals(taskName))
			{
				taskToStart=task;
				break;
			}
		}
		
		if(taskToStart==null)
		{
			sendGenericError(transmission.getFrom(), "No Task with name: "+taskName);
			return;
		}
		
		//Start task
		int taskInstanceId=node.startTask(taskToStart, arguments);
		
		//Answer back task instance ID
		Transmission answer=new Transmission();
		answer.setOpcode(SandboxOpcodes.START_TASK_ANSWER_SLAVE);
		answer.setTo(transmission.getFrom());
		answer.addAtom(atomConverter.toAtom(taskInstanceId));
		
		sendTransmission(answer);
	}
}
