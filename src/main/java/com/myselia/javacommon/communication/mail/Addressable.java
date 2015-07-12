package com.myselia.javacommon.communication.mail;

import com.myselia.javacommon.communication.units.Transmission;

public interface Addressable {
	
	public void in(Transmission trans);
	public Transmission out();
	
}
