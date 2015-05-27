package com.myselia.javacommon.communication;

import com.myselia.javacommon.communication.structures.MailBox;

public interface Addressable {
	
	public MailBox<?> getMailBox();
	
}
