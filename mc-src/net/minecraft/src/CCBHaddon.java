package net.minecraft.src;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3StaticUtilities;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.util.property.simple.ConfigProperty;

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

public class CCBHaddon extends HaddonImpl implements SupportsFrameEvents
{
	public static final int VERSION = 0;
	
	private CCBReader system;
	private CCBUpdate update;
	
	private ConfigProperty blockSound;
	private Map<String, String> blockMap;
	
	@Override
	public void onLoad()
	{
		fixInstallation();
		loadSounds();
		
		if (isInstalledMLP())
		{
			this.system = new CCBMLPReader(this);
		}
		else
		{
			this.system = new CCBGeneralReader(this);
		}
		
		File configFile = new File(Minecraft.getMinecraftDir(), "ccb.cfg");
		if (configFile.exists())
		{
			log("Config file found. Loading...");
			try
			{
				ConfigProperty config = new ConfigProperty();
				config.setSource(configFile.getCanonicalPath());
				config.load();
				
				CCBVariator var = new CCBVariator();
				var.loadConfig(config);
				
				this.system.setVariator(var);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			log("Loaded.");
			
		}
		
		this.blockSound = new ConfigProperty();
		this.blockSound.setProperty("0", "ccb_sounds.hoofstep");
		this.blockSound.setProperty("2", "ccb_sounds.softstep");
		this.blockSound.setProperty("110", "ccb_sounds.softstep");
		this.blockSound.setProperty("35", "ccb_sounds.softstep");
		this.blockSound.setProperty("19", "ccb_sounds.softstep");
		this.blockSound.setProperty("18", "ccb_sounds.softstep");
		this.blockSound.setProperty("78", "ccb_sounds.softstep");
		this.blockSound.setProperty("80", "ccb_sounds.softstep");
		this.blockSound.setProperty("111", "ccb_sounds.softstep");
		this.blockSound.setProperty("81", "ccb_sounds.softstep");
		this.blockSound.setProperty("60", "ccb_sounds.softstep");
		this.blockSound.commit();
		
		// Load configuration from source
		try
		{
			this.blockSound.setSource(new File(Minecraft.getMinecraftDir(), "ccb_blockmap.cfg").getCanonicalPath());
			this.blockSound.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error caused config not to work: " + e.getMessage());
		}
		
		this.blockMap = new LinkedHashMap<String, String>();
		createBlockMap();
		
		manager().hookFrameEvents(true);
		
		this.update = new CCBUpdate(this);
		this.update.attempt();
	}
	
	private void createBlockMap()
	{
		Map<String, String> properties = this.blockSound.getAllProperties();
		for (Entry<String, String> entry : properties.entrySet())
		{
			try
			{
				// blockID = Integer.parseInt(entry.getKey());
				this.blockMap.put(entry.getKey(), entry.getValue());
				
			}
			catch (Exception e)
			{
				log("Error when registering block " + entry.getKey() + ": " + e.getMessage());
			}
			
		}
		
	}
	
	private void fixInstallation()
	{
		File folder = new File(Minecraft.getMinecraftDir(), "resources/sound3/ccb_sounds");
		if (!folder.exists())
		{
			log("Did not find folder resources/sound3/ccb_sounds/. Attempting first installation");
			folder.mkdirs();
		}
		
		String[] names = { "dash1.wav", "hoofstep1.wav", "softstep1.wav", "land1.wav", "wing1.wav" };
		
		for (String name : names)
		{
			InputStream stream = null;
			try
			{
				File file = new File(folder, name);
				
				if (!file.exists())
				{
					URL toInstall =
						net.minecraft.client.Minecraft.class.getResource("/resources/sound/ccb_sounds/" + name);
					stream = toInstall.openStream();
					if (stream != null)
					{
						isToFile(stream, file);
					}
					
					/*File inJarFile = new File(toInstall.getFile());
					
					if (inJarFile.exists())
					{
						log("Did not find file " + name + ". Installing...");
						copyFile(new File(toInstall.getFile()), file);
					}*/
					
				}
			}
			catch (Exception e)
			{
				CCBHaddon.log("Could not fix " + name + ": " + e.getMessage());
			}
			finally
			{
				try
				{
					if (stream != null)
					{
						stream.close();
					}
				}
				catch (Exception e)
				{
				}
			}
			
		}
	}
	
	// from
	// http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
	private static void isToFile(InputStream sourceStream, File destFile) throws IOException
	{
		if (!destFile.exists())
		{
			destFile.createNewFile();
		}
		
		FileOutputStream fos = null;
		
		try
		{
			fos = new FileOutputStream(destFile);
			
			byte buffer[] = new byte[1024];
			int length;
			while ((length = sourceStream.read(buffer)) > 0)
			{
				fos.write(buffer, 0, length);
			}
		}
		finally
		{
			if (sourceStream != null)
			{
				sourceStream.close();
			}
			if (fos != null)
			{
				fos.close();
			}
		}
	}
	
	// from
	// http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
	/*private static void copyFile(File sourceFile, File destFile) throws IOException
	{
		if (!destFile.exists())
		{
			destFile.createNewFile();
		}
		
		FileChannel source = null;
		FileChannel destination = null;
		
		try
		{
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally
		{
			if (source != null)
			{
				source.close();
			}
			if (destination != null)
			{
				destination.close();
			}
		}
	}*/
	
	private boolean isInstalledMLP()
	{
		return Ha3StaticUtilities.classExists("Pony", this)
			|| Ha3StaticUtilities.classExists("net.minecraft.src.Pony", this);
	}
	
	private void loadSounds()
	{
		File dir = new File(Minecraft.getMinecraftDir(), "resources/sound3/ccb_sounds/");
		if (dir.exists())
		{
			loadResource(dir, "sound3/ccb_sounds/");
		}
	}
	
	@Override
	public void onFrame(float semi)
	{
		EntityPlayer ply = manager().getMinecraft().thePlayer;
		
		if (ply == null)
			return;
		
		this.system.frame(ply);
		
		try
		{
			//nextStepDistance
			util().setPrivateValueLiteral(Entity.class, ply, "c", 36, Integer.MAX_VALUE);
			//util().setPrivateValueLiteral(Entity.class, ply, "c", 36, 0);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Loads a resource and passes it to Minecraft to install.
	 */
	private void loadResource(File par1File, String par2Str)
	{
		File[] filesInThisDir = par1File.listFiles();
		int fileCount = filesInThisDir.length;
		
		for (int i = 0; i < fileCount; ++i)
		{
			File file = filesInThisDir[i];
			
			if (file.isDirectory())
			{
				loadResource(file, par2Str + file.getName() + "/");
			}
			else
			{
				try
				{
					String fileRep = par2Str + file.getName();
					fileRep = fileRep.substring(fileRep.indexOf("/") + 1);
					fileRep = fileRep.substring(0, fileRep.indexOf("."));
					while (Character.isDigit(fileRep.charAt(fileRep.length() - 1)))
					{
						fileRep = fileRep.substring(0, fileRep.length() - 1);
					}
					fileRep = fileRep.replaceAll("/", ".");
					
					getManager().getMinecraft().installResource(par2Str + file.getName(), file);
				}
				catch (Exception var9)
				{
					log("Failed to add " + par2Str + file.getName());
				}
			}
		}
	}
	
	public ConfigProperty getConfig()
	{
		return new ConfigProperty();
	}
	
	public void printChat(Object... args)
	{
	}
	
	public static void log(String contents)
	{
		System.out.println("(CCB) " + contents);
	}
	
	public void saveConfig()
	{
	}
	
	public String getSoundForMaterial(int block, CCBEventType event)
	{
		if (event == CCBEventType.STEP)
			return this.blockMap.containsKey(Integer.toString(block))
				? this.blockMap.get(Integer.toString(block)) : this.blockMap.get("0");
		else if (event == CCBEventType.JUMP)
			return this.blockMap.containsKey(block + "_jump")
				? this.blockMap.get(block + "_jump") : getSoundForMaterial(block, CCBEventType.STEP);
		else
			//if (event == CCBEventType.LAND)
			return this.blockMap.containsKey(block + "_land")
				? this.blockMap.get(block + "_land") : getSoundForMaterial(block, CCBEventType.STEP);
		
	}
	
}
