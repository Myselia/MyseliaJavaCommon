package com.mycelia.common.communication;

import java.util.ArrayList;
import java.util.HashMap;

import com.mycelia.common.communication.distributors.Distributor;
import com.mycelia.common.communication.distributors.DistributorFactory;
import com.mycelia.common.communication.distributors.DistributorType;

public class MailService implements Runnable {
    private static HashMap<String, ArrayList<Addressable>> map;
    private static ArrayList<Addressable> systemList;
    private Distributor distributor;
    private DistributorType distributorType;
    
    static{
        map = new HashMap<String, ArrayList<Addressable>>();
        systemList = new ArrayList<Addressable>();
    }
     
    /**
     * Mail service constructor that sets a distributor type
     * @param distributorType
     */
    public MailService(DistributorType distributorType) {
    	this.distributorType = distributorType;
        
        initialize_distributor();    
    }
    
    /**
     * Call to register to packet updates containing a particular field.
     * @param opcode	The field to listen for updates to
     * @param subsystem	The subsystem registering (typically 'this')
     */
    public static void register(String opcode, Addressable subsystem) {
    	System.out.println("registered addressable : " + subsystem.getClass().toString() + " to OPCODE: " + opcode);
    	registerAddressable(subsystem);
    	ArrayList<Addressable> a;
    	//First time field is accessed 
    	if (map.get(opcode) == null) {
    		a = new ArrayList<Addressable>();
    		map.put(opcode, a);
    	} else {
    		a = map.get(opcode);
    	}
    	a.add(subsystem);
    }
    
    /**
     * Registers the subsystem to the system list
     * @param subsystem
     */
    public static void registerAddressable(Addressable subsystem) {
    	if(!isRegistered(subsystem)){
    		systemList.add(subsystem);
    	}
    }
    
    /**
     * Unregisters the component from the particular field
     * @param field
     * @param subsystem
     */
    public void unregister(String field, Addressable subsystem) {
		ArrayList<Addressable> subscriberList;
		if ((map.get(field) != null)) {
			subscriberList = map.get(field);
			if (subscriberList.contains(subsystem)) {
				int subListIndex = subscriberList.indexOf(subsystem);
				subscriberList.remove(subListIndex);
			}
		}
    }
    
    /**
     * Returns true if the addressable unit is already registered
     * @param alpha
     * @return
     */
	public static boolean isRegistered(Addressable alpha){
    	if (systemList.contains(alpha)){
    		return true;
    	} else {
    		return false;
    	}
    }
    
	/**
	 * getter for the mappings of the fields to addressable units
	 * @return
	 */
	public HashMap<String, ArrayList<Addressable>> getMap() {
		return map;
	}

	/**
	 * swaps the distributor for a different one based on 
	 * @param distributorType
	 */
	public void swapDistributor(DistributorType distributorType) {
		if(this.distributorType != distributorType){
			this.distributorType = distributorType;
			initialize_distributor();
		}
	}
    
	/**
	 * initializes the distributor to whatever type was preset
	 */
    private void initialize_distributor(){
        distributor = DistributorFactory.makeDistributor(distributorType, map, systemList);
    }

    /**
     * run method that loops through the distributor's tick
     */
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			distributor.tick();
		}
	}
}