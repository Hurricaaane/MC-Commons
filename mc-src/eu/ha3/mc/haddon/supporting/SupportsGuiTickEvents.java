package eu.ha3.mc.haddon;

import net.minecraft.client.gui.GuiScreen;

/* x-placeholder-wtfplv2 */

public interface SupportsGuiTickEvents
{
	/**
	 * Triggered on each tick outside of a game while the gui tick events are
	 * hooked onto the manager.
	 * 
	 */
	public void onGuiTick(GuiScreen gui);
	
}
