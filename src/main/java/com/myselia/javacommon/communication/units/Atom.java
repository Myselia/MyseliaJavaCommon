package com.myselia.javacommon.communication.units;

public class Atom {
	private String field;
	private String type;
	private String value;

	public Atom(String field, String type, String value) {
		this.field = field;
		this.type = type;
		this.value = value;
	}

	public String get_field() {
		return field;
	}

	public String get_type() {
		return type;
	}

	public String get_value() {
		return value;
	}

}
