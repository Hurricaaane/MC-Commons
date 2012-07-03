package net.minecraft.src;

import eu.ha3.mc.haddon.SupportsKeyEvents;

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
