package com.mycelia.common.constants;

/**
 * Transmission opcode prefixes for the different part of the Mycelia Framework.
 * This also lists opcodes category prefixes.
 */
public class OpcodePrefix
{
	//X00 opcode prefixes
	
	/**
	 * Prefix used for Transmission opcodes related to the sandbox.
	 */
	public static final String SANDBOX_MASTER="4";
	
	/**
	 * Prefix used for Transmission opcodes related to the sandbox.
	 */
	public static final String SANDBOX_SLAVE="5";
	
	/**
	 * Prefix used for Transmission opcodes related to user transmissions.
	 */
	public static final String USER="9";
	
	//0X0 opcode prefixes
	
	public static final String SETUP="0";
	public static final String RUNTIME="2";
	public static final String ERROR="4";
	public static final String DATA="6";
}
