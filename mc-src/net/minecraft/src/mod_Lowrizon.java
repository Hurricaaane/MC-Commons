package net.minecraft.src;


public class mod_Lowrizon extends HaddonBridgeModLoader
{
	public mod_Lowrizon()
	{
		super(new DisabledHaddon());
		//return new LrzMod();
		
	}
	
	@Override
	public String getVersion()
	{
		return "r0";
		
	}
	
}
