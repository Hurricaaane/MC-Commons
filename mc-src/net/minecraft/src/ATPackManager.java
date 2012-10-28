package net.minecraft.src;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;

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

public class ATPackManager
{
	private ATSystem atSystem;
	private ATHaddon mod;
	
	private Map<String, ATPack> packs;
	private List<String> packOrder;
	
	private File atDirectory;
	
	public ATPackManager(ATHaddon mod)
	{
		this.mod = mod;
		this.atSystem = new ATSystem(mod);
		
		this.packs = new LinkedHashMap<String, ATPack>();
		this.packOrder = new ArrayList<String>();
		
		this.atDirectory = new File(Minecraft.getMinecraftDir(), "audiotori/");
	}
	
	public void cacheAllPacks()
	{
		this.packs.clear();
		this.packOrder.clear();
		
		for (File file : this.atDirectory.listFiles())
		{
			if (file.isDirectory())
			{
				this.packs.put(file.getName(), new ATPack(file));
				this.packOrder.add(file.getName());
				
			}
		}
		
		for (ATPack pack : this.packs.values())
		{
			pack.fetchInfo();
		}
		
	}
	
	public void applyAllPacks(boolean silent)
	{
		List<File> filesAsList = new ArrayList<File>();
		
		for (String packName : this.packOrder)
		{
			ATPack pack = this.packs.get(packName);
			if (pack.isActive())
			{
				filesAsList.add(pack.getDirectory());
			}
		}
		
		File[] files = filesAsList.toArray(new File[0]);
		
		if (silent)
		{
			feedAndActivate(files);
		}
		else
		{
			feedAndActivateAndSay(files);
		}
		
	}
	
	private void feedAndActivateAndSay(File[] files)
	{
		StringBuilder order = new StringBuilder();
		for (File file : files)
		{
			order.append(file.getName()).append(", ");
		}
		this.mod.printChat(
			Ha3Utility.COLOR_BRIGHTGREEN, "Activating in this order: ", Ha3Utility.COLOR_WHITE,
			order.substring(0, order.length() - 2));
		
		feedAndActivate(files);
	}
	
	private void feedAndActivate(File[] files)
	{
		this.atSystem.applySubstituantLocations(files, true);
	}
	
	public void activate(boolean silent)
	{
		cacheAllPacks();
		applyAllPacks(silent);
	}
	
	public void deactivate(boolean silent)
	{
		if (!silent)
		{
			this.mod.printChat(Ha3Utility.COLOR_YELLOW + "Deactivating...");
		}
		this.atSystem.clearSubstitutions();
	}
	
	public int getPackCount()
	{
		return this.packs.size();
	}
	
	public ATPack getPack(int id)
	{
		return this.packs.get(this.packOrder.get(id));
	}
	
}
