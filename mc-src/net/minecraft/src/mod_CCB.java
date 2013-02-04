package net.minecraft.src;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3StaticUtilities;

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

public class mod_CCB extends HaddonBridgeModLoader
{
	//private static boolean DEV_MODE_USE_LL_COUNTERPART = true;
	
	public mod_CCB()
	{
		super(!isInstalledInDouble() ? new CCBHaddon() : new HaddonEmpty());
		
		if (isInstalledInDouble())
		{
			System.out.println("Detected CCB was installed in double. Not starting ModLoader version");
		}
	}
	
	private static boolean isInstalledInDouble()
	{
		return isPresentLiteModCounterpart(Minecraft.getMinecraft()) && isInstalledLiteLoader(Minecraft.getMinecraft());
	}
	
	private static boolean isPresentLiteModCounterpart(Minecraft mc)
	{
		return Ha3StaticUtilities.classExists("LiteMod_CCB", mc)
			|| Ha3StaticUtilities.classExists("net.minecraft.src.LiteMod_CCB", mc);
	}
	
	private static boolean isInstalledLiteLoader(Minecraft mc)
	{
		return Ha3StaticUtilities.classExists("com.mumfrey.liteloader.core.LiteLoader", mc);
	}
	
	@Override
	public String getVersion()
	{
		return "r0";
	}
	
}
