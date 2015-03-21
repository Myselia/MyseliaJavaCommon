package com.mycelia.common.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.mycelia.common.communication.distributors.Distributor;
import com.mycelia.common.communication.distributors.DistributorFactory;
import com.mycelia.common.communication.distributors.DistributorType;
import com.mycelia.common.communication.structures.MailBox;
import com.mycelia.common.communication.units.Atom;
import com.mycelia.common.communication.units.Transmission;

public class MailService implements Runnable {
    private static HashMap<String, ArrayList<Addressable>> map;
    private MailBox<Transmission> mainMailbo;
    private ArrayList<Addressable> systemList;
    private Distributor distributor;
    private DistributorType distributorType;
     
    public MailService(DistributorType distributorType) {
    	this.distributorType = distributorType;
        map = new HashMap<String, ArrayList<Addressable>>();
        
        initialize_distributor();    
    }
    
    /**
     * Call to register to packet updates containing a particular field.
     * @param field	The field to listen for updates to
     * @param subsystem	The subsystem registering (typically 'this')
     */
    public void register(String field, Addressable subsystem) {
    	ArrayList<Addressable> a;
    	//First time field is accessed 
    	if (map.get(field) == null) {
    		a = new ArrayList<Addressable>();
    		map.put(field, a);
    	} else {
    		a = map.get(field);
    	}
    	a.add(subsystem);
    }
    
    public void register(Addressable subsystem) {
    	systemList.add(subsystem);
    }
    
    /**
     * De-registers the component from the particular field
     * @param field
     * @param subsystem
     */
    public void deRegister(String field, Addressable subsystem) {
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
	public boolean isRegistered(Addressable alpha){
    	if (systemList.contains(alpha)){
    		return true;
    	} else {
    		return false;
    	}
    }
    
	public HashMap<String, ArrayList<Addressable>> getMap() {
		return map;
	}

	/**
	 * 
	 * @param distributorType
	 */
	public void swapDistributor(DistributorType distributorType) {
		this.distributor = DistributorFactory.makeDistributor(distributorType, map, systemList);
	}

	//At this point a packet is going to be pulled
	private void sendMail() {
		//We get a transmission from the in queue of COomponent communicator
		Transmission t = ComponentCommunicator.receive();
		//Now we check the atoms
		Iterator<Atom> atomI = t.get_atoms().iterator();
		while (atomI.hasNext()) {
			Atom a = atomI.next();
			
			forwardByAtom(a.get_field(), t);
		}
	}
	
	private void forwardByAtom(String s, Transmission t) {
		ArrayList<Addressable> listenerList = map.get(s);
		Iterator<Addressable> it = listenerList.iterator();
		
		while (it.hasNext()) {
			Addressable subsystem = it.next();
			subsystem.getMailBox().putInInQueue(t);
		}
	}
    
    private void initialize_distributor(){
        distributor = DistributorFactory.makeDistributor(distributorType, map, systemList);
    }

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			distributor.tick();
		}
	}
}