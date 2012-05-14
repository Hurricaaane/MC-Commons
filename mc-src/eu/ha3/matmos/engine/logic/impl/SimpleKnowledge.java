package eu.ha3.matmos.engine.logic.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.ha3.matmos.engine.logic.Condition;
import eu.ha3.matmos.engine.logic.Knowledge;
import eu.ha3.matmos.engine.logic.Sheet;

public class SimpleKnowledge implements Knowledge
{
	private Map<String, Sheet> sheets;
	private Map<String, Condition> conditions;
	private Map<String, Sheet> sets;
	private Map<String, Sheet> machines;
	
	public SimpleKnowledge()
	{
		sheets = new HashMap<String, Sheet>();
		conditions = new HashMap<String, Condition>();
		sets = new HashMap<String, Sheet>();
		machines = new HashMap<String, Sheet>();
		
	}
	
	@Override
	public void evaluate()
	{
		
		
	}
	
	@Override
	public void addSheet(String name, Sheet sheetFormat)
	{
		sheets.put(name, sheetFormat);
		
	}
	
	@Override
	public void addCondition(String name, String sheet, String index,
			String compareFunction, String value)
	{
		conditions.put(name, new SimpleCondition(sheet, index, compareFunction,
				value));
		
	}
	
	@Override
	public void addSet(String name, List<String> isTrue, List<String> isFalse)
	{
		sets.put(name, new SimpleSet(isTrue, isFalse));
		
	}
	
	@Override
	public void addMachine(String name, List<String> fuels, List<String> brakes)
	{
		machines.put(name, new SimpleMachine(fuels, brakes));
		
	}
	
	@Override
	public void removeSheet(String name)
	{
		sheets.remove(name);
		
	}
	
	@Override
	public void removeCondition(String name)
	{
		conditions.remove(name);
		
	}
	
	@Override
	public void removeSet(String name)
	{
		sets.remove(name);
		
	}
	
	@Override
	public void removeMachine(String name)
	{
		machines.remove(name);
		
	}
	
}
