package net.minecraft.src;

import eu.ha3.mc.haddon.SupportsKeyEvents;

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

public class PlaceholderGUIHaddon extends HaddonImpl implements
SupportsKeyEvents
{
	private KeyBinding bind;
	
	@Override
	public void onLoad()
	{
		bind = new KeyBinding("", 13);
		
		manager().addKeyBinding(bind, "Placeholder GUI");
	}
	
	@Override
	public void onKey(KeyBinding event)
	{
		if (event == bind && event.pressed)
		{
			if (util().isCurrentScreen(PlaceholderGUI.class))
			{
				util().closeCurrentScreen();
				
			}
			else if (util().isCurrentScreen(null))
			{
				manager().getMinecraft().displayGuiScreen(new PlaceholderGUI());
				
			}
			
			
			
		}
		
	}
	
}
