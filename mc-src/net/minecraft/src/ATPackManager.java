package net.minecraft.src;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import eu.ha3.util.property.simple.PropertyMissingException;

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
	
	private boolean isCached;
	private boolean isActivated;
	
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
		this.mod.log("Caching all packs from disk...");
		
		this.packs.clear();
		this.packOrder.clear();
		
		for (File file : this.atDirectory.listFiles())
		{
			if (file.isDirectory() && !file.getName().contains(","))
			{
				this.packs.put(file.getName(), new ATPack(file));
				this.packOrder.add(file.getName());
				
			}
		}
		
		String[] orderFromConfig = fetchOrderFromConfig();
		List<String> existingPacksFromOrderInConfig = new ArrayList<String>();
		
		for (String sysName : orderFromConfig)
		{
			if (this.packOrder.contains(sysName))
			{
				this.packOrder.remove(this.packOrder.indexOf(sysName));
				
				// A - Existing sound packs have priority
				//   Add existing packs with element 0 last in
				// existingPacksFromOrderInConfig.add(0, sysName);
				
				// B - New packs have priority
				existingPacksFromOrderInConfig.add(sysName);
			}
		}
		
		for (String sysName : existingPacksFromOrderInConfig)
		{
			// A - Existing sound packs have priority
			// Add existing packs at the beginning of the list
			//this.packOrder.add(0, sysName);
			
			// B - New packs have priority
			this.packOrder.add(sysName);
		}
		
		recalculateOrderAndSave();
		
		for (ATPack pack : this.packs.values())
		{
			pack.fetchInfo();
			try
			{
				pack.setActive(this.mod.getConfig().getBoolean("packs.active..." + pack.getSysName()));
			}
			catch (PropertyMissingException e)
			{
				pack.setActive(true);
			}
		}
		
		// Don't remember why I called it twice...
		//recalculateOrderAndSave();
		
		this.isCached = true;
		
	}
	
	public void applyAllPacks(boolean silent)
	{
		if (!this.isCached)
			return;
		
		// Is Ha3SoundCommunicator ready?
		if (!this.mod.canFunction())
			return;
		
		this.mod.log("Applying all packs status...");
		
		List<File> filesAsList = new ArrayList<File>();
		
		for (String packName : this.packOrder)
		{
			ATPack pack = this.packs.get(packName);
			if (pack.isActive())
			{
				// Add it at the beginning of the list to reverse the order for priority
				filesAsList.add(0, pack.getDirectory());
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
		/*StringBuilder order = new StringBuilder();
		for (File file : files)
		{
			order.append(this.packs.get(file.getName()).getPrettyName()).append(", ");
		}
		this.mod.printChat(
			Ha3Utility.COLOR_BRIGHTGREEN, "Activating in this order: ", Ha3Utility.COLOR_WHITE,
			order.substring(0, order.length() - 2));*/
		
		this.mod.printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Activating...");
		
		feedAndActivate(files);
	}
	
	private void feedAndActivate(File[] files)
	{
		this.atSystem.applySubstituantLocations(files, true);
		this.isActivated = true;
	}
	
	public void cacheAndActivate(boolean silent)
	{
		cacheAllPacks();
		applyAllPacks(silent);
	}
	
	public void deactivate(boolean silent)
	{
		this.mod.log("Deactivating...");
		if (!silent)
		{
			this.mod.printChat(Ha3Utility.COLOR_YELLOW + "Deactivating...");
		}
		this.atSystem.clearSubstitutions();
		this.isActivated = false;
	}
	
	public void swapDuetAt(int id)
	{
		if (id < 0 || id + 1 >= getPackCount())
			return;
		
		String a = this.packOrder.get(id);
		String b = this.packOrder.get(id + 1);
		this.packOrder.set(id, b);
		this.packOrder.set(id + 1, a);
		
		recalculateOrderAndSave();
		
		applyAllPacks(true);
	}
	
	private String[] fetchOrderFromConfig()
	{
		String[] split = this.mod.getConfig().getString("packs.order").split(",");
		return split;
	}
	
	private void recalculateOrderAndSave()
	{
		StringBuilder orderBuilder = new StringBuilder();
		int i = 0;
		for (String sysName : this.packOrder)
		{
			orderBuilder.append(sysName);
			if (i != this.packs.size() - 1)
			{
				orderBuilder.append(",");
			}
			i++;
		}
		
		this.mod.log("Recomputed order as " + orderBuilder.toString());
		
		if (!this.mod.getConfig().getString("packs.order").equals(orderBuilder.toString()))
		{
			this.mod.getConfig().setProperty("packs.order", orderBuilder.toString());
			this.mod.saveConfig();
			
		}
		
	}
	
	public void changePackStateAndSave(String sysName, boolean active)
	{
		ATPack pack = this.packs.get(sysName);
		if (pack.isActive() == active)
			return;
		
		this.mod.log("Changing pack state of " + pack.getSysName());
		pack.setActive(active);
		
		this.mod.getConfig().setProperty("packs.active..." + pack.getSysName(), active);
		this.mod.saveConfig();
		
		applyAllPacks(true);
	}
	
	public boolean isCached()
	{
		return this.isCached;
	}
	
	public boolean isActivated()
	{
		return this.isActivated;
	}
	
	public int getPackCount()
	{
		return this.packs.size();
	}
	
	public ATPack getPack(int id)
	{
		return this.packs.get(this.packOrder.get(id));
	}
	
	public ATSystem getSystem()
	{
		return this.atSystem;
	}
	
}
