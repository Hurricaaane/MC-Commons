package eu.ha3.matmos3.units.impl;

import eu.ha3.matmos3.engine.model.Engine;
import eu.ha3.matmos3.units.model.Unit;

public abstract class UnitBase implements Unit
{
	protected int id;
	protected String name;
	protected Engine engine;
	
	@Override
	public int getID()
	{
		return id;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public Engine getEngine()
	{
		return engine;
	}
	
}
