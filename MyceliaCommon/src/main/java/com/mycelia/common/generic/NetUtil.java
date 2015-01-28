package com.mycelia.common.generic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class NetUtil
{
	public static int ubyte(byte b)
	{
		if(b>=0)
			return b;
		else
			return b+256;
	}
	
	public static int b4toint(byte[] buffer)
	{
		return ByteBuffer.wrap(buffer).getInt();
	}
	
	public static byte[] inttob4(int i)
	{
		return ByteBuffer.allocate(4).putInt(i).array();
	}
	
	/**
	 * Sames as InputStream.read(byte[]) but throws an
	 * IOException if the end of stream is reached before
	 * reading all the bytes.
	 */
	public static byte[] read(InputStream fin, int size) throws IOException
	{
		byte[] buffer=new byte[size];
		
		int read=fin.read(buffer);
		
		if(read==-1)
			throw new IOException("Could not read enough bytes: end of stream.");
		
		return buffer;
	}
}
