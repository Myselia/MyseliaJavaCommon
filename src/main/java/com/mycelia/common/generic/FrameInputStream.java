package com.mycelia.common.generic;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class FrameInputStream extends FilterInputStream
{
	private int sizeFieldLength;
	
	public FrameInputStream(InputStream fin)
	{
		this(fin, 4);
	}
	
	public FrameInputStream(InputStream fin, int sizeFieldLength)
	{
		super(fin);
		
		if(!(sizeFieldLength==1||sizeFieldLength==2||sizeFieldLength==4))
			throw new IllegalArgumentException("sizeFieldLength can only be 1 or 2 or 4 bytes");
		
		this.sizeFieldLength=sizeFieldLength;
	}
	
	private int readFrameSize() throws IOException
	{
		if(sizeFieldLength==1)
		{
			return NetUtil.ubyte(NetUtil.read(this, 1)[0]);
		}
		else if(sizeFieldLength==2)
		{
			return ByteBuffer.wrap(NetUtil.read(this, 2)).getShort();
		}
		else
		{
			return ByteBuffer.wrap(NetUtil.read(this, 4)).getInt();
		}
	}
	
	public byte[] readFrame() throws IOException
	{
		int frameSize=readFrameSize();
		
		return NetUtil.read(this, frameSize);
	}
}
