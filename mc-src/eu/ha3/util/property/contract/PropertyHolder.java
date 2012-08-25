package eu.ha3.util.property.contract;

import java.util.Map;

public interface PropertyHolder
{
	public String getString(String name);
	
	public boolean getBoolean(String name);
	
	public int getInteger(String name);
	
	public float getFloat(String name);
	
	public long getLong(String name);
	
	public double getDouble(String name);
	
	public void setProperty(String name, Object o);
	
	public Map<String, String> getAllProperties();
	
}
