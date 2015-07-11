package com.myselia.javacommon.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import com.google.gson.Gson;
import com.myselia.javacommon.communication.mail.Addressable;
import com.myselia.javacommon.communication.mail.MailBox;
import com.myselia.javacommon.communication.mail.MailService;
import com.myselia.javacommon.communication.units.Atom;
import com.myselia.javacommon.communication.units.Transmission;
import com.myselia.javacommon.communication.units.TransmissionBuilder;
import com.myselia.javacommon.constants.opcode.ActionType;
import com.myselia.javacommon.constants.opcode.ComponentType;
import com.myselia.javacommon.constants.opcode.OpcodeBroker;
import com.myselia.javacommon.constants.opcode.Operation;
import com.myselia.javacommon.constants.opcode.operations.StemOperation;
	
public class ComponentCommunicator implements Runnable, Addressable{
	
	private static MailBox<Transmission> networkMailbox;
	private static MailBox<Transmission> systemMailbox;
	
	private static ComponentType componentType;
	private BroadcastListener bl;
	private Gson jsonInterpreter;
	
	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	
	private boolean CONNECTED = false;
	private String inputToken = "";
	private String outputToken = "";
	
	static {
		networkMailbox = new MailBox<Transmission>();
		systemMailbox = new MailBox<Transmission>();
	}

	public ComponentCommunicator(ComponentType componentType) {
		ComponentCommunicator.componentType = componentType;
		bl = new BroadcastListener(componentType);
		jsonInterpreter = new Gson();
	}
	
	/**
	 * ticks the communication manager empties out queue towards the stem fills
	 * up in queue
	 * 
	 * @throws IOException
	 */
	private void tick() {
		if (!CONNECTED) {
			bl.startSeeking();
			Transmission trans = bl.listen(512);
			if (bl.transmissionReady()) {
				bl.endSeeking();
				connect(trans);
			}
		} else {
			try {

				while (CONNECTED) {

					//Receive Packets
					if (input.ready()) {
						if ((inputToken = input.readLine()) != null) {
							System.out.println("GOT STUFF FROM UPHILL");
							networkMailbox.enqueueIn(jsonInterpreter.fromJson(inputToken, Transmission.class));
						}
					}

					// Send Packets
					if (!output.checkError()) {
						if (networkMailbox.getOutSize() > 0) {
							outputToken = jsonInterpreter.toJson(networkMailbox.dequeueOut());
							output.println(outputToken);
						}
					} else {
						socket.close();
						throw new IOException();
					}
					
					//Redirect system/network mailbox contents
					handleMailBoxPair();

				}

			} catch (IOException e) {
				System.err.println("error in the sending or recieving of transmission. Seeking Stems...");
				CONNECTED = false;
				socket = null;
			} 
		}
	}

	public void handleMailBoxPair(){
		//re-routing network in to system out
		if(networkMailbox.getInSize() > 0){
			System.out.println("NEW STUFF IN NETWORK MAILBOX");
			Transmission trans = networkMailbox.dequeueIn();
			System.out.println(jsonInterpreter.toJson(trans));
			systemMailbox.enqueueOut(trans);
			MailService.notify(this);
		}
		
		//re-routing system in to network out
		if(systemMailbox.getInSize() > 0){
			networkMailbox.enqueueOut(systemMailbox.dequeueIn());
		}
	}
	

	@Override
	public void run() {
		try{
			while(!Thread.currentThread().isInterrupted()){
				tick();
			}
		} catch (Exception e){
			e.printStackTrace();
			System.err.println("communication thread error");
			endCommunication();
		}
	}
	
	/**
	 * Instantiates a communication socket to the values that it has received
	 * @param trans
	 */
	private void connect(Transmission trans){
		
		try {
			//Initialize needed info
			ArrayList<Atom> list = trans.get_atoms();
			String hostname = list.get(0).get_value();
			int port = Integer.parseInt(list.get(1).get_value());
			
			//Attempt connection
			System.out.print("Trying to connect to host at: " + hostname + ":" + port + "... ");
			socket = new Socket(hostname, port);
			CONNECTED = true; /* CONNECTION ESTABLISHED*/
			output = new PrintWriter(socket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("done");
			sendSetupPacket();
			
		} catch (IOException e){
			System.err.println("Socket creation error: Cant connect to endpoint");
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Socket creation error: Cant find setup packet");
		}
	}
	
	/**
	 * Returns the connection state of the Lens
	 * @return boolean CONNECTED
	 */
	public boolean isConnected(){
		return CONNECTED;
	}
	
	/**
	 * 
	 */
	private void endCommunication(){
		try{
			output.flush();
			output.close();
			input.close();
			socket.close();
		} catch (Exception e){
			System.err.println("error ending communication");
		}
	}
	
	/**
	 * sends a setup packet containing 
	 */
	private void sendSetupPacket(){
		System.out.print("Setting up setup packet ... ");
		try {
			TransmissionBuilder tb = new TransmissionBuilder();
			String from = OpcodeBroker.make(componentType, null, ActionType.SETUP, null);
			String to = OpcodeBroker.make(ComponentType.STEM, null, ActionType.SETUP, StemOperation.SETUP);
			tb.newTransmission(from, to);
			String[] ifaceInfo = getInterfaceInformation();
			
			tb.addAtom("ip", "String", ifaceInfo[1]);
			tb.addAtom("type", "String", componentType.toString());
			tb.addAtom("mac", "String", ifaceInfo[0]);
			tb.addAtom("hashID", "String",  Integer.toString((ifaceInfo[1] + ifaceInfo[0]).hashCode()));
		
			Transmission trans = tb.getTransmission();
			output.println(jsonInterpreter.toJson(trans));
			
			System.out.println(" ... done");
		} catch (Exception e) {
			System.err.println("setup packet error");
			//e.printStackTrace();
		}
	}
	
	/**
	 * Test packet building method
	 */
	/*
	private void sendTestPacket(){
		try {
			TransmissionBuilder tb = new TransmissionBuilder();
			tb.newTransmission("LENS", "STEM");
			tb.addAtom("count", "String", "5");

			Transmission trans = tb.getTransmission();
			networkMailbox.enqueueOut(trans);
			MailService.notify(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	/**
	 * Method that returns the MAC address of the InetAddress in question
	 * @param ia
	 * @return String (MAC)
	 * @throws SocketException
	 */
	/*
	private String getMac(InetAddress ia) throws SocketException {
		NetworkInterface network = NetworkInterface.getByInetAddress(ia);
		byte[] mac = network.getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
		}
		return sb.toString();
	}
	*/

	/**
	 * 0	:	MAC
	 * 1	:	IP
	 */
	private String[] getInterfaceInformation() {
		String[] info = new String[2];
	    try {
	        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	        while (interfaces.hasMoreElements()) {
	            NetworkInterface iface = interfaces.nextElement();
	            // filters out 127.0.0.1 and inactive interfaces
	            if (iface.isLoopback() || !iface.isUp())
	                continue;

	            info[0] = new String(iface.getHardwareAddress());
	            Enumeration<InetAddress> addresses = iface.getInetAddresses();
	            while(addresses.hasMoreElements()) {
	                InetAddress addr = addresses.nextElement();
	                info[1] = addr.getHostAddress();
	            }
	        }
	    } catch (SocketException e) {
	        throw new RuntimeException(e);
	    }
	    
	    return info;
	}

	@Override
	public void in(Transmission trans) {
		systemMailbox.enqueueIn(trans);
	}
	
	@Override
	public Transmission out(){
		return systemMailbox.dequeueOut();
	}

}