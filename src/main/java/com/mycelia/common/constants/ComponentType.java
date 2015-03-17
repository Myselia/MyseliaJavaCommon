package com.mycelia.common.constants;

/**
 * Enum type that distinguishes between Mycelia components. 
 * Use example: Within LENS initialization code, a global type variable
 * would identify the component as a LENS, allowing STEM networking code
 * to distinguish between various components. 
 */
public enum ComponentType {
	/**
	 * Sandbox running in master mode
	 */
	SANDBOXMASTER,
	/**
	 * Sandbox running in slave mode
	 */
	SANDBOXSLAVE,
	/**
	 * Stem running in master mode
	 */
	STEMMASTER,
	/**
	 * Stem running in slave mode (relay)
	 */
	STEMSLAVE,
	/**
	 * Daemon component
	 */
	DAEMON,
	/**
	 * Lens component
	 */
	LENS,
	/**
	 * Database component
	 */
	DATABASE
}
