package eu.ha3.matmos3.subcomp.model;

import eu.ha3.matmos3.units.model.VariableUnit;

public interface NumberVariable extends VariableUnit
{
	public int getInteger();
	
	public int getLong();
	
	public float getFloat();
	
	public double getDouble();
	
	public Object getNumber();
	
}
