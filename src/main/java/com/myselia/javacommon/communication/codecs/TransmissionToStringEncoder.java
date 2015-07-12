package com.myselia.javacommon.communication.codecs;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.google.gson.Gson;
import com.myselia.javacommon.communication.units.Transmission;

public class TransmissionToStringEncoder extends MessageToMessageEncoder<Transmission> {

	static Gson jsonCodec = new Gson();
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Transmission msg, List<Object> out) throws Exception {
		String payload = jsonCodec.toJson(msg, Transmission.class) + "\r\n";
		
		out.add(payload);
	}

}
	