package com.myselia.javacommon.generic;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class FrameOutputStream extends FilterOutputStream
{
	private int sizeFieldLength;
	
	public FrameOutputStream(OutputStream fout)
	{
		this(fout, 4);
	}
	
	public FrameOutputStream(OutputStream fout, int sizeFieldLength)
	{
		super(fout);
		
		if(!(sizeFieldLength==1||sizeFieldLength==2||sizeFieldLength==4))
			throw new IllegalArgumentException("sizeFieldLength can only be 1 or 2 or 4 bytes");
		
		this.sizeFieldLength=sizeFieldLength;
	}
	
	private void writeFrameSize(int frameSize) throws IOException
	{
		if(frameSize>(256^sizeFieldLength))
			throw new IllegalArgumentException("frameSize must fit in sizeFieldLength");
		
		if(sizeFieldLength==1)
		{
			write((byte)frameSize);
		}
		else if(sizeFieldLength==2)
		{
			write(ByteBuffer.allocate(sizeFieldLength).putShort((short)frameSize).array());
		}
		else
		{
			write(ByteBuffer.allocate(sizeFieldLength).putInt(frameSize).array());
		}
	}
	
	public void writeFrame(byte[] frame) throws IOException
	{
		if(frame==null)
			throw new IllegalArgumentException("frame cannot be null");
		
		writeFrameSize(frame.length);
		write(frame);
	}
}
