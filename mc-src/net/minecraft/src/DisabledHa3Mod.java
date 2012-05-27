package net.minecraft.src;

import eu.ha3.mc.mod.Ha3Mod;
import eu.ha3.mc.mod.Ha3ModCore;
import eu.ha3.mc.mod.Ha3Moddable;

public class DisabledHa3Mod extends Ha3Moddable implements Ha3Mod
{
	DisabledHa3Mod()
	{
		System.out.println("WARNING: Created new Disabled Ha3 Mod.");
		setCore(new Ha3ModCore() {
			@Override
			public void setMod(Ha3Mod modIn)
			{
			}
			
			@Override
			public void load()
			{
			}
		});
	}
	
}
