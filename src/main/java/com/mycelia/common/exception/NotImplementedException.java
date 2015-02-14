package com.mycelia.common.exception;

/**
 * Exception thrown by not implemented interfaces or methods.
 * 
 * These methods might be Mycelia packaged methods that will be implemented in the future
 * or method that were delegated to third parties but a third party implementation was not found
 * by the Mycelia Framework. 
 */
public class NotImplementedException extends RuntimeException
{
	private static final long serialVersionUID=-5792478006700846260L;

	public NotImplementedException()
	{
		//Do nothing.
	}
	
	public NotImplementedException(String message)
	{
		super(message);
	}
	
	public NotImplementedException(Throwable t)
	{
		super(t);
	}
}
