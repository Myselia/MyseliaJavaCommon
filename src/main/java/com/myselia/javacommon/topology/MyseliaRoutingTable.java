package com.myselia.javacommon.topology;

import java.util.HashMap;
import java.util.Iterator;

public class MyseliaRoutingTable {
	
	private String localMUUID = null;
	private HashMap<String, String> table;
	
	public MyseliaRoutingTable() {
		table = new HashMap<String, String>();
	}
	
	public MyseliaRoutingTable(MyseliaUUID localMUUID){
		this.localMUUID = localMUUID.toString();
		table = new HashMap<String, String>();
	}
	
	/**
	 * Gets the UUID of the next hop to the end point where the transmission needs to go
	 * @param destination
	 * @return
	 */
	public String getNext(String destination){
		return table.get(destination);
	}
	
	/**
	 * Sets the next hop to the destination
	 * Can be the destination itself
	 * @param destination
	 * @param hop
	 */
	public void setNext(String destination, String hop){
		
		System.out.println("\n[MyseliaRoutingTable ~ setNext]");
		System.out.println("\tDEST: " + destination);
		System.out.println("\t\t-> HOP: " + hop + "\n");
		
		if(table.get(destination) == null){
			table.put(destination, hop);
		}else {
			table.remove(destination);
			table.put(destination, hop);
		}
	}
	
	/**
	 * Removes a destination from the 
	 * @param destination
	 */
	public void removeDestination(MyseliaUUID destination){
		table.remove(destination);
	}
	
	public void updateTablePropagation(MyseliaRoutingTable src) {
		/*
		 * Daemon: D
		 * Sandbox Slave: S
		 * 
		 * On Stem: 
		 * 	(D, D) - Auto Register on Connect
		 * 
		 * On Daemon:
		 * 	(S, S) - Auto Register on Connect
		 * 
		 * Daemon to Stem Table Update:
		 * 	setNext(S, D)
		 */
		
		//TODO Update Integrity
		Iterator<String> it = src.getTable().keySet().iterator();
		while (it.hasNext()) {
			String dest = it.next();
			setNext(dest, src.getLocalMUUID().toString());
		}
		
	}
	
	public HashMap<String, String> getTable() {
		return table;
	}

	public String getLocalMUUID() {
		return localMUUID;
	}

	public void setLocalMUUID(String localMUUID) {
		this.localMUUID = localMUUID;
	}

	public String toString() {
		String data = "";

		data += "My Local MUUID is: " + localMUUID + "\n";

		for (String s : table.keySet()) {
			data += "\tHave a key: " + s + "\n";
			data += "\t\tWith value " + table.get(s) + "\n";
		}

		return data;
	}


}
