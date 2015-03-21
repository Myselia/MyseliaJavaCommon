package com.mycelia.common.communication;

import com.mycelia.common.communication.structures.MailBox;

public interface Addressable {
	
	public MailBox<?> getMailBox();
	
}
