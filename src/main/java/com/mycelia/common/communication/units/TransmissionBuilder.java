package com.mycelia.common.communication.units;

import java.util.ArrayList;

import org.apache.commons.lang.SerializationUtils;

public class TransmissionBuilder {
	private Transmission transmission;
	private int id;
	private ArrayList<Atom> list;
	
	/**
	 * Unique constructor of a transmission builder
	 */
	public TransmissionBuilder(){
		list = new ArrayList<Atom>();
		id = 0;
	}
	
	/**
	 * Base creation method of an object of type Transmission
	 * @param opcode
	 * @param from
	 * @param to
	 */
	public void newTransmission(int opcode, String from, String to){
		transmission = new Transmission(id, opcode, from, to);
	}
	
	/**
	 * Method used to add an Atom to the current Transmission being built
	 * @param field
	 * @param type
	 * @param value
	 */
	public void newAtom(String field, String type, String value){
		Atom atom = new Atom(field, type, value);
		list.add(atom);
	}
	
	/**
	 * Adds a message to the transmission
	 * @param message
	 */
	public void addMessage(Message message){
		this.newAtom("Serialized", "Message", new String(SerializationUtils.serialize(message)));
	}
	
	/**
	 * Returns a full Transmission object
	 * @return
	 */
	public Transmission getTransmission(){
		transmission.add_atoms(list);
		Transmission ret = transmission;
		transmission = null;
		list.clear();
		id++;
		return ret;
	}

}
