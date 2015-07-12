package com.myselia.javacommon.topology;

import org.apache.commons.codec.digest.DigestUtils;

import com.myselia.javacommon.constants.opcode.ComponentType;
import com.myselia.javacommon.constants.opcode.OpcodeSegment;

public class MyseliaUUID implements OpcodeSegment{
	private final String UUID;
	
	public MyseliaUUID(String UUID){
		this.UUID = UUID;
	}
	
	public MyseliaUUID(ComponentCertificate componentInformation) {
		this.UUID = generateUUID(componentInformation.getHostName(), 
				componentInformation.getIpAddress(), 
				componentInformation.getMacAddress(), 
				componentInformation.getComponentType(),
				componentInformation.getUnixTimestamp());
	}

	private String generateUUID(String hostName, String ipAddress, String macAddress, ComponentType componentType, long unixTimestamp) {
		return DigestUtils.sha1Hex(hostName + ipAddress + macAddress + componentType + unixTimestamp);
	}
	
	public String toString(){
		return UUID;
	}
}
