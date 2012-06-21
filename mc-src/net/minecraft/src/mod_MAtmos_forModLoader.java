package net.minecraft.src;

/*
public class mod_MAtmos_forModLoader extends Ha3Util_ModLoader
{
	@Override
	Ha3Mod instantiateMod()
	{
		//return new DisabledHa3Mod();
		return new MAtMod();
		
	}
	
	@Override
	public String getVersion()
	{
		return "r13 for 1.2.6";
		
	}
	
}
 */

public class mod_MAtmos_forModLoader extends HaddonBridgeModLoader
{
	public mod_MAtmos_forModLoader()
	{
		super(new MAtMod());
		
	}
	
	@Override
	public String getVersion()
	{
		return "r13 for 1.2.6";
		
	}
	
}