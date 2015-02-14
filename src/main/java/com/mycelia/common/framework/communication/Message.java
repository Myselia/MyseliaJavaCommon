package com.mycelia.common.framework.communication;

import java.io.Serializable;
import java.util.List;

/**
 * A message sent from one MyceliaNode to another.
 * 
 * This object is used to carry a specific message from one node to the other.
 */
public class Message
{
	/**
	 * The sender's node ID.
	 */
	private String from;
	
	/**
	 * The recipient's node ID.
	 */
	private String to;
	
	/**
	 * The different elements constituting the content of this message. 
	 */
	private List<Serializable> elements;

	public Message()
	{
		//Do nothing
	}
	
	public void addElement(Serializable serializable)
	{
		elements.add(serializable);
	}
	
	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from=from;
	}

	public String getTo()
	{
		return to;
	}

	public void setTo(String to)
	{
		this.to=to;
	}

	public List<Serializable> getElements()
	{
		return elements;
	}

	public void setElements(List<Serializable> elements)
	{
		this.elements = elements;
	}
}
