package com.myselia.javacommon.communication.units;

import java.io.Serializable;

public class Message implements Serializable{
	
	private String addressee;
	private String title;
	private String content;
	
	public Message(String addressee, String title, String content){
		this.addressee = addressee;
		this.title = title;
		this.content = content;
	}
	
	public String getAddressee(){
		return addressee;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getContent(){
		return content;
	}

}
