package net.minecraft.src;

import eu.ha3.mc.mod.Ha3Mod;

public class mod_Lowrizon extends Ha3Util_ModLoader
{
	@Override
	Ha3Mod instantiateMod()
	{
		return new DisabledHa3Mod();
		//return new LrzMod();
		
	}
	
	@Override
	public String getVersion()
	{
		return "r0";
		
	}
	
}
