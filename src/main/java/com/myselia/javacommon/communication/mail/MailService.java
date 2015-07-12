package com.myselia.javacommon.communication.mail;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.myselia.javacommon.communication.units.Transmission;
import com.myselia.javacommon.constants.opcode.ComponentType;
import com.myselia.javacommon.constants.opcode.OpcodeBroker;
import com.myselia.javacommon.exceptions.MyseliaOpcodeException;

public class MailService implements Runnable{
	private static Gson json = new Gson();
    private static HashMap<String, CopyOnWriteArrayList<Addressable>> map;
    private static CopyOnWriteArrayList<Addressable> systemList;
    private static ComponentType componentType;
   
    
    static {
        map = new HashMap<String, CopyOnWriteArrayList<Addressable>>();
        systemList = new CopyOnWriteArrayList<Addressable>();
    }
     
    /**
     * Mail service constructor that sets a distributor type
     * @param distributorType
     */
    public MailService(ComponentType componentType) {
    	MailService.componentType = componentType; 
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
    		System.out.println("MailService : registered addressable : " + subsystem.getClass().toString());
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
	 * getter for the component type that instantiated this MailService
	 * @return
	 */
	public static ComponentType getComponentType() {
		return componentType;
	}
	
	/**
	 * Notifies the MailService that there is a transmission in a certain mailbox
	 * @param addressable
	 */
	public static void notify(Addressable addressable){
		redirect(addressable.out());
	}
	
	private static void redirect(Transmission trans) {
		String opcode = trans.get_header().get_to();
		String checking;
		
		if(MailService.getComponentType() == ComponentType.STEM){
			checking = OpcodeBroker.getComponentActionOpcode(opcode);
		} else {
			checking = OpcodeBroker.getActionOperationOpcode(opcode);
		}
		
		if (map.containsKey(checking)) {
			// This is a packet that needs to be forwarded
			Iterator<Addressable> subsystemsToForwardTo = map.get(checking).iterator();
			while (subsystemsToForwardTo.hasNext()) {
				Addressable subSystem = subsystemsToForwardTo.next();
				subSystem.in(trans);
				System.out.println("Mail Service : redirected to : " + subSystem.getClass());
			}
		} else {
			System.err.println("Mail Service : dropped transmission : " + json.toJson(trans.get_header()));
		}
	
	}

	@Override
	public void run() {
		
	}

}