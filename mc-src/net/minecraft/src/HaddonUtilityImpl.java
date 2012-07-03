package net.minecraft.src;

import org.lwjgl.input.Keyboard;

import eu.ha3.mc.haddon.Manager;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.Utility;

public class HaddonUtilityImpl implements Utility
{
	final private static int WORLD_HEIGHT = 256;
	
	private Manager manager;
	
	public HaddonUtilityImpl(Manager manager)
	{
		this.manager = manager;
		
		// Initialize field modifiers
		HaddonUtilitySingleton.getInstance();
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, int zeroOffsets)
					throws PrivateAccessException
					{
		return HaddonUtilitySingleton.getInstance().getPrivateValue(
				classToPerformOn, instanceToPerformOn, zeroOffsets);
					}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, int zeroOffsets, Object newValue)
					throws PrivateAccessException
					{
		HaddonUtilitySingleton.getInstance().setPrivateValue(classToPerformOn,
				instanceToPerformOn, zeroOffsets, newValue);
					}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, String obfPriority, int zeroOffsetsDebug)
					throws PrivateAccessException
					{
		Object ret;
		try
		{
			ret = HaddonUtilitySingleton.getInstance().getPrivateValueViaName(
					classToPerformOn, instanceToPerformOn, obfPriority);
			
		}
		catch (Exception e)
		{
			ret = HaddonUtilitySingleton.getInstance().getPrivateValue(
					classToPerformOn, instanceToPerformOn, zeroOffsetsDebug); // This throws a PrivateAccessException
			
		}
		
		return ret;
					}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, String obfPriority,
			int zeroOffsetsDebug, Object newValue)
					throws PrivateAccessException
					{
		try
		{
			HaddonUtilitySingleton.getInstance().setPrivateValueViaName(
					classToPerformOn, instanceToPerformOn, obfPriority,
					newValue);
			
		}
		catch (PrivateAccessException e)
		{
			HaddonUtilitySingleton.getInstance().setPrivateValue(
					classToPerformOn, instanceToPerformOn, zeroOffsetsDebug,
					newValue); // This throws a PrivateAccessException
			
		}
					}
	
	@Override
	public int getWorldHeight()
	{
		return WORLD_HEIGHT;
		
	}
	
	@Override
	public int getClientTick()
	{
		try
		{
			// XXX: IMPL_UPDATE_OBF
			return (Integer) getPrivateValue(
					net.minecraft.client.Minecraft.class, manager
					.getMinecraft(), 26); // private int ticksRan;
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
			return -1;
			
		}
		
	}
	
	@Override
	public Object getCurrentScreen()
	{
		return manager.getMinecraft().currentScreen;
		
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean isCurrentScreen(final Class classtype)
	{
		Object current = getCurrentScreen();
		
		if (classtype == null)
			return current == null;
		
		if (current == null)
			return false;
		
		return classtype.isInstance(current);
		
	}
	
	@Override
	public void closeCurrentScreen()
	{
		manager.getMinecraft().displayGuiScreen(null);
		
	}
	
	@Override
	public void printChat(Object... args)
	{
		if (manager.getMinecraft().thePlayer == null)
			return;
		
		StringBuilder builder = new StringBuilder();
		for (Object o : args)
		{
			builder.append(o);
		}
		manager.getMinecraft().thePlayer.addChatMessage(builder.toString());
		
	}
	
	@Override
	public boolean areKeysDown(int... args)
	{
		for (int arg : args)
		{
			if (!Keyboard.isKeyDown(arg))
				return false;
			
		}
		
		return true;
		
	}
	
}
