package eu.ha3.matmos.engine.logic.impl;

import eu.ha3.matmos.engine.logic.Condition;

public class SimpleCondition implements Condition
{
	private String sheet;
	private String index;
	private String compareFunction;
	private String valueAgainst;
	
	private boolean truth;
	
	public SimpleCondition(String sheet, String index, String compareFunction,
			String valueAgainst)
	{
		this.sheet = sheet;
		this.index = index;
		this.compareFunction = compareFunction;
		this.valueAgainst = valueAgainst;
		
		truth = false;

	}
	
	@Override
	public String getSheet()
	{
		return sheet;
	}
	
	@Override
	public String getIndex()
	{
		return index;
	}
	
	@Override
	public String getCompareFunction()
	{
		return compareFunction;
	}
	
	@Override
	public String getValueAgainst()
	{
		return valueAgainst;
	}
	
	@Override
	public void setSheet(String sheet)
	{
		this.sheet = sheet;
		
	}
	
	@Override
	public void setIndex(String index)
	{
		this.index = index;
		
	}
	
	@Override
	public void setCompareFunction(String compareFunction)
	{
		this.compareFunction = compareFunction;
		
	}
	
	@Override
	public void setValueAgainst(String valueAgainst)
	{
		this.valueAgainst = valueAgainst;
		
	}
	
	@Override
	public boolean isTrue()
	{
		return truth;
	}
	
	@Override
	public void setTruth(boolean truth)
	{
		this.truth = truth;
	}
	
	
}
