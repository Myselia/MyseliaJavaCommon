package com.mycelia.common.runtime.local;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mycelia.common.communication.AtomConverter;
import com.mycelia.common.communication.bean.Atom;
import com.mycelia.common.communication.bean.Transmission;
import com.mycelia.common.constants.SandboxOpcodes;
import com.mycelia.common.exception.MyceliaRuntimeException;
import com.mycelia.common.exception.MyceliaTimeoutException;
import com.mycelia.common.framework.RemoteSlaveNode;
import com.mycelia.common.framework.Task;
import com.mycelia.common.framework.TaskInstance;

public class RemoteSlaveNodeImpl implements RemoteSlaveNode
{
	/**
	 * Default timeout when waiting for a remote node's answer (in milliseconds).
	 */
	private static final int DEFAULT_TIMOUT=1000;
	
	private ThreadNode localThreadNode;
	private String remoteNodeId;
	private AtomConverter atomConverter;
	
	public RemoteSlaveNodeImpl(ThreadNode localThreadNode, String remoteNodeId)
	{
		this.localThreadNode=localThreadNode;
		this.remoteNodeId=remoteNodeId;
		
		atomConverter=new AtomConverter();
	}

	@Override
	public Set<Task> getTasks()
	{
		String opcode=SandboxOpcodes.GET_TASKS_REQUEST_SLAVE;
		
		localThreadNode.sendTransmission(opcode, remoteNodeId);
		
		Transmission answer=localThreadNode.receiveFrameworkAnswerTransmission(
				SandboxOpcodes.GET_RESULT_ANSWER_SLAVE, DEFAULT_TIMOUT);
		
		if(answer==null)
			throw new MyceliaTimeoutException("Timeout while waiting for slave node to answer.");
		
		Set<Task> tasks=new HashSet<Task>(answer.getAtoms().size());
		
		for(Atom atom: answer.getAtoms())
		{
			tasks.add(new Task(atomConverter.getAsString(atom)));
		}
		
		return tasks;
	}
	
	@Override
	public Set<TaskInstance> getRunningTaskInstance()
	{
		String opcode=SandboxOpcodes.GET_RUNNING_TASKS_REQUEST_SLAVE;
		
		try
		{
			localThreadNode.sendTransmission(opcode, remoteNodeId);
			
			Transmission answer=localThreadNode.receiveFrameworkAnswerTransmission(
					SandboxOpcodes.GET_RUNNING_TASKS_ANSWER_SLAVE, DEFAULT_TIMOUT);
			
			if(answer==null)
				throw new MyceliaTimeoutException("Timeout while waiting for slave node to answer.");
			
			Set<TaskInstance> taskInstances=new HashSet<TaskInstance>(answer.getAtoms().size());
			
			for(Atom atom: answer.getAtoms())
			{
				taskInstances.add((TaskInstance)atomConverter.getAsSerializable(atom));
			}
			
			return taskInstances;
		}
		catch(ClassNotFoundException | IOException e)
		{
			throw new MyceliaRuntimeException(e);
		}
	}

	@Override
	public int startTask(Task task, Serializable... parameters) throws MyceliaRuntimeException
	{
		String opcode=SandboxOpcodes.START_TASK_REQUEST_SLAVE;
		List<Atom> atoms=new ArrayList<Atom>(parameters.length);
		
		try
		{
			atoms.add(atomConverter.toAtom(task.getName()));
			
			for(Serializable parameter: parameters)
			{
				atoms.add(atomConverter.toAtomInferType(parameter));
			}
			
			localThreadNode.sendTransmission(opcode, remoteNodeId, atoms);
			
			Transmission answer=localThreadNode.receiveFrameworkAnswerTransmission(
					SandboxOpcodes.START_TASK_ANSWER_SLAVE, DEFAULT_TIMOUT);
			
			if(answer==null)
				throw new MyceliaTimeoutException("Timeout while waiting for slave node to answer.");
			
			return atomConverter.getAsInt(answer.getAtoms().get(0));
		}
		catch(IOException e)
		{
			throw new MyceliaRuntimeException(e);
		}
	}

	@Override
	public Object getTaskResult(int taskInstanceId)
	{
		String opcode=SandboxOpcodes.GET_RESULT_REQUEST_SLAVE;
		
		try
		{
			localThreadNode.sendTransmission(opcode, remoteNodeId, atomConverter.toAtom(taskInstanceId));
			
			Transmission answer=localThreadNode.receiveFrameworkAnswerTransmission(
					SandboxOpcodes.GET_RESULT_ANSWER_SLAVE, DEFAULT_TIMOUT);
			
			if(answer==null)
				throw new MyceliaTimeoutException("Timeout while waiting for slave node to answer.");
			
			return atomConverter.fromAtomInferType(answer.getAtoms().get(0));
		}
		catch(ClassNotFoundException | IOException e)
		{
			throw new MyceliaRuntimeException(e);
		}
	}

	@Override
	public boolean isTaskInstanceDone(int taskInstanceId)
	{
		String opcode=SandboxOpcodes.IS_TASK_DONE_REQUEST_SLAVE;
		
		localThreadNode.sendTransmission(opcode, remoteNodeId, atomConverter.toAtom(taskInstanceId));
		
		Transmission answer=localThreadNode.receiveFrameworkAnswerTransmission(
				SandboxOpcodes.IS_TASK_DONE_ANSWER_SLAVE, DEFAULT_TIMOUT);
		
		if(answer==null)
			throw new MyceliaTimeoutException("Timeout while waiting for slave node to answer.");
		
		
		return atomConverter.getAsBoolean(answer.getAtoms().get(0));
	}

	@Override
	public String getNodeId()
	{
		return remoteNodeId;
	}

	@Override
	public TaskInstance getTaskInstance(int taskInstanceId)
	{
		String opcode=SandboxOpcodes.GET_TASK_INSTANCE_REQUEST_SLAVE;
		
		localThreadNode.sendTransmission(opcode, remoteNodeId, atomConverter.toAtom(taskInstanceId));
		
		Transmission answer=localThreadNode.receiveFrameworkAnswerTransmission(
				SandboxOpcodes.GET_TASK_INSTANCE_ANSWER_SLAVE, DEFAULT_TIMOUT);
		
		if(answer==null)
			throw new MyceliaTimeoutException("Timeout while waiting for slave node to answer.");
		
		TaskInstance taskInstance=new TaskInstance();
		taskInstance.setInstanceId(taskInstanceId);
		taskInstance.setNodeId(remoteNodeId);
		taskInstance.setTask(new Task(atomConverter.getAsString(answer.getAtoms().get(0))));
		
		return taskInstance;
	}
}
