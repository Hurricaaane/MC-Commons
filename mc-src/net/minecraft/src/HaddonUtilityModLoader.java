package net.minecraft.src;

import java.io.File;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.haddon.Manager;

/* x-placeholder-wtfplv2 */

public class HaddonUtilityModLoader extends HaddonUtilityImpl
{
	protected long ticksRan;
	protected File modsFolder;
	
	public HaddonUtilityModLoader(Manager manager)
	{
		super(manager);
	}
	
	@Override
	public long getClientTick()
	{
		return ((HaddonBridgeModLoader) this.manager).bridgeTicksRan();
	}
	
	@Override
	public File getModsFolder()
	{
		if (this.modsFolder != null)
			return this.modsFolder;
		
		/*if (classExists("cpw.mods.fml.client.FMLClientHandler", this))
		{
			// Use FML interpretation of mods/
			this.modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
			return this.modsFolder;
		}
		
		// Use ModLoader interpretation of mods/
		
		File versionsDir = new File(Minecraft.getMinecraft().mcDataDir, "versions");
		File version = new File(versionsDir, Minecraft.getVersion(Minecraft.getMinecraft()));
		
		if (versionsDir.exists() && versionsDir.isDirectory() && version.exists() && version.isDirectory())
		{
			this.modsFolder = new File(version, "/mods/");
		}
		else
		{
			this.modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
		}
		return this.modsFolder;*/
		
		// Always use FML interpretation of mods/ folder
		this.modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
		return this.modsFolder;
	}
	
	/**
	 * Checks if a certain class name exists in a certain object context's class
	 * loader.
	 * 
	 * @param className
	 * @param context
	 * @return
	 */
	public static boolean classExists(String className, Object context)
	{
		boolean canWork = false;
		try
		{
			canWork = Class.forName(className, false, context.getClass().getClassLoader()) != null;
		}
		//catch (ClassNotFoundException e)
		//{
		//}
		catch (Exception e)
		{
			// Normally throws checked ClassNotFoundException
			// This also throws unckecked security exceptions
		}
		
		return canWork;
		
	}
	
}
