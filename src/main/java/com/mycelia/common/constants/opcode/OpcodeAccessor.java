package com.mycelia.common.constants.opcode;

import com.mycelia.common.constants.opcode.operations.DaemonOperation;
import com.mycelia.common.constants.opcode.operations.DatabaseOperation;
import com.mycelia.common.constants.opcode.operations.LensOperation;
import com.mycelia.common.constants.opcode.operations.SandboxMasterOperation;
import com.mycelia.common.constants.opcode.operations.SandboxSlaveOperation;
import com.mycelia.common.constants.opcode.operations.StemOperation;
import com.mycelia.common.exceptions.MyceliaOpcodeException;

public class OpcodeAccessor {

	private static final String SEPARATOR = "_";

	public static String make(ComponentType component, ActionType action, String operation) {
		return component + SEPARATOR + action + SEPARATOR + operation;
	}

	/**
	 * 
	 * 
	 * @param opCode
	 *            A string to be turned into an opcode array. First two fields
	 *            are opcode enums, last field is string representing specific
	 *            component action.
	 * @return CAST TO: [0] - Opcode [1] - Opcode [2] - String
	 * @throws MyceliaOpcodeException
	 */
	public static Object[] retrieve(String opCode) throws MyceliaOpcodeException {
		String[] names = opCode.split(SEPARATOR);

		ComponentType componentType = ComponentType.valueOf(names[0]);
		ActionType actionType = ActionType.valueOf(names[1]);
		String operation_string = names[2];
		String operation;

		try {
			switch (componentType) {
			case DAEMON:
				operation = DaemonOperation.class.getField(operation_string).getName();
				break;
			case DATABASE:
				operation = DatabaseOperation.class.getField(operation_string).getName();
				break;
			case LENS:
				operation = LensOperation.class.getField(operation_string).getName();
				break;
			case SANDBOXMASTER:
				operation = SandboxMasterOperation.class.getField(operation_string).getName();
				break;
			case SANDBOXSLAVE:
				operation = SandboxSlaveOperation.class.getField(operation_string).getName();
				break;
			case STEM:
				operation = StemOperation.class.getField(operation_string).getName();
				break;
			default:
				operation = null;
				throw new MyceliaOpcodeException();

			}
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			throw new MyceliaOpcodeException();
		}

		return (new Object[] { componentType, actionType, operation });

	}
	
	public static String getLocalOpcode(String s) throws MyceliaOpcodeException {
		Object[] opCode = retrieve(s);

		return ((Enum<?>)opCode[1]).toString() + SEPARATOR + opCode[2];
	}

	public static Operation getOpcodes(ComponentType t) {
		Operation operation = null;
		switch (t) {
		case DAEMON:
			operation = new LensOperation();
			break;
		case DATABASE:
			operation = new DatabaseOperation();
			break;
		case LENS:
			operation = new LensOperation();
			break;
		case SANDBOXMASTER:
			operation = new SandboxMasterOperation();
			break;
		case SANDBOXSLAVE:
			operation = new SandboxSlaveOperation();
			break;
		case STEM:
			operation = new StemOperation();
			break;
		}
		return operation;
	}
}