package eu.ha3.util.property.simple;

import java.util.Map;

import eu.ha3.util.property.contract.PropertyHolder;
import eu.ha3.util.property.contract.Versionnable;

public class VersionnableProperty implements PropertyHolder, Versionnable
{
	private PropertyHolder soft;
	private PropertyHolder hard;
	
	public VersionnableProperty()
	{
		this.soft = new PropertyCell();
		this.hard = new PropertyCell();
	}
	
	@Override
	public void commit()
	{
		this.hard.getAllProperties().putAll(this.soft.getAllProperties());
		this.soft.getAllProperties().clear();
	}
	
	@Override
	public void revert()
	{
		this.soft.getAllProperties().clear();
	}
	
	@Override
	public String getString(String name)
	{
		try
		{
			return this.soft.getString(name);
		}
		catch (PropertyMissingException e)
		{
			return this.hard.getString(name);
		}
		/*catch (PropertyTypeException e)
		{
			return this.hard.getString(name);
		}*/
	}
	
	@Override
	public boolean getBoolean(String name)
	{
		try
		{
			return this.soft.getBoolean(name);
		}
		catch (PropertyMissingException e)
		{
			return this.hard.getBoolean(name);
		}
		catch (PropertyTypeException e)
		{
			return this.hard.getBoolean(name);
		}
	}
	
	@Override
	public int getInteger(String name)
	{
		try
		{
			return this.soft.getInteger(name);
		}
		catch (PropertyMissingException e)
		{
			return this.hard.getInteger(name);
		}
		catch (PropertyTypeException e)
		{
			return this.hard.getInteger(name);
		}
	}
	
	@Override
	public float getFloat(String name)
	{
		try
		{
			return this.soft.getFloat(name);
		}
		catch (PropertyMissingException e)
		{
			return this.hard.getFloat(name);
		}
		catch (PropertyTypeException e)
		{
			return this.hard.getFloat(name);
		}
	}
	
	@Override
	public long getLong(String name)
	{
		try
		{
			return this.soft.getLong(name);
		}
		catch (PropertyMissingException e)
		{
			return this.hard.getLong(name);
		}
		catch (PropertyTypeException e)
		{
			return this.hard.getLong(name);
		}
	}
	
	@Override
	public double getDouble(String name)
	{
		try
		{
			return this.soft.getDouble(name);
		}
		catch (PropertyMissingException e)
		{
			return this.hard.getDouble(name);
		}
		catch (PropertyTypeException e)
		{
			return this.hard.getDouble(name);
		}
	}
	
	@Override
	public void setProperty(String name, Object o)
	{
		this.soft.setProperty(name, o);
	}
	
	@Override
	public Map<String, String> getAllProperties()
	{
		return this.hard.getAllProperties();
	}
	
}
