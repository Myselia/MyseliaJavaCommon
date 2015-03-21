package com.mycelia.common.exceptions;

/**
 * Generic Mycelia Runtime Exception.
 * 
 * Exceptions subclassing or instantiating this class should be
 * Non-fatal or rarely occurring exception; this class extends
 * RuntimeException and therefore does not force the method callee to
 * check the exception. 
 */
public class MyceliaRuntimeException extends RuntimeException
{
	private static final long serialVersionUID=7693921292465325121L;

	public MyceliaRuntimeException()
	{
		//Do nothing.
	}
	
	public MyceliaRuntimeException(String message)
	{
		super(message);
	}
	
	public MyceliaRuntimeException(Throwable t)
	{
		super(t);
	}
}
