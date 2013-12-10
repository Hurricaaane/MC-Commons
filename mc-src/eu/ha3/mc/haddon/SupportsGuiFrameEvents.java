package eu.ha3.mc.haddon;

import net.minecraft.src.GuiScreen;

/* x-placeholder-wtfplv2 */

public interface SupportsGuiFrameEvents
{
	/**
	 * Triggered on each tick outside of a game while the gui tick events are
	 * hooked onto the manager.
	 * 
	 * @param semi
	 *            Intra-tick time, from 0f to 1f
	 */
	public void onGuiFrame(GuiScreen gui, float semi);
	
}
