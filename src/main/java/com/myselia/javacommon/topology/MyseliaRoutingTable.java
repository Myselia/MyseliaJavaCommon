package com.myselia.javacommon.topology;

import java.util.HashMap;

public class MyseliaRoutingTable {
	
	HashMap<MyseliaUUID, MyseliaUUID> table = new HashMap<MyseliaUUID, MyseliaUUID>();
	 
	public MyseliaRoutingTable(){
		
	}
	
	/**
	 * Gets the UUID of the next hop to the end point where the transmission needs to go
	 * @param destination
	 * @return
	 */
	public MyseliaUUID getNext(MyseliaUUID destination){
		return table.get(destination);
	}
	
	/**
	 * Sets the next hop to the destination
	 * Can be the destination itself
	 * @param destination
	 * @param hop
	 */
	public void setNext(MyseliaUUID destination, MyseliaUUID hop){
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

}
