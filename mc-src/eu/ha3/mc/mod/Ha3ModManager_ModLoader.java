package eu.ha3.mc.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.ModLoader;

public class Ha3ModManager_ModLoader implements Ha3ModManager
{
	private Ha3Mod mod;
	private Minecraft mc;
	private boolean usesFrame;
	
	//private boolean usesKeyBindingEvent;
	
	public Ha3ModManager_ModLoader()
	{
		mc = null;
		usesFrame = false;
		//usesKeyBindingEvent = false;
		
	}
	
	@Override
	public void setMod(Ha3Mod modIn)
	{
		mod = modIn;
		
	}
	
	@Override
	public Minecraft getMinecraft()
	{
		if (mc == null)
			mc = ModLoader.getMinecraftInstance();
		
		return mc;
		
	}
	
	@Override
	public void communicateFrame(float fspan)
	{
		if (usesFrame)
			mod.core().doFrame(fspan);
		
	}
	
	@Override
	public void communicateKeyBindingEvent(KeyBinding event)
	{
		//if (usesKeyBindingEvent)
		mod.core().doKeyBindingEvent(event);
		
	}
	
	@Override
	public void communicateManagerReady()
	{
		mod.core().doManagerReady();
		
	}
	
	@Override
	public void setUsesFrame(boolean enable)
	{
		usesFrame = enable;
		
		if (enable)
			ModLoader.setInGameHook((BaseMod) mod.reference(), true, false);
		
		else
			ModLoader.setInGameHook((BaseMod) mod.reference(), false, false); // TOFO Check if this is correct
		
	}
	
	/*@Override
	public void setUsesKeyBindingEvent(boolean enable)
	{
		usesKeyBindingEvent = enable;
		
	}*/
	
	@Override
	public boolean getUsesFrame()
	{
		return usesFrame;
		
	}
	
	/*@Override
	public boolean getUsesKeyBindingEvent()
	{
		return usesKeyBindingEvent;
	}*/
	
	@Override
	public void addKeyBinding(KeyBinding keyBindingIn, String localization)
	{
		ModLoader.addLocalization(keyBindingIn.keyDescription, localization);
		ModLoader.registerKey((BaseMod) mod.reference(), keyBindingIn, true);
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, int zeroOffsets)
					throws Ha3ModPrivateAccessException
					{
		try
		{
			return ModLoader.getPrivateValue(classToPerformOn,
					instanceToPerformOn, zeroOffsets);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			throw new Ha3ModPrivateAccessException(
					"getPrivateValue has failed: IllegalArgument");
			
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
			throw new Ha3ModPrivateAccessException(
					"getPrivateValue has failed: Security");
			
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
			throw new Ha3ModPrivateAccessException(
					"getPrivateValue has failed: NoSuchField");
			
		}
		
					}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, int zeroOffsets, Object newValue)
					throws Ha3ModPrivateAccessException
					{
		try
		{
			ModLoader.setPrivateValue(classToPerformOn, instanceToPerformOn,
					zeroOffsets, newValue);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			throw new Ha3ModPrivateAccessException(
					"setPrivateValue has failed: IllegalArgument");
			
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
			throw new Ha3ModPrivateAccessException(
					"setPrivateValue has failed: Security");
			
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
			throw new Ha3ModPrivateAccessException(
					"setPrivateValue has failed: NoSuchField");
			
		}
		
					}
	
}
