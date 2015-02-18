package com.mycelia.common.constants;

/**
 * Transmission opcodes used by the Daemon
 */
public class DaemonOpcode {
	/**
	 * ID Request opcode
	 */
	public static final int GET_MASTER_ID_REQUEST = 
			OpcodePrefix.DAEMON	+ OpcodePrefix.RUNTIME 	+1;

	/**
	 * ID Request answer opcode
	 */
	public static final int GET_MASTER_ID_ANSWER = 
			OpcodePrefix.DAEMON + OpcodePrefix.RUNTIME 	+2;
}
