package com.myselia.javacommon.constants.opcode;

import com.myselia.javacommon.constants.opcode.operations.DaemonOperation;
import com.myselia.javacommon.constants.opcode.operations.DatabaseOperation;
import com.myselia.javacommon.constants.opcode.operations.LensOperation;
import com.myselia.javacommon.constants.opcode.operations.SandboxMasterOperation;
import com.myselia.javacommon.constants.opcode.operations.SandboxSlaveOperation;
import com.myselia.javacommon.constants.opcode.operations.StemOperation;
import com.myselia.javacommon.exceptions.MyseliaOpcodeException;
import com.myselia.javacommon.topology.MyseliaUUID;

public final class OpcodeBroker {

	private static final String SEPARATOR = "_";
	private static final String SEGMENTOR = ":";

	/**
	 * Makes an opcode out of values
	 * 
	 * @param Component Type
	 * @param ComponentCertificate
	 * @param ActionType
	 * @param Operation
	 * @return
	 */
	public static String make(ComponentType ct, MyseliaUUID muuid, ActionType at, Operation op) {
		return ct + SEGMENTOR + muuid + SEPARATOR + at + SEPARATOR + op;
	}

	/**
	 * segregates an opcode into an array of OpcodeSegments
	 * 
	 * @param opcode
	 * @return OpcodeSegment[]
	 */
	public static OpcodeSegment[] segregate(String opcode) {
		OpcodeSegment[] segregatedsegments = new OpcodeSegment[4];

		String[] section = opcode.split(SEPARATOR);

		try {
			segregatedsegments[0] = getComponentType(section[0]);
			segregatedsegments[1] = getMyseliaUUID(section[0]);
			segregatedsegments[2] = getActionType(section[1]);
			segregatedsegments[3] = getOperation((ComponentType)segregatedsegments[0], section[2]);
		} catch (Exception e) {
			System.err.println("Opcode Broker : Myselia Opcode Exception");
		}

		return segregatedsegments;
	}

	/**
	 * gets the ComponentType out of the component section
	 * 
	 * @param component
	 * @return ComponentType
	 */
	private static ComponentType getComponentType(String component) {
		String[] componentsection = component.split(SEGMENTOR);
		return ComponentType.valueOf(componentsection[0]);
	}

	/**
	 * gets the MyseliaUUID of the component
	 * 
	 * @param component
	 * @return MyseliaUUID
	 */
	private static MyseliaUUID getMyseliaUUID(String component) {
		String[] componentsection = component.split(SEGMENTOR);
		if (componentsection.length > 0) {
			return new MyseliaUUID(componentsection[1]);
		} else {
			return null;
		}
	}

	/**
	 * gets the ActionType out of the component section
	 * 
	 * @param action
	 * @return ActionType
	 */
	private static ActionType getActionType(String action) {
		return ActionType.valueOf(action);
	}

	private static Operation getOperation(ComponentType ct, String op) throws MyseliaOpcodeException{
		Operation operation;

		try {
			switch (ct) {
			case DAEMON:
				operation = DaemonOperation.valueOf(op);
				break;
			case DATABASE:
				operation = DatabaseOperation.valueOf(op);
				break;
			case LENS:
				operation = LensOperation.valueOf(op);
				break;
			case SANDBOXMASTER:
				operation = SandboxMasterOperation.valueOf(op);
				break;
			case SANDBOXSLAVE:
				operation = SandboxSlaveOperation.valueOf(op);
				break;
			case STEM:
				operation = StemOperation.valueOf(op);
				break;
			default:
				operation = null;
				throw new MyseliaOpcodeException();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new MyseliaOpcodeException();
		}

		return operation;
	}
	
	/**
	 * Makes an opcode section that is ready to be checked by the MailService
	 * @param os_one
	 * @param os_two
	 * @return opcode section
	 */
	public static String makeMailCheckingOpcode(OpcodeSegment os_one, OpcodeSegment os_two){
		return os_one + SEPARATOR + os_two;
	}
	
	/**
	 * 
	 * @param opcode
	 * @return Component+Action opcode section
	 */
	public static String getComponentActionOpcode(String opcode){
		OpcodeSegment[] segregatedsegments = OpcodeBroker.segregate(opcode);
		return makeMailCheckingOpcode(segregatedsegments[0], segregatedsegments[2]);
	}
	
	/**
	 * 
	 * @param opcode
	 * @return Action+Operation opcode section
	 */
	public static String getActionOperationOpcode(String opcode){
		OpcodeSegment[] segregatedsegments =OpcodeBroker.segregate(opcode);
		return makeMailCheckingOpcode(segregatedsegments[2], segregatedsegments[3]);
	}

}
