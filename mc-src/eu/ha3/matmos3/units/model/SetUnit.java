package eu.ha3.matmos3.units.model;

import java.util.Set;

public interface SetUnit
{
	/** Truth value of the set. */
	public boolean isTrue();
	
	public Set<String> getTrueSet();
	
	public Set<String> getFalseSet();
	
}
