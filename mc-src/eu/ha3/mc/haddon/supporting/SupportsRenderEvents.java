package eu.ha3.mc.haddon.supporting;

import net.minecraft.client.gui.GuiScreen;

/*
--filenotes-placeholder
*/

public interface SupportsRenderEvents
{
	public void onRender();
	
	public void onRenderGui(GuiScreen currentScreen);
	
	public void onRenderWorld();
	
	public void onSetupCameraTransform();
}
