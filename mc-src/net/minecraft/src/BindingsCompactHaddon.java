package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3EdgeModel;
import eu.ha3.mc.convenience.Ha3EdgeTrigger;
import eu.ha3.mc.haddon.SupportsTickEvents;

public class BindingsCompactHaddon extends HaddonImpl implements
SupportsTickEvents
{
	private Ha3EdgeTrigger bindTrigger;
	
	@Override
	public void onLoad()
	{
		manager().hookTickEvents(true);
		bindTrigger = new Ha3EdgeTrigger(new Ha3EdgeModel() {
			@Override
			public void onTrueEdge()
			{
			}
			
			@Override
			public void onFalseEdge()
			{
				display();
				
			}
			
		});
		
	}
	
	protected void display()
	{
		if (util().isCurrentScreen(null))
			manager().getMinecraft().displayGuiScreen(
					new BindindsCompactGUI(null,
							manager().getMinecraft().gameSettings));
		
	}
	
	@Override
	public void onTick()
	{
		// ctrl shift B
		bindTrigger.signalState(util().areKeysDown(29, 42, 48));
		
	}
	
}
