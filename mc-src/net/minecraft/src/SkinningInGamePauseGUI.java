package net.minecraft.src;

import org.lwjgl.opengl.Display;

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
