package com.mycelia.common.communication.structures;

import java.util.ArrayList;

public class TransmissionBuilder {
	private Transmission transmission;
	private int id;
	private ArrayList<Atom> list;
	
	public TransmissionBuilder(){
		list = new ArrayList<Atom>();
		id = 0;
	}
	
	public void newTransmission(int opcode, String from, String to){
		transmission = new Transmission(id, opcode, from, to);
	}
	
	public void newAtom(String field, String type, String value){
		Atom atom = new Atom(field, type, value);
		list.add(atom);
	}
	
	public Transmission getTransmission(){
		transmission.add_atoms(list);
		Transmission ret = transmission;
		transmission = null;
		list.clear();
		id++;
		return ret;
	}

}
