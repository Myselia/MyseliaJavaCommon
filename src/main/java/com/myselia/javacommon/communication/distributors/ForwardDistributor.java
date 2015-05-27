package com.myselia.javacommon.communication.distributors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.myselia.javacommon.communication.Addressable;
import com.myselia.javacommon.communication.MailService;
import com.myselia.javacommon.communication.structures.MailBox;
import com.myselia.javacommon.communication.units.Transmission;
import com.myselia.javacommon.constants.opcode.ComponentType;
import com.myselia.javacommon.constants.opcode.OpcodeAccessor;
import com.myselia.javacommon.exceptions.MyceliaOpcodeException;

public class ForwardDistributor implements Distributor {

	private HashMap<String, CopyOnWriteArrayList<Addressable>> map;
	private CopyOnWriteArrayList<Addressable> systemList;
	
	public ForwardDistributor(HashMap<String, CopyOnWriteArrayList<Addressable>> map, CopyOnWriteArrayList<Addressable> systemList) {
		this.map = map;
		this.systemList = systemList;
	}

	@Override
	public void tick() {
		//CopyOnWriteArrayList<Addressable> copylist = systemList;
		for (Addressable subSystem : systemList) {
			MailBox<Transmission> mail = null;
			try {
				mail = (MailBox<Transmission>) subSystem.getMailBox();
			} catch (Exception e) {
				System.err.println("HERE LIES ERROR");
				e.printStackTrace();
			}
			if (mail != null && mail.getOutQueueSize() > 0) {
				Transmission t = mail.getFromOutQueue();
				if (t != null) {
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
				localOpcode = OpcodeAccessor.getComponentOpcode(fromOpcode);
			else 
				localOpcode = OpcodeAccessor.getLocalOpcode(fromOpcode);
			if (map.containsKey(localOpcode)) {
				// This is a packet that needs to be forwarded
				Iterator<Addressable> subsystemsToForwardTo = map.get(localOpcode).iterator();
				while (subsystemsToForwardTo.hasNext()) {
					Addressable subSystem = subsystemsToForwardTo.next();
					MailBox<Transmission> subSystemMailbox = (MailBox<Transmission>) subSystem.getMailBox();
					System.out.println("~!!!!!!!!!!!!!!FORWARD!!!!!!!!!!!!!!~");
					subSystemMailbox.putInInQueue(trans);
				}
			}
		} catch (MyceliaOpcodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
