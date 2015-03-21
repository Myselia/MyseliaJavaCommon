package com.mycelia.common.constants.opcode;

import com.mycelia.common.constants.opcode.operations.DaemonOperation;
import com.mycelia.common.constants.opcode.operations.DatabaseOperation;
import com.mycelia.common.constants.opcode.operations.LensOperation;
import com.mycelia.common.constants.opcode.operations.SandboxMasterOperation;
import com.mycelia.common.constants.opcode.operations.SandboxSlaveOperation;
import com.mycelia.common.constants.opcode.operations.StemMasterOperation;
import com.mycelia.common.constants.opcode.operations.StemSlaveOperation;
import com.mycelia.common.exceptions.MyceliaOpcodeException;

public class OpcodeAccessor {
	
private final String SEPARATOR = "_";

	public String make(ComponentType component, ActionType action, Operation operation) {
		return component + SEPARATOR + action + SEPARATOR + operation;
	}
	
	public Opcode[] retrieve(String opCode) throws MyceliaOpcodeException {
		String[] names = opCode.split(SEPARATOR);
		
		ComponentType componentType = ComponentType.valueOf(names[0]);
		ActionType actionType = ActionType.valueOf(names[1]);
		String operation_string = names[2];
		Operation operation;

		switch (componentType) {
		case DAEMON:
			operation = DaemonOperation.valueOf(operation_string);
			break;
		case DATABASE:
			operation = DatabaseOperation.valueOf(operation_string);
			break;
		case LENS:
			operation = LensOperation.valueOf(operation_string);
			break;
		case SANDBOXMASTER:
			operation = SandboxMasterOperation.valueOf(operation_string);
			break;
		case SANDBOXSLAVE:
			operation = SandboxSlaveOperation.valueOf(operation_string);
			break;
		case STEMMASTER:
			operation = StemMasterOperation.valueOf(operation_string);
			break;
		case STEMSLAVE:
			operation = StemSlaveOperation.valueOf(operation_string);
			break;
		default:
			operation = null;
			throw new MyceliaOpcodeException();
		}
		
		return (new Opcode[] {componentType, actionType, operation});
		
	}
}