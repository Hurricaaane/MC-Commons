package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3KeyActions;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
 */

public class MinapticsLiteZoomBinding implements Ha3KeyActions
{
	mod_MinapticsLite mod;
	
	MinapticsLiteZoomBinding(mod_MinapticsLite mod)
	{
		super();
		this.mod = mod;
		
	}
	
	@Override
	public void doBefore()
	{
		(mod).zoomDoBefore();

	}
	
	@Override
	public void doDuring(int curTime)
	{
		(mod).zoomDoDuring(curTime);
		
	}
	
	@Override
	public void doAfter(int curTime)
	{
		(mod).zoomDoAfter(curTime);
		
	}
	
	
}
