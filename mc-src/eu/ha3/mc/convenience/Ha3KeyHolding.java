package eu.ha3.mc.convenience;

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

public class Ha3KeyHolding implements Ha3KeyActions
{
	private final Ha3HoldActions holdActions;
	private final int tippingPoint;
	
	private boolean isHolding;
	
	public Ha3KeyHolding(Ha3HoldActions holdActions, int tippingPoint)
	{
		this.holdActions = holdActions;
		this.tippingPoint = tippingPoint;
	}
	
	@Override
	public void doBefore()
	{
		this.holdActions.beginPress();
	}
	
	@Override
	public void doDuring(int curTime)
	{
		if (curTime >= this.tippingPoint && !this.isHolding)
		{
			this.isHolding = true;
			
			this.holdActions.beginHold();
		}
	}
	
	@Override
	public void doAfter(int curTime)
	{
		if (curTime < this.tippingPoint)
		{
			this.holdActions.shortPress();
		}
		else if (this.isHolding)
		{
			this.isHolding = false;
			
			this.holdActions.endHold();
		}
		this.holdActions.endPress();
	}
	
}