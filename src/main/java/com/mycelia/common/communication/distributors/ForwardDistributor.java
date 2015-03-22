package com.mycelia.common.communication.distributors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.mycelia.common.communication.Addressable;
import com.mycelia.common.communication.MailService;
import com.mycelia.common.communication.structures.MailBox;
import com.mycelia.common.communication.units.Transmission;
import com.mycelia.common.constants.opcode.ComponentType;
import com.mycelia.common.constants.opcode.OpcodeAccessor;
import com.mycelia.common.exceptions.MyceliaOpcodeException;

public class ForwardDistributor implements Distributor {

	private HashMap<String, ArrayList<Addressable>> map;
	private ArrayList<Addressable> systemList;
	
	public ForwardDistributor(HashMap<String, ArrayList<Addressable>> map, ArrayList<Addressable> systemList) {
		this.map = map;
		this.systemList = systemList;
	}

	@Override
	public void tick() {
		ArrayList<Addressable> copylist = systemList;
		for (Addressable subSystem : copylist) {
			MailBox<Transmission> mail = (MailBox<Transmission>) subSystem.getMailBox();
			if (mail.getOutQueueSize() > 0) {
				//System.out.println("found a mailbox with over 0 transmissions : " + subSystem.getClass().toString());
				Transmission t = mail.getFromOutQueue();
				if (t != null){
					redirect(t);
				}
			}
		}
		
	}

	private void redirect(Transmission trans) {
		String fromOpcode = trans.get_header().get_from();
		String localOpcode;
		try {
			if (MailService.getComponentType() == ComponentType.STEM)
				localOpcode = OpcodeAccessor.getLocalOpcode(fromOpcode);
			else 
				localOpcode = OpcodeAccessor.getComponentOpcode(fromOpcode);
			//System.out.print("OPCODE: " + localOpcode);
			if (map.containsKey(localOpcode)) {
				// This is a packet that needs to be forwarded
				Iterator<Addressable> subsystemsToForwardTo = map.get(localOpcode).iterator();
				while (subsystemsToForwardTo.hasNext()) {
					Addressable subSystem = subsystemsToForwardTo.next();
					MailBox<Transmission> subSystemMailbox = (MailBox<Transmission>) subSystem.getMailBox();
					//System.out.println(" ... redirected to : " + subSystem.getClass());
					subSystemMailbox.putInInQueue(trans);
				}
			}
		} catch (MyceliaOpcodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
