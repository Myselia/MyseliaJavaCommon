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
	
	private static BroadcastListener bl;
	private static MailBox mb;
	private static ComponentType componenttype;
	
	static {
		mb = new MailBox();
	}
	
	
	private Socket socket;
	private Gson gson = new Gson();
	private PrintWriter writer;
	private BufferedReader reader;
	
	private boolean CONNECTED = false;
	private String input = "";
	
	public ComponentCommunicator(ComponentType componenttype){
		this.componenttype = componenttype;
		bl = new BroadcastListener(componenttype);
	}
	
	/**
	 * ticks the communication manager 
	 * empties out queue towards the stem
	 * fills up in queue
	 * @throws IOException 
	 */
	private void tick() throws IOException{
		if(!CONNECTED){
			bl.startSeeking();
			Transmission trans = bl.listen(512);
			if (bl.transmissionReady()) {
				bl.endSeeking();
				connect(trans);
			}
		} else {		
			try {
				System.out.println(mb.getOutQueueSize());
				
				while(mb.getOutQueueSize() != 0){
					writer.println(gson.toJson(mb.getNextExpedited())); //SENDING EXPEDITED MAIL
				}
				//System.out.println("Finished outgoing queue");
				while (((input = reader.readLine() ) != null)) {
					System.out.println("||" + input + "||");
					mb.deliver(gson.fromJson(input, Transmission.class)); //STORE DELIVERED MAIL
					break; //TODO: FIX!
				}
				//System.out.println("Finished incoming queue");
			} catch (IOException e) {
				System.err.println("error in the sending or recieving of transmission, going back to seeking");
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
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
			writer.flush();
			writer.close();
			reader.close();
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
			mb.sendPriorityMail(trans);
		} else {
			mb.sendMail(trans);
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
			return mb.getNextDelivered();
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
			tb.newAtom("type", "String", componenttype.toString());
			tb.newAtom("mac", "String", getMac(ip));
			tb.newAtom("hashID", "String", Integer.toString((ip + getMac(ip)).hashCode()));
		
			Transmission trans = tb.getTransmission();
			mb.sendMail(trans);
			
			System.out.println(" ... done");
		} catch (Exception e) {
			System.err.println("setup packet error");
			//e.printStackTrace();
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
