package net.minecraft.src;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3Options;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
 */

public class MAtOptions extends Ha3Options
{
	private MAtMod mod;
	
	private File propertiesFile;
	
	public MAtOptions(MAtMod mAtmosHaddon)
	{
		super();
		
		this.mod = mAtmosHaddon;
		
		propertiesFile = new File(Minecraft.getMinecraftDir(),
				"matmos_options.cfg");
		
	}
	
	public void saveOptions()
	{
		Properties prop = outputOptions();
		try
		{
			prop.store(new FileWriter(propertiesFile), "#matmos config "
					+ mod.VERSION);
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
			if (propertiesFile.exists())
			{
				Properties prop = new Properties();
				prop.load(new FileReader(propertiesFile));
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
