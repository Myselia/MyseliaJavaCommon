package com.mycelia.common.constants;


/**
 * Transmission opcodes used by the Sandbox.
 */
public class SandboxOpcodes
{
	//General
	
	/**
	 * General error opcode.
	 */
	public static final String GENERIC_ERROR=
			OpcodePrefix.SANDBOX_MASTER+OpcodePrefix.ERROR+"00";
	
	/**
	 * Opcode to start a node.
	 */
	public static final String START=
			OpcodePrefix.SANDBOX_MASTER+OpcodePrefix.RUNTIME+"01";
	/**
	 * Opcode to stop a node.
	 */
	public static final String STOP=
			OpcodePrefix.SANDBOX_MASTER+OpcodePrefix.RUNTIME+"02";
	
	//Slave node codes
	
	/**
	 * Opcode to request "start task" on a slave node.
	 */
	public static final String START_TASK_REQUEST_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"03";
	
	/**
	 * Opcode for "start task" answer on a slave node.
	 */
	public static final String START_TASK_ANSWER_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"04";
	
	/**
	 * Opcode to request "get task instance result" on a slave node.
	 */
	public static final String GET_RESULT_REQUEST_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"05";
	
	/**
	 * Opcode for "get task instance result" answer on a slave node.
	 */
	public static final String GET_RESULT_ANSWER_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"06";
	
	/**
	 * Opcode to request "is task instance done" on a slave node.
	 */
	public static final String TASK_STATUS_REQUEST_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"07";
	
	/**
	 * Opcode for "is task instance done" answer on a slave node.
	 */
	public static final String TASK_STATUS_ANSWER_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"08";
	
	/**
	 * Opcode to request "get task instance" on a slave node.
	 */
	public static final String GET_TASK_INSTANCE_REQUEST_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"09";
	
	/**
	 * Opcode for "get task instance result" answer on a slave node.
	 */
	public static final String GET_TASK_INSTANCE_ANSWER_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"10";
	
	/**
	 * Opcode to request "is task done" on a slave node.
	 */
	public static final String IS_TASK_DONE_REQUEST_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"11";
	
	/**
	 * Opcode for "is task done" answer on a slave node.
	 */
	public static final String IS_TASK_DONE_ANSWER_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"12";
	
	/**
	 * Opcode to request "get tasks" on a slave node.
	 */
	public static final String GET_TASKS_REQUEST_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"13";
	
	/**
	 * Opcode for "get tasks" answer on a slave node.
	 */
	public static final String GET_TASKS_ANSWER_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"14";
	
	/**
	 * Opcode to request "get running tasks" on a slave node.
	 */
	public static final String GET_RUNNING_TASKS_REQUEST_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"15";
	
	/**
	 * Opcode for "get running tasks" answer on a slave node.
	 */
	public static final String GET_RUNNING_TASKS_ANSWER_SLAVE=
			OpcodePrefix.SANDBOX_SLAVE+OpcodePrefix.RUNTIME+"16";
}
