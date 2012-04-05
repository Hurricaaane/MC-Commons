package net.minecraft.src;

import eu.ha3.mc.mod.Ha3Mod;
import eu.ha3.mc.mod.Ha3ModPrivateAccessException;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
 */

public class Ha3Utility
{
	final public static String COLOR_BLACK = "§0";
	final public static String COLOR_DARKBLUE = "§1";
	final public static String COLOR_DARKGREEN = "§2";
	final public static String COLOR_TEAL = "§3";
	final public static String COLOR_DARKRED = "§4";
	final public static String COLOR_PURPLE = "§5";
	final public static String COLOR_GOLD = "§6";
	final public static String COLOR_GRAY = "§7";
	final public static String COLOR_DARKGRAY = "§8";
	final public static String COLOR_BLUE = "§9";
	final public static String COLOR_BRIGHTGREEN = "§a";
	final public static String COLOR_AQUA = "§b";
	final public static String COLOR_RED = "§c";
	final public static String COLOR_PINK = "§d";
	final public static String COLOR_YELLOW = "§e";
	final public static String COLOR_WHITE = "§f";
	
	final private static int WORLD_HEIGHT = 256;
	
	private Ha3Mod mod;
	
	public Ha3Utility(Ha3Mod modIn)
	{
		mod = modIn;
		
	}
	
	public Object getCurrentScreen()
	{
		return mod.manager().getMinecraft().currentScreen;
		
	}
	
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
	
	public void closeCurrentScreen()
	{
		mod.manager().getMinecraft().displayGuiScreen(null);
		
	}
	
	public int getClientTick()
	{
		try
		{
			return (Integer) mod.manager().getPrivateValue(
					net.minecraft.client.Minecraft.class,
					mod.manager().getMinecraft(), 26); // private int ticksRan;
		}
		catch (Ha3ModPrivateAccessException e)
		{
			e.printStackTrace();
			return -1;
			
		}
		
	}
	
	public int getWorldHeight()
	{
		return WORLD_HEIGHT;
		
	}
	
	public void printChat(Object... args)
	{
		if (mod.manager().getMinecraft().thePlayer == null)
			return;
		
		StringBuilder builder = new StringBuilder();
		for (Object o : args)
		{
			builder.append(o);
		}
		mod.manager().getMinecraft().thePlayer.addChatMessage(builder
				.toString());
		
	}
	
}
