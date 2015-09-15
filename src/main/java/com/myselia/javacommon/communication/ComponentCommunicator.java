package com.myselia.javacommon.communication;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.myselia.javacommon.communication.mail.Addressable;
import com.myselia.javacommon.communication.mail.MailBox;
import com.myselia.javacommon.communication.units.Atom;
import com.myselia.javacommon.communication.units.Transmission;
import com.myselia.javacommon.constants.opcode.ComponentType;
import com.myselia.javacommon.topology.ComponentCertificate;
	
public class ComponentCommunicator implements Addressable, Runnable {
	
	private Thread networkThread;
	private static Gson jsonInterpreter = new Gson();
	private MailBox<Transmission> mailBox;
	private BroadcastListener bl;
	private ComponentCommunicationHandler handler = null;
	private ComponentCertificate componentCertificate = null;
	private ComponentCertificate stemCertificate = null;
	
	private boolean CONNECTED = false;

	public ComponentCommunicator(ComponentType componentType) {
		componentCertificate = new ComponentCertificate(componentType);
		bl = new BroadcastListener(this);
		mailBox = new MailBox<Transmission>();
	}
	
	public void endpointReceive() {
		System.out.println("Handler is at: " + handler);
		System.out.println("Mailbox is at: " + mailBox.peekIn());
		if(handler != null){
			handler.write(mailBox.dequeueIn());
		}
		
	}
	
	private void createNetworkClient(ComponentCertificate stemCertificate, int port) throws InterruptedException {
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ComponentCommunicatorInitializer(this));

			// Start the client.
			ChannelFuture f = b.connect(stemCertificate.getHostName(), port).sync(); 

			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}

	/**
	 * Instantiates a communication socket to the values that it has received
	 * @param trans
	 * @throws InterruptedException 
	 */
	public void connect(Transmission trans) {

		// Initialize needed info
		ArrayList<Atom> list = trans.get_atoms();
		stemCertificate = jsonInterpreter.fromJson(list.get(0).get_value(), ComponentCertificate.class);
		int port = Integer.parseInt(list.get(1).get_value());

		// Attempt connection
		System.out.print("Trying to connect to host at: " + stemCertificate.getIpAddress() + ":" + port + "... ");
		try {
			createNetworkClient(stemCertificate, port);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public ComponentCertificate getComponentCertificate() {
		return componentCertificate;
	}
	
	public ComponentCertificate getStemCertificate() {
		return stemCertificate;
	}

	/**
	 * Returns the connection state of the Lens
	 * @return boolean CONNECTED
	 */
	public boolean isConnected(){
		return CONNECTED;
	}

	@Override
	//Something is meant to be sent out
	public void in(Transmission trans) {
		mailBox.enqueueIn(trans);
		endpointReceive();
	}
	
	@Override
	//Something has been received, this takes it out
	public Transmission out(){
		return mailBox.dequeueOut();
	}

	public MailBox<Transmission> getMailBox() {
		return mailBox;
	}
	
	public void setHandler(ComponentCommunicationHandler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		System.out.println("STARTING SEEK PROCEDURE");
		bl.startSeeking();
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		networkThread = new Thread(this);
		networkThread.start();
	}

}