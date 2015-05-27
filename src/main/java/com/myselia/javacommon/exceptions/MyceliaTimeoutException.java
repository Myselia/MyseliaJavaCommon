package com.myselia.javacommon.exceptions;

/**
 * A MyceliaRuntimeException occurring when a certain time
 * limit was set on a blocking method and that time limit was reached.
 */
public class MyceliaTimeoutException extends MyceliaRuntimeException
{
	private static final long serialVersionUID=1836620069904379085L;

	public MyceliaTimeoutException()
	{
		//Do nothing.
	}
	
	public MyceliaTimeoutException(String message)
	{
		super(message);
	}
	
	public MyceliaTimeoutException(Throwable t)
	{
		super(t);
	}
}
