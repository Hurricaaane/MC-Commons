package net.minecraft.src;


public class mod_Lowrizon extends HaddonBridgeModLoader
{
	public mod_Lowrizon()
	{
		super(new HaddonImpl() {
			
			@Override
			public void onLoad()
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onInitialize()
			{
				// TODO Auto-generated method stub
				
			}
		});
		//return new DisabledHa3Mod();
		//return new LrzMod();
		
	}
	
	@Override
	public String getVersion()
	{
		return "r0";
		
	}
	
}
