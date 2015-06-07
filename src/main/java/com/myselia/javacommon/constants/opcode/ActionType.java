package com.myselia.javacommon.constants.opcode;


public enum ActionType implements OpcodeSegment{
	
	/**
	 * Setup operations of component
	 */
	SETUP, 
	
	/**
	 * Runtime operations of component
	 */
	RUNTIME, 
	
	/**
	 * Error in operations of component
	 */
	ERROR, 
	
	/**
	 * Data transfer operations of component
	 */
	DATA, 
	
	/**
	 * Configuration operations of component
	 */
	CONFIG

}
