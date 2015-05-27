package com.myselia.javacommon.framework.communication;

import java.io.IOException;
import java.io.InputStream;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myselia.javacommon.communication.units.Transmission;
import com.myselia.javacommon.generic.FrameInputStream;

public class TransmissionInputStream extends FrameInputStream
{
	private Gson gson;
	
	public TransmissionInputStream(InputStream fin)
	{
		super(fin);
		
		gson=new Gson();
	}
	
	public Transmission readTransmission() throws IOException
	{
		try
		{
			return gson.fromJson(new String(readFrame()), Transmission.class);
		}
		catch(JsonSyntaxException e)
		{
			throw new IOException(e);
		}
	}
}
