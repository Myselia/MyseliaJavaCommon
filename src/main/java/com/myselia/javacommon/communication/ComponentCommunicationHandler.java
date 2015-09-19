package com.myselia.javacommon.communication;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Iterator;

import com.google.gson.Gson;
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

public class ComponentCommunicationHandler extends SimpleChannelInboundHandler<Transmission> {

	private static Gson jsonInterpreter = new Gson();
	private Channel clientChannel = null;
	private ComponentCommunicator componentCommunicator;
	// The certificate of this component
	private ComponentCertificate componentCertificate;
	// The certificate of the Stem this component is connected to
	private ComponentCertificate stemCertificate;
	private MailBox<Transmission> mailBox;

	public ComponentCommunicationHandler(Channel clientChannel, ComponentCommunicator componentCommunicator) {
		this.clientChannel = clientChannel;
		this.componentCommunicator = componentCommunicator;
		this.componentCertificate = componentCommunicator.getComponentCertificate();
		this.stemCertificate = componentCommunicator.getStemCertificate();
		this.mailBox = componentCommunicator.getMailBox();
		componentCommunicator.setHandler(this);
		//setupFutures(clientChannel);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		System.out.println("[ComponentCommunicator]: Established connection @ " + ctx);
		System.out.println("\t|-->Awaiting handshake");

		//Send the setup packet expected by the stem containing the component certificate
		sendSetupPacket();
		
		//Send current routing table 
		write(ComponentCommunicator.routingTableUpdateTransmission());
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Transmission msg) throws Exception {
		printRecvMsg(msg);
		mailBox.enqueueOut(msg);
		MailService.notify(componentCommunicator);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	public void write(Transmission t) {
		clientChannel.writeAndFlush(t);
	}

	/*public void setupFutures(Channel channel) {
		ChannelFuture closeFuture = channel.closeFuture();

		closeFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				System.out.println("DISCONNECTED ");
			}
		});
	}*/

	private void sendSetupPacket() {
		System.out.print("Setting up setup packet ... ");

		TransmissionBuilder tb = new TransmissionBuilder();
		String from = OpcodeBroker.make(componentCertificate.getComponentType(), componentCertificate.getUUID(), ActionType.SETUP, null);
		String to = OpcodeBroker.make(ComponentType.STEM, null, ActionType.SETUP, StemOperation.SETUP);
		tb.newTransmission(from, to);

		tb.addAtom("componentCertificate", "ComponentCertificate", jsonInterpreter.toJson(componentCertificate));
		Transmission trans = tb.getTransmission();
		
		write(trans);
		System.out.println(" ... done");
	}

	private void printRecvMsg(Transmission t) {
		System.out.println();
		System.out.println("[Message Received]");
		
		
		//Header
		System.out.println("\t->Header: ");
		System.out.println("\t\t->ID: " + t.get_header().get_id());
		System.out.println("\t\t->From: " + t.get_header().get_from());
		System.out.println("\t\t->To: " + t.get_header().get_to());
		//Atoms
		System.out.println("\t->Atoms:");
		Iterator<Atom> it = t.get_atoms().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			System.out.println("\t\t->Field: " + a.get_field());
			System.out.println("\t\t\t->Type: " + a.get_type());
			System.out.println("\t\t\t->Value: " + a.get_value());
		}
		System.out.println();
	}

}
