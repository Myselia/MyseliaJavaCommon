package com.mycelia.common.generic;

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
