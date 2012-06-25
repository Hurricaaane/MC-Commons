package eu.ha3.matmos3.engine.model;

import eu.ha3.matmos3.units.model.Unit;

public interface Engine
{
	public Unit getUnit(String unitName);
	
	public Unit getUnit(int unitId);
	
	public Sheet obtainSheet(String coreName);
}
