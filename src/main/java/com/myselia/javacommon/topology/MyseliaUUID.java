package com.myselia.javacommon.topology;

import org.apache.commons.codec.digest.DigestUtils;

import com.myselia.javacommon.constants.opcode.OpcodeSegment;

public class MyseliaUUID implements OpcodeSegment {
	
	private final String UUID;
	
	public MyseliaUUID(String host, String ip, String mac, String ct, String unixts){
		this.UUID = DigestUtils.sha1Hex(host + ip + mac + ct + unixts);	
	}
	
	public MyseliaUUID(String UUID){
		this.UUID= UUID;
	}
	
	public String getValueOf(){
		return UUID;
	}

}
