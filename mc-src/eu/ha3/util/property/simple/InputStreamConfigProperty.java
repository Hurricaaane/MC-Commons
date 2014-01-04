package eu.ha3.util.property.simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import eu.ha3.util.property.contract.ConfigInputStream;
import eu.ha3.util.property.contract.PropertyHolder;
import eu.ha3.util.property.contract.Versionnable;

/*
--filenotes-placeholder
*/

public class InputStreamConfigProperty implements PropertyHolder, Versionnable, ConfigInputStream
{
	private VersionnableProperty mixed;
	
	public InputStreamConfigProperty()
	{
		this.mixed = new VersionnableProperty();
	}
	
	@Override
	public boolean loadStream(InputStream stream)
	{
		try
		{
			Reader reader = new InputStreamReader(stream);
			
			Properties props = new Properties();
			props.load(reader);
			
			for (Entry<Object, Object> entry : props.entrySet())
			{
				this.mixed.setProperty(entry.getKey().toString(), entry.getValue().toString());
				
			}
			this.mixed.commit();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.mixed.revert();
			return false;
		}
		
		return true;
		
	}
	
	@Override
	public boolean commit()
	{
		return this.mixed.commit();
	}
	
	@Override
	public void revert()
	{
		this.mixed.revert();
	}
	
	@Override
	public String getString(String name)
	{
		return this.mixed.getString(name);
	}
	
	@Override
	public boolean getBoolean(String name)
	{
		return this.mixed.getBoolean(name);
	}
	
	@Override
	public int getInteger(String name)
	{
		return this.mixed.getInteger(name);
	}
	
	@Override
	public float getFloat(String name)
	{
		return this.mixed.getFloat(name);
	}
	
	@Override
	public long getLong(String name)
	{
		return this.mixed.getLong(name);
	}
	
	@Override
	public double getDouble(String name)
	{
		return this.mixed.getDouble(name);
	}
	
	@Override
	public void setProperty(String name, Object o)
	{
		this.mixed.setProperty(name, o);
	}
	
	@Override
	public Map<String, String> getAllProperties()
	{
		return this.mixed.getAllProperties();
	}
	
}
