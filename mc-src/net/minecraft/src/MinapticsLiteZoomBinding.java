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

public class MinapticsLiteZoomBinding implements Ha3KeyActions
{
	MinapticsLiteHaddon mod;
	
	MinapticsLiteZoomBinding(MinapticsLiteHaddon mod)
	{
		super();
		this.mod = mod;
		
	}
	
	@Override
	public void doBefore()
	{
		this.mod.zoomDoBefore();
		
	}
	
	@Override
	public void doDuring(int curTime)
	{
		this.mod.zoomDoDuring(curTime);
		
	}
	
	@Override
	public void doAfter(int curTime)
	{
		this.mod.zoomDoAfter(curTime);
		
	}
	
}
