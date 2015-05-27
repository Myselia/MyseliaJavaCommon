package com.myselia.javacommon.communication.units;

public class Header {
	private int id;
	private String from;
	private String to;

	public Header(int id, String from, String to) {
		this.id = id;
		this.from = from;
		this.to = to;
	}

	public int get_id() {
		return id;
	}

	public String get_from() {
		return from;
	}

	public String get_to() {
		return to;
	}

}
