package com.mycelia.common.communication.units;

public class Header {
	private int id;
	private int opcode;

	private String from_type;
	private int from_value;
	private String to_type;
	private int to_value;

	public Header(int id, int opcode, String from, String to) {
		this.id = id;
		this.opcode = opcode;
		this.from_type = get_part(from, 0);
		this.from_value = Integer.parseInt(get_part(from, 1));
		this.to_type = get_part(to, 0);
		this.to_value = Integer.parseInt(get_part(to, 1));
	}

	public static String get_part(String data, int split) {
		String[] split_data = data.split(":");
		if (split_data.length > 1) {
			return split_data[split];
		} else {
			return "-1";
		}
	}

	public int get_id() {
		return id;
	}

	public int get_opcode() {
		return opcode;
	}

	public String get_from() {
		return from_type + ":" + from_value;
	}

	public String get_to() {
		return to_type + ":" + to_value;
	}

}
