package com.myselia.javacommon.topology;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.codec.binary.Hex;

import com.myselia.javacommon.constants.opcode.ComponentType;

public class ComponentCertificate {

	private final MyseliaUUID UUID;
	private final ComponentType componentType;
	private final String hostName;
	//FORMAT: xxx.xxx.xxx.xxx
	private final String ipAddress; 
	//FORMAT: NO SEPARATORS, ALL LOWER CASE
	//Example: 4c0f6ee0cb57 (Length: 12 chars, ASCII Range: 71h - 7Ah, 30h - 39h)
	private final String macAddress;
	private final long unixTimestamp;
	
	private transient static NetworkInterface connectionEndpoint;

	/**
	 * Constructor called if object is generated for local use (ie created by a component, there is only ONE per component)
	 * @param componentType
	 */
	public ComponentCertificate(ComponentType componentType) {
		try {
			filterInterfaces();
		} catch (UnknownHostException e) {
			System.err.println("MyseliaUUID : failed to fetch proper interface");
		}
		
		this.hostName = fetchHost();
		this.ipAddress = fetchIP();
		this.macAddress = fetchMAC();
		this.componentType = componentType;
		this.unixTimestamp = fetchUnixTimestamp();
		this.UUID = new MyseliaUUID(this);
		
		printData();
	}

	/**
	 * Constructor called if object is created for component record keeping and system use (ie created by the stem to store in topology handlers)
	 * @param hostName
	 * @param ipAddress
	 * @param macAddress
	 * @param componentType
	 * @param unixTimestamp
	 */
	public ComponentCertificate(String hostName, String ipAddress, String macAddress, ComponentType componentType, long unixTimestamp) {
		this.hostName = hostName;
		this.ipAddress = ipAddress;
		this.macAddress = macAddress;
		this.componentType = componentType;
		this.unixTimestamp = unixTimestamp;
		this.UUID = new MyseliaUUID(this);
	}


	/*
	 * Fetch Methods
	 */
	private static String fetchHost() {
		String hostName = null;
		for (Enumeration<InetAddress> inetAddrs = connectionEndpoint.getInetAddresses(); inetAddrs.hasMoreElements();) {
			InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
			hostName = inetAddr.getHostName();
			break;
		}
		return hostName;
	}

	private static String fetchIP() {
		String ipAddress = null;
		for (Enumeration<InetAddress> inetAddrs = connectionEndpoint.getInetAddresses(); inetAddrs.hasMoreElements();) {
			InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
			ipAddress = inetAddr.getHostAddress();
			break;
		}
		return ipAddress;
	}
	
	private static String fetchMAC() {
		try {
			return Hex.encodeHexString(connectionEndpoint.getHardwareAddress());
		} catch (SocketException e) {
			System.err.println("MyseliaUUID : error could not fetch mac address");
		}
		
		return null;
	}

	private static long fetchUnixTimestamp() {
		return (System.currentTimeMillis() / 1000L);
	}

	/**
	 * Gets only the relevant network interfaces.
	 * 
	 * @param interfaceList
	 *            List of all the network interfaces present on this system.
	 */
	private void filterInterfaces() throws UnknownHostException {
		ArrayList<NetworkInterface> networkMetaData = new ArrayList<NetworkInterface>();
		try {
			// Iterate all NICs (network interface cards)
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				networkMetaData.add(iface);

				for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();

					if (isGoodIP(inetAddr))
						connectionEndpoint = iface;
				}
			}
		} catch (Exception e) {
			System.err.println("MyseliaUUID : Error fetching network interface manifest");
		}
	}

	private boolean isGoodIP(InetAddress ip) {
		if (ip.isMulticastAddress() || ip.isLoopbackAddress() || ip.isLinkLocalAddress())
			return false;
		return true;
	}


	/*
	 * GETTERS
	 */
	public String getHostName() {
		return hostName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public ComponentType getComponentType() {
		return componentType;
	}

	public long getUnixTimestamp() {
		return unixTimestamp;
	}
	
	public MyseliaUUID getUUID() {
		return UUID;
	}

	public void printData() {
		System.out.println(this);
	}
	
	@Override
	public String toString() {
		return 	"\n[Mycelia Component Cerficate]: " +
		"\n\t->[Type]: " + componentType.toString() + 
		"\n\t->[UUID]: " + this.UUID + 
		"\n\t\t-->[Host]: " + getHostName() +
		"\n\t\t-->[IP]: " + getIpAddress() +
		"\n\t\t-->[MAC]: " + getMacAddress() +
		"\n\t\t-->[Timestamp]: " + getUnixTimestamp() + "\n";
	}

}
