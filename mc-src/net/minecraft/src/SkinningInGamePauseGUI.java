package net.minecraft.src;

import org.lwjgl.opengl.Display;

public class SkinningInGamePauseGUI extends GuiScreen
{
	@Override
	public void drawDefaultBackground()
	{
	}
	
	@Override
	public void updateScreen()
	{
		if (Display.isActive())
			this.mc.displayGuiScreen(null);
		
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
}
