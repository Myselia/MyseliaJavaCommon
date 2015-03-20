package com.mycelia.common.runtime;

import com.mycelia.common.communication.units.Transmission;

/**
 * This is the interface that takes care of communication for a MyceliaNode.
 */
public interface CommunicationDevice
{
	/**
	 * Sends a Transmission.
	 * 
	 * @param transmission
	 * 			The Transmission to send.
	 */
	public void sendTransmission(Transmission transmission);
	
	/**
	 * Return a Transmission received by this node.
	 * 
	 * If the node already received at least one Transmission it will return the first transmission received.
	 * If the node did not receive any Transmission since the last call to this method,
	 * it will act diffenrenty depending on the timeout value: <br />
	 * <ul>
	 * 		<li><strong>-1</strong>: This method will block and wait till a Transaction is received.
	 * 			Once a Transaction is received it will return it.</li>
	 * 		<li>
	 * 			<strong>0</strong>: This method will return null right away. 
	 * 		</li>
	 * 		<li>
	 * 			<strong>greater than 0</strong> It will wait <code>timeout</code> number of milliseconds for
	 * 			a Transmission object. If it receives a Transmission it will return it right away.
	 * 			If does not receive a Transaction it will return null after waiting <code>timeout</code> milliseconds.
	 * 		</li>
	 * </ul>
	 * 
	 * @param timeout
	 * 	<ul>
	 * 		<li><strong>-1</strong>: This method will block and wait till a Transaction is received.
	 * 			Once a Transaction is received it will return it.</li>
	 * 		<li>
	 * 			<strong>0</strong>: This method will return null right away. 
	 * 		</li>
	 * 		<li>
	 * 			<strong>greater than 0</strong> It will wait <code>timeout</code> number of milliseconds for
	 * 			a Transmission object. If it receives a Transmission it will return it right away.
	 * 			If does not receive a Transaction it will return null after waiting <code>timeout</code> milliseconds.
	 * 		</li>
	 * </ul>
	 */
	public Transmission receiveTransmission(int timeout);
}
