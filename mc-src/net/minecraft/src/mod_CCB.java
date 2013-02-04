package net.minecraft.src;

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
	private static boolean DEV_MODE_USE_LL_COUNTERPART = false;
	
	public mod_CCB()
	{
		super(!DEV_MODE_USE_LL_COUNTERPART ? new CCBHaddon() : new HaddonEmpty());
	}
	
	/*private static boolean isPresentLiteModCounterpart()
	{
		return (Ha3StaticUtilities.classExists("LiteMod_CCB", this)
			|| Ha3StaticUtilities.classExists("net.minecraft.src.LiteMod_CCB", this));
	}
	private static boolean isInstalledLiteLoader()
	{
		return (Ha3StaticUtilities.classExists("com.mumfrey.liteloader.core.LiteLoader", this));
	}*/
	
	@Override
	public String getVersion()
	{
		return "r0";
	}
	
}
