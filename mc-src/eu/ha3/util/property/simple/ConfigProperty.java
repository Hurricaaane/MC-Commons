package eu.ha3.util.property.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import eu.ha3.util.property.contract.ConfigSource;
import eu.ha3.util.property.contract.PropertyHolder;
import eu.ha3.util.property.contract.Versionnable;

public class ConfigProperty implements PropertyHolder, Versionnable, ConfigSource
{
	private VersionnableProperty mixed;
	
	private String path;
	
	public ConfigProperty()
	{
		this.mixed = new VersionnableProperty();
		
	}
	
	@Override
	public void setSource(String path)
	{
		this.path = path;
		
	}
	
	@Override
	public boolean load()
	{
		File file = new File(this.path);
		
		if (file.exists())
		{
			try
			{
				Reader reader = new FileReader(file);
				
				Properties props = new Properties();
				props.load(reader);
				
				for (Entry<Object, Object> entry : props.entrySet())
				{
					this.mixed.setProperty(entry.getKey().toString(), entry.getValue().toString());
					
				}
				this.mixed.commit();
				
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				this.mixed.revert();
				return false;
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
				this.mixed.revert();
				return false;
				
			}
		}
		else
			return false;
		
		return true;
		
	}
	
	@Override
	public boolean save()
	{
		try
		{
			File userFile = new File(this.path);
			Properties props = new Properties();
			for (Entry<String, String> property : this.mixed.getAllProperties().entrySet())
			{
				props.setProperty(property.getKey(), property.getValue());
				
			}
			
			props.store(new FileWriter(userFile), "");
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
