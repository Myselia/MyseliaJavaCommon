package com.myselia.javacommon.constants.opcode.operations;

import com.myselia.javacommon.constants.opcode.Operation;

public enum StemOperation implements Operation {
	
	BROADCAST,
	SEEK,
	SETUP,
	CONFIRMSETUP,
	REJECTSETUP,
	TEST
}
