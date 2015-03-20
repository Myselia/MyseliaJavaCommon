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

import com.google.gson.Gson;
import com.mycelia.common.communication.structures.Atom;
import com.mycelia.common.communication.structures.Transmission;
import com.mycelia.common.communication.structures.TransmissionBuilder;
import com.mycelia.common.constants.ComponentType;

public class ComponentCommunicator  implements Runnable{
	
	private BroadcastListener bl;
	private static MailBox<Transmission> mb;
	private static ComponentType componentType;
	private Gson jsonInterpreter;
	
	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	
	private boolean CONNECTED = false;
	private String inputToken = "";
	private String outputToken = "";
	
	static {
		mb = new MailBox<Transmission>();
	}
	
	public ComponentCommunicator(ComponentType componenttype){
		ComponentCommunicator.componentType = componenttype;
		bl = new BroadcastListener(componenttype);
		jsonInterpreter = new Gson();
	}
	
	/**
	 * ticks the communication manager empties out queue towards the stem fills
	 * up in queue
	 * 
	 * @throws IOException
	 */
	private void tick() throws IOException {
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
					// Get Packets
					//sendTestPacket();
					if (input.ready()) {
						if ((inputToken = input.readLine()) != null) {
							mb.receive(jsonInterpreter.fromJson(inputToken, Transmission.class));
							System.out.println("Received: " + jsonInterpreter.toJson(mb.getNextReceived()));
						}
					}

					// Send Packets
					if (!output.checkError()) {
						if (mb.getOutQueueSize() > 0) {
							outputToken = jsonInterpreter.toJson(mb.getNextToSend());
							System.out.println("Sending: " + outputToken);
							output.println(outputToken);
						}
					} else {
						throw new IOException();
					}
				}

			} catch (IOException e) {
				System.err.println("Error in the sending or recieving of transmission. Seeking Stems..");
				CONNECTED = false;
				socket.close();
				socket = null;
			} 
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
	 * wrapper for the outgoing queue
	 * @param trans
	 */
	public static synchronized void send(Transmission trans, boolean priority){
		if(priority){
			mb.sendPriority(trans);
		} else {
			mb.send(trans);
		}
	}
	
	/**
	 * wrapper for the incoming queue
	 * @return
	 */
	public static synchronized Transmission receive(){
		if(mb.getInQueueSize() == 0){
			return null;
		} else {
			return (Transmission) mb.getNextReceived();
		}
	}
	
	/**
	 * sends a setup packet containing 
	 */
	private void sendSetupPacket(){
		System.out.print("Setting up setup packet ... ");
		try {
			TransmissionBuilder tb = new TransmissionBuilder();
			tb.newTransmission(1000, "LENS", "STEM");
			InetAddress ip = Inet4Address.getLocalHost();
			System.out.print(ip);
			
			tb.newAtom("ip", "String", ip.toString());
			tb.newAtom("type", "String", componentType.toString());
			tb.newAtom("mac", "String", getMac(ip));
			tb.newAtom("hashID", "String", Integer.toString((ip + getMac(ip)).hashCode()));
		
			Transmission trans = tb.getTransmission();
			output.println(jsonInterpreter.toJson(trans));
			
			System.out.println(" ... done");
		} catch (Exception e) {
			System.err.println("setup packet error");
			//e.printStackTrace();
		}
	}
	
	private void sendTestPacket(){
		try {
			TransmissionBuilder tb = new TransmissionBuilder();
			tb.newTransmission(1000, "LENS", "STEM");
			
			tb.newAtom("count", "String", "5");

			Transmission trans = tb.getTransmission();
			mb.send(trans);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getMac(InetAddress ia) throws SocketException {
		NetworkInterface network = NetworkInterface.getByInetAddress(ia);
		byte[] mac = network.getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
		}
		return sb.toString();
	}

}

/*
 * 	//System.out.println("GOT: " + mb.getNextReceived());
				}
				/*while(mb.getOutQueueSize() != 0){
					output.println(gson.toJson(mb.getNextToSend())); //SENDING EXPEDITED MAIL
				}
				//System.out.println("Finished outgoing queue");
				while (((inputToken = input.readLine() ) != null)) {
					System.out.println("||" + inputToken + "||");
					mb.receive(gson.fromJson(inputToken, Transmission.class)); //STORE DELIVERED MAIL
					break; //TODO: FIX!
				}*/
				
