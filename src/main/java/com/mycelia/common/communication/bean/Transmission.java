package com.mycelia.common.communication.bean;

import java.util.LinkedList;
import java.util.List;

/**
 * A specific message sent from one MyceliaNode to another.
 */
public class Transmission
{
	/**
	 * The ID of the Transaction.
	 * 
	 * This is a unique identifier for this specific instance of transmission.
	 * If a message is transmitted twice with the same content, it is going to
	 * have a different Transmission ID.
	 */
	private String id;
	
	/**
	 * The Node ID of the Node that sent this transmission.
	 */
	private String from;
	
	/**
	 * The Node ID of the intended recipient of this transmission. 
	 */
	private String to;
	
	/**
	 * The opcode describing more specifically how this message should be interpreted
	 * at the Node level once received.
	 */
	private String opcode;
	
	/**
	 * List of Atom object representing the content of the message.
	 */
	private List<Atom> atoms;

	public Transmission()
	{
		atoms=new LinkedList<Atom>();
	}
	
	public Transmission(String id, String from, String to)
	{
		this.id=id;
		this.from=from;
		this.to=to;
		
		atoms=new LinkedList<Atom>();
	}

	public Transmission(Transmission transmission)
	{
		this.id=transmission.id;
		this.from=transmission.from;
		this.to=transmission.to;
		this.opcode=transmission.opcode;
		this.atoms=transmission.atoms;
	}

	public void setAttributes(String id, String from, String to)
	{
		this.id=id;
		this.from=from;
		this.to=to;
	}
	
	public void setOpcode(String opcode)
	{
		this.opcode=opcode;
	}

	public void setAtoms(List<Atom> list)
	{
		atoms=list;
	}
	
	public void addAtom(Atom atom)
	{
		atoms.add(atom);
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getTo()
	{
		return to;
	}

	public void setTo(String to)
	{
		this.to = to;
	}

	public String getOpcode()
	{
		return opcode;
	}

	public List<Atom> getAtoms()
	{
		return atoms;
	}

	@Override
	public String toString()
	{
		String str="[id: "+id+", to: "+to+", from: "+from+", opcode: "+opcode+", atoms: {";
		
		boolean first=true;
		
		for(Atom atom: atoms)
		{
			if(first)
				first=false;
			else
				str+=", ";
			
			str+=atom.toString();
		}
		
		str+="}]";
		
		return str;
	}
}
