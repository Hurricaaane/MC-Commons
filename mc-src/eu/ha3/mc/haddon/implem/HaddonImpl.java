package eu.ha3.mc.haddon.implem;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Operator;
import eu.ha3.mc.haddon.Utility;

/* x-placeholder-wtfplv2 */

public abstract class HaddonImpl implements Haddon
{
	private Utility utility;
	private Operator operator;
	
	@Override
	public Utility getUtility()
	{
		return this.utility;
	}
	
	@Override
	public void setUtility(Utility utility)
	{
		this.utility = utility;
	}
	
	@Override
	public Operator getOperator()
	{
		return this.operator;
	}
	
	@Override
	public void setOperator(Operator operator)
	{
		this.operator = operator;
	}
	
	/**
	 * Convenience shortener for getUtility()
	 * 
	 * @return
	 */
	public Utility util()
	{
		return getUtility();
	}
	
	/**
	 * Convenience shortener for getCaster()
	 * 
	 * @return
	 */
	public Operator op()
	{
		return getOperator();
	}
}
