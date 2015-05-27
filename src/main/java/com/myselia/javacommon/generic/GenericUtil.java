package com.myselia.javacommon.generic;

public class GenericUtil
{
	public static void joindIgnoreInterrupts(Thread thread)
	{
		while(true)
		{
			try
			{
				thread.join();
				return;
			}
			catch(InterruptedException e)
			{
				//Do nothing
			}
		}
	}
}
