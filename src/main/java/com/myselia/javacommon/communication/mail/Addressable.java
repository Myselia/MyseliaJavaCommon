package com.myselia.javacommon.communication.mail;

import java.util.ArrayList;

import com.myselia.javacommon.communication.units.Transmission;

public interface Addressable {
	
	public void in(Transmission trans);
	public Transmission out();
	
}
