package com.mycelia.common.constants;

/**
 * Transmission opcode prefixes for the different part of the Mycelia Framework.
 * This also lists opcodes category prefixes.
 */
public class OpcodePrefix
{
	//interpreter prefixes to:
	public static final int STEM = 1000;
	public static final int DAEMON=5000;
	public static final int SANDBOX_MASTER=6000;
	public static final int SANDBOX_SLAVE=7000;
	public static final int LENS=8000;
	public static final int USER=9000;
	
	//action prefixes for:
	public static final int SETUP=100;
	public static final int RUNTIME=200;
	public static final int ERROR=400;
	public static final int DATA=600;
}
