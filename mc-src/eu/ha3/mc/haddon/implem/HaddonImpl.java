package eu.ha3.mc.haddon.implem;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Manager;
import eu.ha3.mc.haddon.Utility;

/* x-placeholder-wtfplv2 */

public abstract class HaddonImpl implements Haddon
{
	private Manager manager;
	
	@Override
	public Manager getManager()
	{
		return this.manager;
	}
	
	@Override
	public void setManager(Manager manager)
	{
		this.manager = manager;
		
	}
	
	/**
	 * Convenience shortener for getUtility()
	 * 
	 * @return
	 */
	public Utility util()
	{
		return getManager().getUtility();
		
	}
	
	/**
	 * Convenience shortener for getManager()
	 * 
	 * @return
	 */
	public Manager manager()
	{
		return getManager();
		
	}
	
}
