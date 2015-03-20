package com.mycelia.common.communication.units;

import java.util.ArrayList;

public class Transmission {

	private Header header;
	private ArrayList<Atom> atoms = new ArrayList<Atom>();

	public Transmission() {
	}

	public Transmission(int id, int opcode, String from, String to) {
		this.header = new Header(id, opcode, from, to);
	}

	public void add_header(Header header) {
		this.header = header;
	}

	public void add_atoms(ArrayList<Atom> list) {
		atoms.addAll(list);
	}

	public void add_atom(Atom atom) {
		atoms.add(atom);
	}

	public Header get_header() {
		return header;
	}

	public ArrayList<Atom> get_atoms() {
		return atoms;
	}

	public boolean isValid() {
		if (header != null && atoms.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void printTransmission() {
		System.out.print("id:" + header.get_id() + " ");
		System.out.print("opcode:" + header.get_opcode() + " ");
		System.out.print("from:" + header.get_from() + " ");
		System.out.print("to:" + header.get_to() + " ");
		System.out.println("atoms:" + atoms.size());
	}

}
