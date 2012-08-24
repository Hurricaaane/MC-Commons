package eu.ha3.util.property.contract;


public interface ConfigDuet
{
	public void setDuet(String defaultPath, String userPath);
	
	public void load();
	
	public void save();
}