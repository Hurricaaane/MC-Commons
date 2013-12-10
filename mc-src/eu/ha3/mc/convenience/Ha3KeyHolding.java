package eu.ha3.mc.convenience;

/* x-placeholder-wtfplv2 */

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