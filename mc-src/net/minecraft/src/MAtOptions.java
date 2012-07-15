package net.minecraft.src;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3Options;

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

public class MAtOptions extends Ha3Options
{
	private MAtMod mod;
	
	private File propertiesFile;
	
	public MAtOptions(MAtMod mAtmosHaddon)
	{
		super();
		
		this.mod = mAtmosHaddon;
		
		this.propertiesFile = new File(Minecraft.getMinecraftDir(), "matmos_options.cfg");
		
	}
	
	public void saveOptions()
	{
		Properties prop = outputOptions();
		try
		{
			prop.store(new FileWriter(this.propertiesFile), "#matmos config " + this.mod.VERSION);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void loadOptions()
	{
		try
		{
			if (this.propertiesFile.exists())
			{
				Properties prop = new Properties();
				prop.load(new FileReader(this.propertiesFile));
				inputOptions(prop);
				
			}
			else
			{
				// If the file does not exist, write it.
				saveOptions();
				
			}
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
		}
		
	}
	
}
