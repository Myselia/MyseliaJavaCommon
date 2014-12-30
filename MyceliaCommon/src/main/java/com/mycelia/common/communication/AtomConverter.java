package com.mycelia.common.communication;

import java.io.*;

import org.apache.commons.codec.binary.Base64;

import com.mycelia.common.communication.bean.Atom;

/**
 * Converts any kind of objects to and from an Atom (serialized version of an object).
 */
public class AtomConverter
{
	/**
	 * Convert an Atom to the object instance it represents.
	 * 
	 * @param atom
	 * 			The Atom to convert.
	 * @return
	 * 			The object instance represented by the Atom.
	 * 
	 * @throws ClassNotFoundException
	 * 			Thrown if the atom contains a non primary type, custom type, class instance
	 * 			of a class that is not found in the JVM.
	 *  
	 * @throws IOException
	 * 			Thrown if the atom's content is not parsable given its intended type.
	 */
	public Serializable fromAtomInferType(Atom atom) throws ClassNotFoundException, IOException
	{
		if(atom.getAtomClass().equals(Integer.class.getCanonicalName()))
		{
			return getAsInt(atom); 
		}
		else if(atom.getAtomClass().equals(Double.class.getCanonicalName()))
		{
			return getAsDouble(atom); 
		}
		else if(atom.getAtomClass().equals(String.class.getCanonicalName()))
		{
			return getAsString(atom); 
		}
		else if(atom.getAtomClass().equals(Boolean.class.getCanonicalName()))
		{
			return getAsBoolean(atom); 
		}
		else
		{
			return getAsSerializable(atom);
		}
	}
	
	/**
	 * 
	 * @param serializable
	 * @return
	 * 
	 * @throws IOException
	 */
	public Atom toAtomInferType(Serializable serializable) throws IOException
	{
		Atom atom;
		
		if(serializable instanceof Double)
		{
			atom=toAtom((Double)serializable);
		}
		else if(serializable instanceof Integer)
		{
			atom=toAtom((Integer)serializable);
		}
		else if(serializable instanceof String)
		{
			atom=toAtom((String)serializable);
		}
		else if(serializable instanceof Boolean)
		{
			atom=toAtom((Boolean)serializable);
		}
		else
		{
			atom=toAtom(serializable);
		}
		
		return atom;
	}
	
	/**
	 * Converts an integer to an Atom.
	 * 
	 * @param integer
	 * 			Integer to convert.
	 * @return
	 * 			The Atom object representing the integer value.
	 */
	public Atom toAtom(Integer integer)
	{
		Atom atom=new Atom("", Integer.class.getCanonicalName());
		atom.setContent(Integer.toString(integer));

		return atom;
	}
	
	/**
	 * Converts a boolean to an Atom.
	 * 
	 * @param bool
	 * 			Boolean to convert.
	 * @return
	 * 			The Atom object representing the boolean  value.
	 */
	public Atom toAtom(Boolean bool)
	{
		Atom atom=new Atom("", Boolean.class.getCanonicalName());
		atom.setContent(Boolean.toString(bool));

		return atom;
	}
	
	/**
	 * Converts a string to an Atom.
	 * 
	 * @param str
	 * 			String to convert.
	 * @return
	 * 			The Atom object representing the string value.
	 */
	public Atom toAtom(String str)
	{
		Atom atom=new Atom("", String.class.getCanonicalName());
		atom.setContent(str);

		return atom;
	}

	/**
	 * Converts a double to an Atom.
	 * 
	 * @param value
	 * 			Double to convert.
	 * @return
	 * 			The Atom object representing the double value.
	 */
	public Atom toAtom(Double value)
	{
		Atom atom=new Atom("", Double.class.getCanonicalName());
		atom.setContent(Double.toString(value));

		return atom;
	}

	/**
	 * Converts a custom serializable object to an Atom.
	 * 
	 * @param serializable
	 * 			Serializable to convert.
	 * @return
	 * 			The Atom object representing the serializable instance.
	 */
	public Atom toAtom(Serializable serializable) throws IOException
	{
		Atom atom=new Atom("", Serializable.class.getCanonicalName());
		atom.setContent(encodeBase64Atom(serializable));

		return atom;
	}

	/**
	 * Get the atoms value as an integer.
	 * 
	 * @param atom
	 * 			Atom to convert.
	 * @return
	 * 			Integer value represented by the Atom.
	 */
	public Integer getAsInt(Atom atom)
	{
		return (Integer)Integer.parseInt(atom.getContent());
	}
	
	/**
	 * Get the atoms value as an boolean.
	 * 
	 * @param atom
	 * 			Atom to convert.
	 * @return
	 * 			Boolean value represented by the Atom.
	 */
	public Boolean getAsBoolean(Atom atom)
	{
		return (Boolean)Boolean.parseBoolean(atom.getContent());
	}
	
	/**
	 * Get the atoms value as an double.
	 * 
	 * @param atom
	 * 			Atom to convert.
	 * @return
	 * 			Double value represented by the Atom.
	 */
	public Double getAsDouble(Atom atom)
	{
		return (Double)Double.parseDouble(atom.getContent());
	}
	
	/**
	 * Get the atoms value as an string.
	 * 
	 * @param atom
	 * 			Atom to convert.
	 * @return
	 * 			String value represented by the Atom.
	 */
	public String getAsString(Atom atom)
	{
		return atom.getContent();
	}
	
	/**
	 * Reads in the atom object and deserialize it as the custom class it represents.
	 * 
	 * @param atom
	 * 			Atom to convert.
	 * @return
	 * 			Serializable value that is the serialized instance of the custom class
	 * 			contained in this atom.
	 */
	public Serializable getAsSerializable(Atom atom) throws ClassNotFoundException, IOException
	{
		return (Serializable)decodeBase64Atom(atom.getContent());
	}
	
	/**
	 * Deserialize a base64 encoded custom object.
	 * 
	 * @param string
	 * 			Base64 encoded custom object.
	 * @return
	 * 			The deserialized object instance.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Object decodeBase64Atom(String string) throws IOException, ClassNotFoundException
	{
		byte[] data=Base64.decodeBase64(string);
		ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(data));
		Object object=ois.readObject();
		ois.close(); 
		
		return object;
	}

	/**
	 * Serialize a custom object into base64.
	 * 
	 * @param serializable
	 * 		Serializble object to serialize to base64.
	 * 
	 * @return
	 * 			A base64 encoding of the serialized object.
	 * 
	 * @throws IOException
	 */
	private String encodeBase64Atom(Serializable serializable) throws IOException
	{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ObjectOutputStream oos=new ObjectOutputStream(baos);
		oos.writeObject(serializable);
		oos.close();
		
		return new String(Base64.encodeBase64(baos.toByteArray()));
	}
}
