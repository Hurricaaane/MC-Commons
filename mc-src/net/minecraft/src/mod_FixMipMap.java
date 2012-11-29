package net.minecraft.src;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

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

public class mod_FixMipMap extends BaseMod
{
	@Override
	public String getVersion() { return ""; }
	
	@Override
	public void load() { ModLoader.setInGameHook(this, true, false); }
	
	// Call on every frame...
	@Override
	public boolean onTickInGame(float sub, Minecraft mc) { GL11.glDisable(GL11.GL_BLEND); return true; }
}
