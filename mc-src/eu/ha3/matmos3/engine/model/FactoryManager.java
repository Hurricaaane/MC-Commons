package eu.ha3.matmos3.engine.model;

public interface FactoryManager
{
	public boolean addBuilder(String className, UnitBuilder builder);
	
	public UnitBuilder getBuilder(String className);
	
}
