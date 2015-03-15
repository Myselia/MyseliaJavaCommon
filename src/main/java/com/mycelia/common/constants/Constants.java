package com.mycelia.common.constants;

public class Constants
{
	/**
	 * Node ID representing a transmission to any node.
	 */
	public static final String ANY_SLAVE_NODE="any";
	
	/**
	 * Default user opcode for when the user does not specify it.
	 */
	public static final int DEFAULT_USER_OPCODE_PREFIX=
			OpcodePrefix.USER+OpcodePrefix.DATA		+0;
	
	/**
	 * Enum type that distinguishes between MyCelia components. 
	 * Use example: Within LENS initialization code, a global type variable
	 * would identify the component as a LENS, allowing STEM networking code
	 * to distinguish between various components. 
	 */
	public enum componentType {
		DAEMON,
		LENS,
		SANDBOX
	};
}
