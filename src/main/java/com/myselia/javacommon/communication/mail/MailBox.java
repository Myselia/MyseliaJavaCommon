package com.myselia.javacommon.communication.mail;

import java.util.LinkedList;

public class MailBox<T> {
	private LinkedList<T> in_queue = new LinkedList<T>();
	private LinkedList<T> out_queue = new LinkedList<T>();

	public MailBox() {
		// Do nothing
	}

	/**
	 * Adds the transmission to the incoming queue
	 * The transmission will be processed as soon as it becomes the next item that is being polled
	 * @param trans
	 */
	public synchronized void enqueueIn(T t) {
		in_queue.add(t);
	}
	
	/**
	 * Adds a new Item to the head of the incoming queue This
	 * Item is the next in line to be sent
	 * 
	 * @param trans
	 */
	public synchronized void enqueueInPriority(T t) {
		in_queue.add(0, t);
	}
	
	/**
	 * Returns the next available item
	 * @return transmission
	 */
	public synchronized T dequeueIn() {
		if (this.getInSize() == 0) {
			System.err.println("THIS IN QUEUE IS NULL");
			return null;
		} else {
			return in_queue.removeFirst();
		}
	}
	
	/**
	 * Returns the size of the incoming queue
	 * @return integer
	 */
	public synchronized int getInSize() {
		return in_queue.size();
	}
	
	/**
	 * Returns the next available transmission that was received
	 * @return transmission
	 */
	public synchronized T peekIn() {
		if (this.getInSize() == 0) {
			return null;
		} else {
			return in_queue.getFirst();
		}
	}
	
	/**
	 * Empties the incoming queue
	 */
	public synchronized void clearIn() {
		in_queue.clear();
	}

	/**
	 * Adds a new Item to the outgoing queue This Item will be
	 * sent as soon as it becomes the next element
	 * 
	 * @param trans
	 */
	public synchronized void enqueueOut(T t) {
		out_queue.add(t);
	}
	
	/**
	 * Adds a new Item to the head of the outgoing queue This
	 * Item is the next in line to be sent
	 * 
	 * @param trans
	 */
	public synchronized void enqueueOutPriority(T t) {
		out_queue.add(0, t);
	}
	
	/**
	 * Gets the next element that needs to be transmitted Return null if the
	 * queue is empty
	 * 
	 * @return transmission
	 */
	public synchronized T dequeueOut() {
		if (this.getOutSize() == 0) {
			System.err.println("THIS OUT QUEUE IS NULL");
			return null;
		} else {
			return out_queue.removeFirst();
		}
	}

	/**
	 * Returns the size of the outgoing queue
	 * @return integer
	 */
	public synchronized int getOutSize() {
		return out_queue.size();
	}
	
	/**
	 * Gets the next element that needs to be transmitted Return null if the
	 * queue is empty
	 * 
	 * @return transmission
	 */
	public synchronized T peekOut() {
		if (this.getOutSize() == 0) {
			return null;
		} else {
			return out_queue.getFirst();
		}
	}	

	/**
	 * Empties the outgoing queue
	 */
	public synchronized void clearOut() {
		out_queue.clear();
	}
}
