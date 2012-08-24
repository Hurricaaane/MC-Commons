package eu.ha3.util.property.contract;

public interface Versionnable
{
	public void commit();
	
	public void revert();
}