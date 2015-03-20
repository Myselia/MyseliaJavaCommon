package com.mycelia.common.framework;

import java.io.IOException;
import java.io.Serializable;

import com.mycelia.common.communication.units.Atom;
import com.mycelia.common.communication.units.Transmission;
import com.mycelia.common.constants.ComponentType;
import com.mycelia.common.framework.communication.Message;
import com.mycelia.common.runtime.CommunicationDevice;
import com.mycelia.common.runtime.LoadBalancer;
import com.mycelia.common.runtime.NodeContainer;

/**
 * A Mycelia integral entity capable of processing part of a Mycelia Application
 * and communicate with other MyceliaNodes. 
 */
public abstract class MyceliaNode
{
	/**
	 * The unique ID representing this node. 
	 */
	private String nodeId;
	
	/**
	 * The communication interfaced used to communicate with other MyceliaNodes.
	 */
	private CommunicationDevice communicationDevice;

	
	/**
	 * The NodeContainer used to get information residing at the nodes arrangement level.
	 */
	private NodeContainer nodeContainer;
	
	/**
	 * Abstract method to propagate NodeContainer only to some children.
	 */
	public void setNodeContainer(NodeContainer nodeContainer)
	{
		this.nodeContainer=nodeContainer;
	}
	
	/**
	 * Abstract method to propagate LoadBalancer only to some children.
	 */
	public abstract void setLoadBalancer(LoadBalancer loadBalancer);
	
	/**
	 * Receive the CommunicationDevice from the NodeContainer creating this node.
	 */
	public final void setCommunicationDevice(CommunicationDevice communicationDevice)
	{
		this.communicationDevice=communicationDevice;
	}
	
	/**
	 * Converts a user object Message to a Framework usable Transmission.
	 * 
	 * This uses the recipient and content from the Message object
	 * and adds this node's context information (sender Node ID, opcode) to the Transmission. 
	 * 
	 * @throws IOException
	 */
	private Transmission toTransmission(Message message) throws IOException
	{
		Transmission transmission=new Transmission(ComponentType.DEFAULT_USER_OPCODE_PREFIX, "node:"+nodeId, "somewhere");
		
		return transmission;
	}
	
	/**
	 * Converts a end-user Transmission to a user consumable Message object.
	 * 
	 * This method converts a Transmission object to a user Message by removing
	 * extra context information from the Transmission object.  
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private Message toMessage(Transmission transmission) throws ClassNotFoundException, IOException
	{
		Message message=new Message();
		message.setFrom(transmission.getFrom());
		message.setTo(transmission.getTo());
		
		for(Atom atom: transmission.getAtoms())
			message.addElement(atomConverter.fromAtomInferType(atom));
		
		return message;
	}
	
	/***
	 * Sends a Message to the corresponding node.
	 */
	protected final void sendMessage(Message message) throws IOException
	{ 
		communicationDevice.sendTransmission(toTransmission(message));
	}
	
	/**
	 * Receive a message that was sent from a MyceliaNode to this node.
	 * 
	 * Return the first Message received. If no message was received,
	 * this method listens <code>timeout</code> milliseconds for a message to arrive.
	 * After <code>timeout</code> milliseconds, this method throws a MyceliaTimeoutException.
	 * 
	 * @param timeout
	 * 			Timeout in milliseconds.
	 * @return
	 * 		The received Message.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	protected final Message receiveMessage(int timeout) throws ClassNotFoundException, IOException
	{
		Transmission transmission=communicationDevice.receiveTransmission(timeout);
		
		if(transmission==null)
			return null;
		
		return toMessage(transmission);
	}
	
	/**
	 * Shorthand to sendMessage(Message message), this methods sets the Message's recipient to
	 * the MyceliaMasterNode's ID.  
	 * 
	 * @throws IOException
	 */
	protected final void sendMessageToMaster(Message message) throws IOException
	{
		message.setTo(nodeContainer.getMasterNodeId());
		sendMessage(message);
	}
	
	/**
	 * Shorthand to sendMessage(Message message), this methods sets the Message's recipient to
	 * a value that tells the Mycelia Framework to send this Message to ANY MyceliaSlaveNode.
	 * 
	 * @throws IOException
	 */
	protected final void sendMessageToAnySlave(Message message) throws IOException
	{
		message.setTo(com.mycelia.common.constants.ComponentType.ANY_SLAVE_NODE);
		sendMessage(message);
	}
	
	/**
	 * Sets this node's ID.
	 * 
	 * Used by the NodeContainer after creation of this node.
	 */
	public final void setNodeId(String nodeId)
	{
		this.nodeId=nodeId;
	}
	
	/**
	 * Gets this node's ID.
	 */
	public final String getNodeId()
	{
		return nodeId;
	}
}
