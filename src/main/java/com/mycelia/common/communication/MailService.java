package com.mycelia.common.communication;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mycelia.common.communication.distributors.Distributor;
import com.mycelia.common.communication.distributors.DistributorFactory;
import com.mycelia.common.communication.distributors.DistributorType;
import com.mycelia.common.constants.opcode.ComponentType;

public class MailService implements Runnable {
    private static HashMap<String, CopyOnWriteArrayList<Addressable>> map;
    private static CopyOnWriteArrayList<Addressable> systemList;
    private static ComponentType componentType;
    private Distributor distributor;
    private DistributorType distributorType;
   
    
    static {
        map = new HashMap<String, CopyOnWriteArrayList<Addressable>>();
        systemList = new CopyOnWriteArrayList<Addressable>();
    }
     
    /**
     * Mail service constructor that sets a distributor type
     * @param distributorType
     */
    public MailService(DistributorType distributorType, ComponentType componentType) {
    	this.distributorType = distributorType;
    	MailService.componentType = componentType;
        
        initialize_distributor();    
    }
    
    /**
     * Call to register to packet updates containing a particular field.
     * @param opcode	The field to listen for updates to
     * @param subsystem	The subsystem registering (typically 'this')
     */
    public synchronized static void register(String opcode, Addressable subsystem) {
    	registerAddressable(subsystem);
    	CopyOnWriteArrayList<Addressable> a;
    	//First time field is accessed 
    	if (map.get(opcode) == null) {
    		a = new CopyOnWriteArrayList<Addressable>();
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
    public synchronized static void registerAddressable(Addressable subsystem) {
    	if(!isRegistered(subsystem)){
    		systemList.add(subsystem);
    		System.out.println("registered addressable : " + subsystem.getClass().toString());
    	}
    }
    
    /**
     * Unregisters the component from the particular field
     * @param field
     * @param subsystem
     */
    public void unregister(String field, Addressable subsystem) {
    	CopyOnWriteArrayList<Addressable> subscriberList;
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
	public HashMap<String, CopyOnWriteArrayList<Addressable>> getMap() {
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
	
	public static ComponentType getComponentType() {
		return componentType;
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