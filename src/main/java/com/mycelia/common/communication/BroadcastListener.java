package com.mycelia.common.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.mycelia.common.communication.structures.Atom;
import com.mycelia.common.communication.structures.Transmission;

public class BroadcastListener {

	private static int port = 42068;
	
	private DatagramSocket socket;
	private byte[] buffer;
	
	private boolean CORRECT = false;
	private boolean SEEKING = true;
	
	public BroadcastListener() {
	}
	
	
	/**
	 * Listens to all transmissions and checks if they're addressed to its kind
	 * @param length
	 * @return
	 */
	public Transmission listen(int length){
		Gson g = new Gson();
	    buffer = new byte[length];
	    setupSocket();
	    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	    Transmission trans = new Transmission();
	    String make = "";
		
		while (!CORRECT) {
			try {
				while (SEEKING) {
					System.out.println("Waiting for setup packet");
					socket.receive(packet);
					make = new String(buffer);
					System.out.println("RECV Broadcast: " + make);
					make = make.substring(0, make.lastIndexOf('}') + 1);
					break;
				}
			} catch (IOException e) {
				System.err.println("BroadcastListener: IOException");
				e.printStackTrace();
			}
		
	    	trans = g.fromJson(make, Transmission.class);
	    	ArrayList<Atom> list = trans.get_atoms();
	    	if(list.size() == 3){
				Atom a = list.get(2);
				if (a.get_type().equals("String") && a.get_field().equals("type")) {
					String check = a.get_value();
					if (check.equals("LENS")) {
						CORRECT = true;
						packet = null;
						socket.close();
						break;
					}
	    		}
	    	}
	    	
	    }
	    
	    return trans;
		
	}
	
	public void startSeeking() {
		SEEKING = true;
		System.out.println("Starting bcast session");
	}
	
	public void endSeeking() {
		SEEKING = false;
		CORRECT = false;
		System.out.println("End of bcast session");
	}
	
	public boolean transmissionReady() {
		return CORRECT;
	}
	
	private void setupSocket() {
		try{
			this.socket = new DatagramSocket(port);
			socket.setBroadcast(true);
		}catch(IOException e){
			System.err.println("BroadcastListener: Connection failed.");
			e.printStackTrace();
		}
	}
}
