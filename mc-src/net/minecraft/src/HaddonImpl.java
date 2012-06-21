package net.minecraft.src;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Manager;
import eu.ha3.mc.haddon.Utility;

public abstract class HaddonImpl implements Haddon
{
	private Manager manager;
	
	@Override
	public Manager getManager()
	{
		return manager;
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
		return this.getManager().getUtility();
		
	}
	
	/**
	 * Convenience shortener for getManager()
	 * 
	 * @return
	 */
	public Manager manager()
	{
		return this.getManager();
		
	}
	
}
