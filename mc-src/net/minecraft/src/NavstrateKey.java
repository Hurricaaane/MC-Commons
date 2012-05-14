package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3KeyActions;

public class NavstrateKey implements Ha3KeyActions
{
	mod_Navstrate nav;
	
	public NavstrateKey(mod_Navstrate nav)
	{
		this.nav = nav;

	}
	
	@Override
	public void doBefore()
	{
		
	}
	
	@Override
	public void doDuring(int curTime)
	{
		
	}
	
	@Override
	public void doAfter(int curTime)
	{
		if (curTime < 5)
		{
			if (!nav.isOn())
			{
				nav.rescan();
				
			}
			else
			{
				nav.performSnapshot();
				
			}
			
		}
		else
			nav.toggle();
		
	}
	
}
