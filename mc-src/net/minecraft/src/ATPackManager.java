package net.minecraft.src;

import java.io.File;

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
	
	public ATPackManager(ATHaddon mod)
	{
		this.mod = mod;
		this.atSystem = new ATSystem(mod);
	}
	
	public void feedAndActivateAndSay(File[] files)
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
	
	public void feedAndActivate(File[] files)
	{
		this.atSystem.applySubstituantLocations(files, true);
	}
	
	public void deactivate()
	{
		this.mod.printChat(Ha3Utility.COLOR_YELLOW + "Deactivating...");
		this.atSystem.clearSubstitutions();
	}
	
}
