package com.myselia.javacommon.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

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
import com.myselia.javacommon.constants.opcode.operations.StemOperation;
import com.myselia.javacommon.topology.ComponentCertificate;
	
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
	
	public ComponentCertificate componentCertificate;
	
	static {
		networkMailbox = new MailBox<Transmission>();
		systemMailbox = new MailBox<Transmission>();
	}

	public ComponentCommunicator(ComponentType componentType) {
		ComponentCommunicator.componentType = componentType;
		bl = new BroadcastListener(componentType);
		jsonInterpreter = new Gson();
		componentCertificate = new ComponentCertificate(componentType);
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
	
	//TODO: UUID FROM STEM
	private void sendSetupPacket(){
		System.out.print("Setting up setup packet ... ");
		try {
			TransmissionBuilder tb = new TransmissionBuilder();
			String from = OpcodeBroker.make(componentCertificate.getComponentType(), componentCertificate.getUUID(), ActionType.SETUP, null);
			String to = OpcodeBroker.make(ComponentType.STEM, null, ActionType.SETUP, StemOperation.SETUP);
			tb.newTransmission(from, to);

			tb.addAtom("componentCerficate", "ComponentCertificate", jsonInterpreter.toJson(componentCertificate));
		
			Transmission trans = tb.getTransmission();
			output.println(jsonInterpreter.toJson(trans));
			
			System.out.println(" ... done");
		} catch (Exception e) {
			System.err.println("setup packet error");
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
			networkMailbox.enqueueOut(trans);
			MailService.notify(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
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