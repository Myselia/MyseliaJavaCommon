package com.myselia.javacommon.framework.communication;

import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.Gson;
import com.myselia.javacommon.communication.units.Transmission;
import com.myselia.javacommon.generic.FrameOutputStream;

public class TransmissionOutputStream extends FrameOutputStream
{
	private Gson gson;
	
	public TransmissionOutputStream(OutputStream fout)
	{
		super(fout);
		
		gson=new Gson();
	}

	public void writeTransmission(Transmission transmission) throws IOException
	{
		write(gson.toJson(transmission).getBytes());
	}
}
