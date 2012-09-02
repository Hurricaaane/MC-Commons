package net.minecraft.src;

import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.haddon.SupportsTickEvents;

public class BindingsCompactHaddon extends HaddonImpl implements SupportsTickEvents
{
	private EdgeTrigger bindTrigger;
	
	@Override
	public void onLoad()
	{
		manager().hookTickEvents(true);
		this.bindTrigger = new EdgeTrigger(new EdgeModel() {
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
		{
			manager().getMinecraft().displayGuiScreen(
				new BindingsCompactGUI(null, manager().getMinecraft().gameSettings));
		}
		
	}
	
	@Override
	public void onTick()
	{
		// ctrl shift B
		this.bindTrigger.signalState(util().areKeysDown(29, 42, 48));
		
	}
	
}
