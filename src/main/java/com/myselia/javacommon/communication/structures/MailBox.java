package com.myselia.javacommon.communication.structures;

import java.util.LinkedList;

public class MailBox<T> {
	private LinkedList<T> in_queue = new LinkedList<T>();
	private LinkedList<T> out_queue = new LinkedList<T>();

	public MailBox() {
		// Do nothing
	}

	/**
	 * Adds a new Transmission to the outgoing queue This Transmission will be
	 * sent as soon as it becomes the next element
	 * 
	 * @param trans
	 */
	public synchronized void putInOutQueue(T trans) {
		out_queue.add(trans);
	}

	/**
	 * Adds a new Transmission to the head of the outgoing queue This
	 * Transmission is the next in line to be sent
	 * 
	 * @param trans
	 */
	public synchronized void putInOutQueueTop(T trans) {
		out_queue.add(0, trans);
	}
	
	/**
	 * Adds all the elements of the given list into the out queue
	 * @param out_list
	 */
	public synchronized void putAllInOutQueue(LinkedList<T> out_list){
		out_queue.addAll(out_list);
		out_list.clear();
	}

	/**
	 * Adds the transmission to the incoming queue
	 * The transmission will be processed as soon as it becomes the next item that is being polled
	 * @param trans
	 */
	public synchronized void putInInQueue(T trans) {
		in_queue.add(trans);
	}
	
	/**
	 * Adds all the elements on the given list into the in queue
	 * @param in_list
	 */
	public synchronized void putAllInInQueue(LinkedList<T> in_list){
		in_queue.addAll(in_list);
		in_list.clear();
	}
	
	/**
	 * Gets the next element that needs to be transmitted Return null if the
	 * queue is empty
	 * 
	 * @return transmission
	 */
	public synchronized T getFromOutQueue() {
		if (this.getOutQueueSize() == 0) {
			System.err.println("THIS OUT QUEUE IS NULL");
			return null;
		} else {
			return out_queue.removeFirst();
		}
	}

	/**
	 * Returns the next available transmission that was received
	 * @return transmission
	 */
	public synchronized T getFromInQueue() {
		if (this.getInQueueSize() == 0) {
			System.err.println("THIS IN QUEUE IS NULL");
			return null;
		} else {
			return in_queue.removeFirst();
		}
	}
	
	/**
	 * Gets the next element that needs to be transmitted Return null if the
	 * queue is empty
	 * 
	 * @return transmission
	 */
	public synchronized T peekOutQueue() {
		if (this.getOutQueueSize() == 0) {
			return null;
		} else {
			return out_queue.getFirst();
		}
	}

	/**
	 * Returns the next available transmission that was received
	 * @return transmission
	 */
	public synchronized T peekInQueue() {
		if (this.getInQueueSize() == 0) {
			return null;
		} else {
			return in_queue.getFirst();
		}
	}

	/**
	 * Returns the size of the incoming queue
	 * @return integer
	 */
	public synchronized int getInQueueSize() {
		return in_queue.size();
	}

	/**
	 * Returns the size of the outgoing queue
	 * @return integer
	 */
	public synchronized int getOutQueueSize() {
		return out_queue.size();
	}
	
	/**
	 * Returns everything from the in queue of the Mailbox
	 * @return
	 */
	public synchronized LinkedList<T> getAllFromInQueue(){
		return in_queue;
	}
	
	/**
	 * Returns everything from the out queue of the Mailbox
	 * @return
	 */
	public synchronized LinkedList<T> getAllFromOutQueue(){
		return out_queue;
	}

	public synchronized void clearInQueue() {
		in_queue.clear();
	}

	public synchronized void clearOutQueue() {
		out_queue.clear();
	}
}
