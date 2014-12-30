package com.mycelia.common.communication.bean;

/**
 * A single object that is part of a <code>Transmission</code>.
 */
public class Atom
{
	private String atomType;
	
	/**
	 * Class of the atom's content.
	 */
	private String atomClass;
	
	/**
	 * Content value that can be deserialized into an object.
	 */
	private String content;

	public Atom(String atomType, String atomClass)
	{
		this.atomType=atomType;
		this.atomClass=atomClass;
	}

	public void setContent(String content)
	{
		this.content=content;
	}

	public String getContent()
	{
		return content;
	}

	public String getAtomType()
	{
		return atomType;
	}

	public String getAtomClass()
	{
		return atomClass;
	}
	
	@Override
	public String toString()
	{
		return "[class: "+atomClass+", content: \""+content+"\"]";
	}
}
