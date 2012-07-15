package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3KeyActions;

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
