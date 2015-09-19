package com.myselia.javacommon.topology;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class MyseliaRoutingTable {
	
	private String localMUUID = null;
	private ConcurrentHashMap<String, String> table;
	
	public MyseliaRoutingTable() {
		table = new ConcurrentHashMap<String, String>();
	}
	
	public MyseliaRoutingTable(MyseliaUUID localMUUID){
		this.localMUUID = localMUUID.toString();
		table = new ConcurrentHashMap<String, String>();
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
	public void removeDestination(String destination){
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
		
		// If the table is of size nil, then all links have been severed and the
		// component that sent this routing table no longer serves as a relay for anything

		if (src.getTable().isEmpty()) {
			// Sever all hop links
			Iterator<String> it = table.keySet().iterator();
			while (it.hasNext()) {
				String dest = it.next();
				if (!dest.equals(table.get(dest))) {
					//TODO It might be better to use it.remove() here. As a temporary fix of the 
					//ConcurrentModificationException, the route table was changed to ConcurrentHashMap.
					removeDestination(dest);
				}
			}
		} else {
			// Update all hop links
			Iterator<String> it = src.getTable().keySet().iterator();
			while (it.hasNext()) {
				String dest = it.next();
				setNext(dest, src.getLocalMUUID().toString());
			}
		}
		
	}
	
	public ConcurrentHashMap<String, String> getTable() {
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
			data += "\tHave a destination: " + s + "\n";
			data += "\t\tNext hop is -> " + table.get(s) + "\n";
		}

		return data;
	}


}
