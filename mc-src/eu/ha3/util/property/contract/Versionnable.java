package eu.ha3.util.property.contract;

public interface Versionnable
{
	public boolean commit();
	
	public void revert();
}