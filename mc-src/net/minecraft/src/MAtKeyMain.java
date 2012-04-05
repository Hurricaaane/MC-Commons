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

public class MAtKeyMain implements Ha3KeyActions
{
	private MAtUserControl userControl;
	
	MAtKeyMain(MAtUserControl userControlIn)
	{
		userControl = userControlIn;
		
	}
	
	@Override
	public void doBefore()
	{
		// OK, do nothing.
		
	}
	
	@Override
	public void doDuring(int curTime)
	{
		if (curTime == 1)
		{
			userControl.signalPress();
			
		}
		
		if (curTime == 7)
		{
			userControl.beginHold();
			
		}
		
	}
	
	@Override
	public void doAfter(int curTime)
	{
		if (curTime < 6)
		{
			userControl.signalShortPress();
			
		} // Omit frame 7
		else if (curTime > 7)
		{
			userControl.endHold();
			
		}
		
	}
	
}