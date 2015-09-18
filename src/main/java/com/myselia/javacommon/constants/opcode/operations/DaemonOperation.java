package com.myselia.javacommon.constants.opcode.operations;

import com.myselia.javacommon.constants.opcode.Operation;

/**
 * Opcodes used by the Daemon operations
 */
public enum DaemonOperation implements Operation {
	
	
	BROADCAST,
	FETCH,
	STARTSANDBOX,
	TABLEBROADCAST, /* {AT: SETUP, PURPOSE: Routing Table Update / Initialization}*/
	
}
