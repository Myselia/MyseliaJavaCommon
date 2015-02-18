package com.mycelia.common.runtime.network;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycelia.common.communication.AtomConverter;
import com.mycelia.common.communication.TransmissionInputStream;
import com.mycelia.common.communication.TransmissionOutputStream;
import com.mycelia.common.communication.structures.Atom;
import com.mycelia.common.communication.structures.Transmission;
import com.mycelia.common.constants.DaemonOpcode;
import com.mycelia.common.constants.OpcodePrefix;
import com.mycelia.common.constants.SandboxOpcodes;
import com.mycelia.common.exception.MyceliaRuntimeException;
import com.mycelia.common.generic.GenericUtil;
import com.mycelia.common.runtime.CommunicationDevice;

public abstract class NetworkNode implements CommunicationDevice
{
	private class TransmisisonReadThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				while(true)
				{
					if(isInterrupted())
						throw new InterruptedException();
					
					acceptTransmission(tin.readTransmission());
				}
			}
			catch(IOException e)
			{
				logger.error("", e);
			}
			catch(InterruptedException e)
			{
				//Just let the thread finish.
			}
		}
	}
	
	/**
	 * How many milliseconds to sleep when waiting for an event to occur.
	 */
	private static final int SLEEP_DURATION=100;
	
	private static Logger logger=LoggerFactory.getLogger(NetworkNode.class);
	
	private Queue<Transmission> userTransmissions;
	private Queue<Transmission> frameworkTransmissions;
	private List<Transmission> frameworkAnswerTrans;
	private int daemonPort;
	private String masterNodeId;
	private TransmissionInputStream tin;
	private TransmissionOutputStream tout;
	private AtomConverter atomConverter;
	
	public NetworkNode(int daemonPort)
	{
		this.daemonPort=daemonPort;
		
		userTransmissions=new LinkedList<Transmission>();
		frameworkTransmissions=new LinkedList<Transmission>();
		frameworkAnswerTrans=new LinkedList<Transmission>();
		atomConverter=new AtomConverter();
	}
	
	public String getMasterNodeId()
	{
		return masterNodeId;
	}
	
	private void getMasterNodeIdFromDaemon()
	{
		Transmission transmission=new Transmission();
		transmission.setOpcode(DaemonOpcode.GET_MASTER_ID_REQUEST);
		
		sendTransmission(transmission);
		
		transmission=readFrameworkAnswerTransmission(DaemonOpcode.GET_MASTER_ID_ANSWER);
		
		masterNodeId=atomConverter.getAsString(transmission.getAtoms().get(0));
	}
	
	public final void start() throws IOException
	{
		Transmission frameworkTrans;
		
		try(Socket socket=new Socket("127.0.0.1", daemonPort))
		{
			tin=new TransmissionInputStream(socket.getInputStream());
			tout=new TransmissionOutputStream(socket.getOutputStream());
			
			TransmisisonReadThread readThread=new TransmisisonReadThread();
			readThread.start();
			
			getMasterNodeIdFromDaemon();
			
			//This node has been created. Execute the nodeStart lifecycle event.
			nodeStart(); 
			
			while(true)
			{
				frameworkTrans=receiveFrameworkTransmission(0);
				
				if(frameworkTrans!=null)
				{
					if(frameworkTrans.getOpcode().equals(SandboxOpcodes.STOP))
					{
						/* This node has been request to stop.
						 * Execute nodeStop lifecycle event and then stop execution.
						 */
						nodeStop();
						readThread.interrupt();
						GenericUtil.joindIgnoreInterrupts(readThread);
						break;
					}
					else if(isAnswerTransmission(frameworkTrans.getOpcode()))
					{
						//Store the framework answer transmission for later retrieval.
						
						synchronized(frameworkAnswerTrans)
						{
							frameworkAnswerTrans.add(frameworkTrans);
						}
					}
					else
					{
						//Execute Node type dependent framework action.
						executeFrameworkAction(frameworkTrans);
					}
				}
			}
		}
	}
	
	private boolean isAnswerTransmission(String opcode)
	{
		return opcode.equals(SandboxOpcodes.GET_RESULT_ANSWER_SLAVE)||
				opcode.equals(SandboxOpcodes.START_TASK_ANSWER_SLAVE)||
				opcode.equals(SandboxOpcodes.TASK_STATUS_ANSWER_SLAVE)||
				opcode.equals(SandboxOpcodes.GET_TASK_INSTANCE_ANSWER_SLAVE)||
				opcode.equals(DaemonOpcode.GET_MASTER_ID_ANSWER);
	}
	
	/**
	 * Stores the Transaction in this node's memory buffer for later
	 * retrieval by the node's thread.
	 */
	public final void acceptTransmission(Transmission transmission)
	{
		if(transmission.getOpcode().startsWith(OpcodePrefix.SANDBOX_MASTER)||
				transmission.getOpcode().startsWith(OpcodePrefix.SANDBOX_SLAVE))
		{
			synchronized(frameworkTransmissions)
			{
				frameworkTransmissions.add(transmission);
			}
		}
		else
		{
			synchronized(userTransmissions)
			{
				userTransmissions.add(transmission);
			}
		}
	}
	
	private void universalSendTransmission(Transmission transmission)
	{
		try
		{
			tout.writeTransmission(transmission);
		}
		catch(IOException e)
		{
			throw new MyceliaRuntimeException(e);
		}
	}
	
	/**
	 * Sends a Transmission.
	 * @see CommunicationDevice.sendTransmission(Transmission transmission)
	 */
	@Override
	public final void sendTransmission(Transmission transmission)
	{
		//logger.debug("sendTransmission(Transmission transmission)");
		universalSendTransmission(transmission);
	}
	
	public final void sendTransmissionToMasterNode(String opcode, List<Atom> atoms)
	{
		sendTransmission(opcode, getMasterNodeId(), atoms);
	}
	
	public final void sendTransmission(String opcode, String toNodeId, List<Atom> atoms)
	{
		Transmission transmission=new Transmission();
		transmission.setOpcode(opcode);
		transmission.setTo(toNodeId);
		transmission.setAtoms(atoms);
		
		//logger.debug("sendTransmission(String opcode, String toNodeId, List<Atom> atoms)");
		universalSendTransmission(transmission);
	}
	
	public final void sendTransmission(String opcode, String toNodeId, Atom atom)
	{
		Transmission transmission=new Transmission();
		transmission.setOpcode(opcode);
		transmission.setTo(toNodeId);
		transmission.addAtom(atom);
		
		//logger.debug("sendTransmission(String opcode, String toNodeId, Atom atom)");
		universalSendTransmission(transmission);
	}
	
	public final void sendTransmission(String opcode, String toNodeId)
	{
		Transmission transmission=new Transmission();
		transmission.setOpcode(opcode);
		transmission.setTo(toNodeId);
		
		//logger.debug("sendTransmission(String opcode, String toNodeId, Atom atom)");
		universalSendTransmission(transmission);
	}
	
	/**
	 * Return a Transmission received by this node.
	 * @see CommunicationDevice.receiveTransmission(int timeout)
	 */
	@Override
	public final Transmission receiveTransmission(int timeout)
	{
		synchronized(userTransmissions)
		{
			if(userTransmissions.size()>0)
			{
				return userTransmissions.remove();
			}
			else if(timeout==0)
			{
				return null;
			}
		}
		
		long startTimeNano=System.nanoTime();
		long elapsedTimeMilli;
		
		do
		{
			try
			{
				Thread.sleep(SLEEP_DURATION);
			}
			catch(InterruptedException e)
			{
				//Do nothing.
			}
			
			synchronized(userTransmissions)
			{
				if(userTransmissions.size()>0)
				{
					return userTransmissions.remove();
				}
			}
			
			elapsedTimeMilli=(System.nanoTime()-startTimeNano)/1000;
		}while(timeout==-1||elapsedTimeMilli>=timeout);
		
		return null;
	}
	
	/**
	 * Reads a received framework Transmission.
	 * 
	 * @see receiveTransmission(int timeout) for more details.
	 */
	protected final Transmission receiveFrameworkTransmission(int timeout)
	{
		synchronized(frameworkTransmissions)
		{
			if(frameworkTransmissions.size()>0)
			{
				return frameworkTransmissions.remove();
			}
			else if(timeout==0)
			{
				return null;
			}
		}
		
		long startTimeNano=System.nanoTime();
		long elapsedTimeMilli;
		
		do
		{
			try
			{
				Thread.sleep(SLEEP_DURATION);
			}
			catch(InterruptedException e)
			{
				//Do nothing.
			}
			
			synchronized(frameworkTransmissions)
			{
				if(frameworkTransmissions.size()>0)
				{
					return frameworkTransmissions.remove();
				}
			}
			
			elapsedTimeMilli=(System.nanoTime()-startTimeNano)/1000;
		}while(timeout==-1||elapsedTimeMilli>=timeout);
		
		return null;
	}
	
	/**
	 * Retrieves an already stored framework answer transmission with the specified opcode.
	 */
	public final Transmission readFrameworkAnswerTransmission(String opcode)
	{
		Transmission ret=null;
		
		synchronized(frameworkAnswerTrans)
		{
			for(Transmission transmission: frameworkAnswerTrans)
			{
				if(transmission.getOpcode().equals(opcode))
				{
					ret=transmission;
					break;
				}
			}
			
			if(ret!=null)
				frameworkAnswerTrans.remove(ret);
		}
		
		return ret;
	}
	
	/**
	 * Same as readFrameworkAnswerTransmission(String opcode) but with timeout.
	 */
	public final Transmission receiveFrameworkAnswerTransmission(String opcode, int timeout)
	{
		Transmission transmission=readFrameworkAnswerTransmission(opcode);
		
		if(transmission!=null)
		{
			return transmission;
		}
		else if(timeout==0)
		{
			return null;
		}
		
		long startTimeNano=System.nanoTime();
		long elapsedTimeMilli;
		
		do
		{
			try
			{
				Thread.sleep(SLEEP_DURATION);
			}
			catch(InterruptedException e)
			{
				//Do nothing.
			}
			
			transmission=readFrameworkAnswerTransmission(opcode);
			
			if(transmission!=null)
				return transmission;
			
			elapsedTimeMilli=(System.nanoTime()-startTimeNano)/1000;
		}while(timeout==-1||elapsedTimeMilli>=timeout);
		
		return null;
	}
	
	//Abstract methods
	
	/**
	 * Life cycle <strong>nodeStart</strong> event: called just after this node has been created.  
	 */
	protected abstract void nodeStart();
	
	/**
	 * Life cycle <strong>nodeStop</strong> event: called just before this node is deleted.  
	 */
	protected abstract void nodeStop();
	
	protected abstract void executeFrameworkAction(Transmission transmission);
}
