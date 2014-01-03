package eu.ha3.mc.haddon.implem;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Utility;

/* x-placeholder-wtfplv2 */

public abstract class HaddonImpl implements Haddon
{
	private Utility utility;
	
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
	
	/**
	 * Convenience shortener for getUtility()
	 * 
	 * @return
	 */
	public Utility util()
	{
		return getUtility();
	}
}
