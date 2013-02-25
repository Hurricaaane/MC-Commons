package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import eu.ha3.util.property.contract.PropertyHolder;
import eu.ha3.util.property.contract.Versionnable;
import eu.ha3.util.property.simple.VersionnableProperty;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class ATConfigFromStream implements PropertyHolder, Versionnable
{
	private VersionnableProperty mixed;
	
	public ATConfigFromStream()
	{
		this.mixed = new VersionnableProperty();
	}
	
	public boolean loadFromStream(InputStream stream)
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
