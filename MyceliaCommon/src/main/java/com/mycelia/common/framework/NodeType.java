package com.mycelia.common.framework;

public enum NodeType
{
	MASTER("master"),
	SLAVE("slave");
	
	private String name;
	
	private NodeType(String name)
	{
		this.name=name;
	}
	
	public static NodeType getNodeType(String name)
	{
		for(NodeType nodeType: values())
			if(nodeType.getName().equals(name))
				return nodeType;
		
		throw new IllegalArgumentException("no "+NodeType.class.getSimpleName()+" with name \""+name+"\"");
	}
	
	public String getName()
	{
		return name;
	}
}
