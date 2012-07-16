package eu.ha3.mc.haddon;

import net.minecraft.src.GuiScreen;

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

public interface SupportsGuiTickEvents
{
	/**
	 * Triggered on each tick outside of a game while the gui tick events are
	 * hooked onto the manager.
	 * 
	 */
	public void onGuiTick(GuiScreen gui);
	
}
