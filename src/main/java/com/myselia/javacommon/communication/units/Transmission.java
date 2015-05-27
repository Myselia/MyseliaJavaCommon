package com.myselia.javacommon.communication.units;

import java.util.ArrayList;

public class Transmission {

	private Header header;
	private ArrayList<Atom> atoms = new ArrayList<Atom>();

	/**
	 * Default constructor of Transmission
	 */
	public Transmission() {
	}

	/**
	 * Complex constructor that forwards everything to the header
	 * @param id
	 * @param from
	 * @param to
	 */
	public Transmission(int id, String from, String to) {
		this.header = new Header(id, from, to);
	}

	/**
	 * Header changing function
	 * @param header
	 */
	public void add_header(Header header) {
		this.header = header;
	}

	/**
	 * Adding a list of Atoms to the Transmission
	 * @param list
	 */
	public void add_atoms(ArrayList<Atom> list) {
		atoms.addAll(list);
	}

	/**
	 * Gets the Header of the transmission
	 * @return HEADER
	 */
	public Header get_header() {
		return header;
	}

	/**
	 * Returns all the Atoms in the Transmission
	 * @return
	 */
	public ArrayList<Atom> get_atoms() {
		return atoms;
	}

	/**
	 * Checks the validity of the Transmission
	 * @return boolean
	 */
	public boolean isValid() {
		if (header != null && atoms.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Prints a string representation of the transmission
	 */
	public void printTransmission() {
		System.out.print("id:" + header.get_id() + " ");
		System.out.print("from:" + header.get_from() + " ");
		System.out.print("to:" + header.get_to() + " ");
		System.out.println("atoms:" + atoms.size());
	}

}
