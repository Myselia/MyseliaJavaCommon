package com.mycelia.common.communication.structures;

import java.util.ArrayList;

public class TransmissionBuilder {
	private static Transmission transmission;
	private static int id;
	private static ArrayList<Atom> list;
	
	static {
		list = new ArrayList<Atom>();
		id = 0;
	}
	
	public static void newTransmission(int opcode, String from, String to){
		transmission = new Transmission(id, opcode, from, to);
	}
	
	public static void newAtom(String field, String type, String value){
		Atom atom = new Atom(field, type, value);
		list.add(atom);
	}
	
	public static Transmission getTransmission(){
		transmission.add_atoms(list);
		Transmission ret = transmission;
		transmission = null;
		list.clear();
		id++;
		return ret;
	}

}
