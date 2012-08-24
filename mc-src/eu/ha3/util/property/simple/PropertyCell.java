package eu.ha3.util.property.simple;

import java.util.HashMap;
import java.util.Map;

import eu.ha3.util.property.contract.PropertyHolder;

public class PropertyCell implements PropertyHolder
{
	private Map<String, String> properties;
	
	public PropertyCell()
	{
		this.properties = new HashMap<String, String>();
	}
	
	@Override
	public String getString(String name)
	{
		if (!this.properties.containsKey(name))
			throw new PropertyMissingException();
		
		return this.properties.get(name);
	}
	
	@Override
	public int getInteger(String name)
	{
		if (!this.properties.containsKey(name))
			throw new PropertyMissingException();
		
		try
		{
			return Integer.parseInt(this.properties.get(name));
		}
		catch (NumberFormatException e)
		{
			throw new PropertyTypeException();
		}
	}
	
	@Override
	public float getFloat(String name)
	{
		if (!this.properties.containsKey(name))
			throw new PropertyMissingException();
		
		try
		{
			return Float.parseFloat(this.properties.get(name));
		}
		catch (NumberFormatException e)
		{
			throw new PropertyTypeException();
		}
	}
	
	@Override
	public long getLong(String name)
	{
		if (!this.properties.containsKey(name))
			throw new PropertyMissingException();
		
		try
		{
			return Long.parseLong(this.properties.get(name));
		}
		catch (NumberFormatException e)
		{
			throw new PropertyTypeException();
		}
	}
	
	@Override
	public double getDouble(String name)
	{
		if (!this.properties.containsKey(name))
			throw new PropertyMissingException();
		
		try
		{
			return Double.parseDouble(this.properties.get(name));
		}
		catch (NumberFormatException e)
		{
			throw new PropertyTypeException();
		}
	}
	
	@Override
	public void setProperty(String name, Object o)
	{
		this.properties.put(name, o.toString());
	}
	
	@Override
	public Map<String, String> getAllProperties()
	{
		return this.properties;
	}
}
