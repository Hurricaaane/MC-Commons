package net.minecraft.src;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Manager;
import eu.ha3.mc.haddon.Utility;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

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
