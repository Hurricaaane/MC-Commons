package eu.ha3.mc.haddon.implem;

import eu.ha3.mc.haddon.Caster;
import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Utility;

/* x-placeholder-wtfplv2 */

public abstract class HaddonImpl implements Haddon
{
	private Utility utility;
	private Caster caster;
	
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
	public Caster getCaster()
	{
		return this.caster;
	}
	
	@Override
	public void setCaster(Caster caster)
	{
		this.caster = caster;
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
	public Caster caster()
	{
		return getCaster();
	}
}
