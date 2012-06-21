package net.minecraft.src;

import eu.ha3.mc.mod.Ha3Mod;
import eu.ha3.mc.mod.Ha3ModCore;

public class LrzModCore extends Ha3ModCore
{
	private LrzMod mod;
	private Ha3Utility utility;
	
	private LrzWorldCacheI worldCache;
	
	@Override
	public void setMod(Ha3Mod modIn)
	{
		mod = (LrzMod) modIn;
		
	}
	
	@Override
	public void load()
	{
		mod.manager().setUsesFrame(true);
		utility = new Ha3Utility(mod);
		
		worldCache = new LrzWorldCache(8, 64, "poland", mod);
		
	}
	
	@Override
	public void doFrame(float fspan)
	{
		if (utility.getClientTick() % 50 != 0)
			return;
		
		EntityPlayer player = mod.manager().getMinecraft().thePlayer;
		if (player != null)
		{
			for (int i = -6; i < 6; i++)
				for (int j = -6; j < 6; j++)
					worldCache.requestAverage((int) player.posX + i * 16,
							(int) player.posZ + j * 16);
			
			worldCache.save();
			
		}
		
	}
	
	@Override
	public void doKeyBindingEvent(KeyBinding event)
	{
	}
	
	@Override
	public void doManagerReady()
	{
	}
	
	public Ha3Utility util()
	{
		return utility;
	}
	
}
