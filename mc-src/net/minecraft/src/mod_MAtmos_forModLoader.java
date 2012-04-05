package net.minecraft.src;

import eu.ha3.mc.mod.Ha3Mod;

public class mod_MAtmos_forModLoader extends Ha3Util_ModLoader
{
	@Override
	Ha3Mod instantiateMod()
	{
		return new MAtMod();
		
	}
	
	@Override
	public String getVersion()
	{
		return "r12 for 1.1.x";
		
	}
	
}
