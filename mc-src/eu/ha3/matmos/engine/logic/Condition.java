package eu.ha3.matmos.engine.logic;

public interface Condition
{
	public String getSheet();
	
	public String getIndex();
	
	public String getCompareFunction();
	
	public String getValueAgainst();
	
	
	public void setSheet(String sheet);
	
	public void setIndex(String index);
	
	public void setCompareFunction(String compareFunction);
	
	public void setValueAgainst(String valueAgainst);
	
	
	public boolean isTrue();
	
	public void setTruth(boolean truth);
	
}
