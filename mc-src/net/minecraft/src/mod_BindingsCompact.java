package net.minecraft.src;

public class mod_BindingsCompact extends HaddonBridgeModLoader
{
	public mod_BindingsCompact()
	{
		super(new BindingsCompactHaddon());
	}
	
	@Override
	public String getVersion()
	{
		return "r4 for 1.4.6";
		
	}
	
}
