package com.mycelia.common.communication.distributors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.mycelia.common.communication.Addressable;
import com.mycelia.common.communication.structures.MailBox;
import com.mycelia.common.communication.units.Transmission;
import com.mycelia.common.constants.opcode.OpcodeAccessor;
import com.mycelia.common.exceptions.MyceliaOpcodeException;

public class ForwardDistributor implements Distributor {

	private HashMap<String, ArrayList<Addressable>> map;
	private ArrayList<Addressable> systemList;
	private Iterator<Addressable> it;

	public ForwardDistributor(HashMap<String, ArrayList<Addressable>> map, ArrayList<Addressable> systemList) {
		this.map = map;
		this.systemList = systemList;
	}

	@Override
	public void tick() {
		it = systemList.iterator();
		while (it.hasNext()) {
			Addressable subSystem = it.next();
			MailBox<Transmission> mail = (MailBox<Transmission>) subSystem.getMailBox();
			if (mail.getOutQueueSize() > 0) {
				Transmission t = mail.getFromOutQueue();
				redirect(t);
			}
		}
	}

	private void redirect(Transmission trans) {
		String fromOpcode = trans.get_header().get_from();
		String localOpcode;
		try {
			localOpcode = OpcodeAccessor.getLocalOpcode(fromOpcode);

			if (map.containsKey(localOpcode)) {
				// This is a packet that needs to be forwarded
				Iterator<Addressable> subsystemsToForwardTo = map.get(localOpcode).iterator();
				while (subsystemsToForwardTo.hasNext()) {
					Addressable subSystem = subsystemsToForwardTo.next();
					MailBox<Transmission> subSystemMailbox = (MailBox<Transmission>) subSystem.getMailBox();
					subSystemMailbox.putInInQueue(trans);
				}
			}
		} catch (MyceliaOpcodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
