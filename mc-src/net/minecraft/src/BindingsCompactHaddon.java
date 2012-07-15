package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3EdgeModel;
import eu.ha3.mc.convenience.Ha3EdgeTrigger;
import eu.ha3.mc.haddon.SupportsTickEvents;

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

public class BindingsCompactHaddon extends HaddonImpl implements SupportsTickEvents
{
	private Ha3EdgeTrigger bindTrigger;
	
	@Override
	public void onLoad()
	{
		manager().hookTickEvents(true);
		this.bindTrigger = new Ha3EdgeTrigger(new Ha3EdgeModel() {
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
				new BindindsCompactGUI(null, manager().getMinecraft().gameSettings));
		}
		
	}
	
	@Override
	public void onTick()
	{
		// ctrl shift B
		this.bindTrigger.signalState(util().areKeysDown(29, 42, 48));
		
	}
	
}
