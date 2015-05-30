package com.myselia.javacommon.communication.mail;



public interface Addressable {
	
	public MailBox<?> getMailBox();
	public void notifyIncomingMail();
	
}
