package net.minecraft.src;

import java.io.File;

import eu.ha3.mc.haddon.Manager;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

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
		
		File versionsDir = new File(Minecraft.getMinecraft().mcDataDir, "versions");
		File version = new File(versionsDir, Minecraft.func_110431_a(Minecraft.getMinecraft()));
		
		if (versionsDir.exists() && versionsDir.isDirectory() && version.exists() && version.isDirectory())
		{
			this.modsFolder = new File(version, "/mods/");
		}
		else
		{
			this.modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
		}
		return this.modsFolder;
	}
	
}
