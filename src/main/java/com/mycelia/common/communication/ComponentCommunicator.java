package com.mycelia.common.communication;

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
import java.util.LinkedList;

import com.google.gson.Gson;
import com.mycelia.common.communication.structures.MailBox;
import com.mycelia.common.communication.tools.BroadcastListener;
import com.mycelia.common.communication.units.Atom;
import com.mycelia.common.communication.units.Transmission;
import com.mycelia.common.communication.units.TransmissionBuilder;
import com.mycelia.common.constants.opcode.ActionType;
import com.mycelia.common.constants.opcode.ComponentType;
import com.mycelia.common.constants.opcode.OpcodeAccessor;
import com.mycelia.common.constants.opcode.Operation;
import com.mycelia.common.constants.opcode.operations.StemOperation;
	
public class ComponentCommunicator implements Runnable, Addressable{
	
	private static MailBox<Transmission> networkMailbox;
	private static MailBox<Transmission> systemMailbox;
	private static ComponentType componentType;
	private static Operation componentOp;
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
		componentOp = OpcodeAccessor.getOpcodes(componentType);
		MailService.registerAddressable(this);
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
					//sendTestPacket();
					if (input.ready()) {
						if ((inputToken = input.readLine()) != null) {
							networkMailbox.putInInQueue(jsonInterpreter.fromJson(inputToken, Transmission.class));
						}
					}

					// Send Packets
					if (!output.checkError()) {
						if (networkMailbox.getOutQueueSize() > 0) {
							outputToken = jsonInterpreter.toJson(networkMailbox.getFromOutQueue());
							output.println(outputToken);
						}
					} else {
						socket.close();
						throw new IOException();
					}
					
					handle_mailboxes();
				}

			} catch (IOException e) {
				System.err.println("error in the sending or recieving of transmission. Seeking Stems...");
				CONNECTED = false;
				socket = null;
			} 
		}
	}
	
	private void handle_mailboxes() {
		/*LinkedList<Transmission> bob = networkMailbox.getAllFromInQueue();
		System.out.println("NETWORK MAILBOX PACKAGE GRAB SIZE : " + bob.size());*/
		systemMailbox.putAllInOutQueue(networkMailbox.getAllFromInQueue());
		networkMailbox.putAllInInQueue(systemMailbox.getAllFromOutQueue());
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
			String from = OpcodeAccessor.make(componentType, ActionType.SETUP, componentOp.SEND_SETUP);
			String to = OpcodeAccessor.make(ComponentType.STEM, ActionType.SETUP, StemOperation.ACCEPT_SETUP);
			tb.newTransmission(from, to);
			InetAddress ip = Inet4Address.getLocalHost();
			System.out.print(ip);
			
			tb.addAtom("ip", "String", ip.toString());
			tb.addAtom("type", "String", componentType.toString());
			tb.addAtom("mac", "String", getMac(ip));
			tb.addAtom("hashID", "String", Integer.toString((ip + getMac(ip)).hashCode()));
		
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
	private void sendTestPacket(){
		try {
			TransmissionBuilder tb = new TransmissionBuilder();
			tb.newTransmission("LENS", "STEM");
			tb.addAtom("count", "String", "5");

			Transmission trans = tb.getTransmission();
			networkMailbox.putInOutQueue(trans);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that returns the MAC address of the InetAddress in question
	 * @param ia
	 * @return String (MAC)
	 * @throws SocketException
	 */
	private String getMac(InetAddress ia) throws SocketException {
		NetworkInterface network = NetworkInterface.getByInetAddress(ia);
		byte[] mac = network.getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
		}
		return sb.toString();
	}

	@Override
	public MailBox<Transmission> getMailBox() {
		return systemMailbox;
	}

}