package eu.ha3.util.property.contract;

public interface ConfigSource
{
	public void setSource(String path);
	
	public boolean load();
	
	public boolean save();
}