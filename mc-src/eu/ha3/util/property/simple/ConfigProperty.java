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

import eu.ha3.util.property.contract.ConfigDuet;
import eu.ha3.util.property.contract.PropertyHolder;
import eu.ha3.util.property.contract.Versionnable;

public class ConfigProperty implements PropertyHolder, Versionnable, ConfigDuet
{
	private VersionnableProperty mixed;
	
	private String defaultPath;
	private String userPath;
	
	public ConfigProperty()
	{
		this.mixed = new VersionnableProperty();
		
	}
	
	@Override
	public void setDuet(String defaultPath, String userPath)
	{
		this.defaultPath = defaultPath;
		this.userPath = userPath;
		
	}
	
	@Override
	public void load()
	{
		File defaultFile = new File(this.defaultPath);
		File userFile = new File(this.userPath);
		
		if (defaultFile.exists())
		{
			try
			{
				Reader reader = new FileReader(defaultFile);
				
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
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
			throw new MissingDefaultConfigException();
		
		if (userFile.exists())
		{
			try
			{
				Reader reader = new FileReader(userFile);
				
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
			}
			catch (IOException e)
			{
				e.printStackTrace();
				this.mixed.revert();
			}
		}
		else
		{
			// NO USER FILE
		}
		
	}
	
	@Override
	public void save()
	{
		try
		{
			File userFile = new File(this.userPath);
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
		}
		
	}
	
	@Override
	public void commit()
	{
		this.mixed.commit();
		
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
