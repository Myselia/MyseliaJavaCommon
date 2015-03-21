package com.mycelia.common.communication.units;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable{
	/**
	 * SerialVersionID of the Message Class
	 */
	private static final long serialVersionUID = -7029455537541149909L;

	/**
	 * The recipient's id
	 */
	private String to;

	/**
	 * The different elements constituting the content of this message
	 */
	public List<Serializable> elements; //TODO: make non-public
	public List<String> element_types;  //TODO: make non-public

	/**
	 * Only constructor of a message object
	 * 
	 * @param to
	 */
	public Message(String to) {
		this.to = to;
	}

	/**
	 * returns the intended recipient of this message
	 * 
	 * @return
	 */
	public String getTo() {
		return to;
	}

	/**
	 * adds an element to the list of elements in this message
	 * @param serializable
	 */
	public void addElement(Serializable serializable) {
		element_types.add(serializable.getClass().toString());
		elements.add(serializable);
	}

	/**
	 * adds multiple elements to the list of elements in this message
	 * @param serializable
	 */
	public void addElementList(List<Serializable> serializable) {
		for(Serializable serial : serializable){
			this.addElement(serial);
		}
	}

	/**
	 * returns all the elements of this message
	 * @return
	 */
	public List<Serializable> getElements() {
		return elements;
	}
}
