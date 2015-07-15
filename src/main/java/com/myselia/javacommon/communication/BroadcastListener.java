package com.myselia.javacommon.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.myselia.javacommon.communication.units.Atom;
import com.myselia.javacommon.communication.units.Transmission;
import com.myselia.javacommon.constants.opcode.ComponentType;

public class BroadcastListener {

	private static Gson jsonInterpreter = new Gson();
	private static int externalListenPort = 42068;
	private static int internalListenPort = 42065;
	private ComponentCommunicator componentCommunicator;
	private DatagramSocket socket;
	
	private boolean CORRECT = false;
	private boolean SEEKING = true;
	
	public BroadcastListener(ComponentCommunicator componentCommunicator) {
		this.componentCommunicator = componentCommunicator;
	}
	
	/**
	 * Listens to all transmissions and checks if they're addressed to its kind
	 * @param length
	 * @return
	 */
	public void listen(int length) {
		if(componentCommunicator.getComponentCertificate().getComponentType() == ComponentType.SANDBOXSLAVE){
			setupSocket(false);
		} else {
			setupSocket(true);
		}
	    
	    byte[] buffer;
	    DatagramPacket packet;
	    String make = "";
	    Transmission broadcastTransmission = null;
		
		while (!CORRECT) {
			try {
				while (SEEKING) {
					System.out.println("Waiting for seek packet");
					buffer = new byte[length];
					packet = new DatagramPacket(buffer, buffer.length);
					socket.receive(packet);
					make = new String(buffer);
					System.out.println("RECV Broadcast: " + make);
					make = make.substring(0, make.lastIndexOf('}') + 1);
					
					broadcastTransmission = jsonInterpreter.fromJson(make, Transmission.class);
					break;
				}
			} catch (IOException e) {
				System.err.println("BroadcastListener: IOException");
				e.printStackTrace();
			}
			
			System.out.println("CHECKING!!!!!!!!!!!!");

			//Checks to see if the type of component the broadcast transmission is targeting is relevant to this component
	    	ArrayList<Atom> list = broadcastTransmission.get_atoms();
	    	if(list.size() == 3){
				Atom a = list.get(2);
				if (a.get_type().equals("String") && a.get_field().equals("type")) {
					String check = a.get_value();
					if (check.equals(componentCommunicator.getComponentCertificate().getComponentType().name())) {
						CORRECT = true;
						packet = null;
						socket.close();
						break;
					}
	    		}
	    	}
	    }
		
		if (CORRECT) {
			endSeeking();
			componentCommunicator.connect(broadcastTransmission);
		}
	}
	
	public void startSeeking() {
		SEEKING = true;
		listen(1024);
		System.out.println("Starting bcast session");
	}
	
	public void endSeeking() {
		SEEKING = false;
		CORRECT = false;
		System.out.println("End of bcast session");
	}
	
	private void setupSocket(boolean external) {
		try{
			if(external){
				this.socket = new DatagramSocket(externalListenPort);
			} else {
				this.socket = new DatagramSocket(internalListenPort);
			}
			socket.setBroadcast(true);
		}catch(IOException e){
			System.err.println("BroadcastListener: Connection failed.");
			e.printStackTrace();
		}
	}
}
