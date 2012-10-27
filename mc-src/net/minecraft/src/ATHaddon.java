package net.minecraft.src;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.convenience.Ha3Signal;
import eu.ha3.mc.haddon.SupportsTickEvents;
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

public class ATHaddon extends HaddonImpl implements SupportsTickEvents
{
	private Ha3SoundCommunicator sndComms;
	private ATPackManager atPackManager;
	
	private EdgeTrigger key;
	private ConfigProperty config;
	
	private boolean canFunction;
	private boolean hasActivated;
	
	@Override
	public void onLoad()
	{
		if (!new File(Minecraft.getMinecraftDir(), "audiotori/").exists())
		{
			try
			{
				new File(Minecraft.getMinecraftDir(), "audiotori/").createNewFile();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.atPackManager = new ATPackManager(this);
		
		// Create default configuration
		this.config = new ConfigProperty();
		this.config.setProperty("start.enabled", true);
		this.config.commit();
		
		// Load configuration from source
		try
		{
			this.config.setSource(new File(Minecraft.getMinecraftDir(), "audiotori/userconfig.cfg").getCanonicalPath());
			this.config.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error caused config not to work: " + e.getMessage());
		}
		
		this.sndComms = new Ha3SoundCommunicator(this, "AT_");
		this.sndComms.load(new Ha3Signal() {
			@Override
			public void signal()
			{
				continueLoading();
			}
		}, new Ha3Signal() {
			@Override
			public void signal()
			{
				log("Unable to initialize the Sound Communicator.");
			}
		});
		
		this.key = new EdgeTrigger(new EdgeModel() {
			@Override
			public void onTrueEdge()
			{
				ATHaddon.this.atPackManager.applyAllPacks();
				openGUI();
			}
			
			@Override
			public void onFalseEdge()
			{
			}
		});
		
		manager().hookTickEvents(true);
		
	}
	
	protected void openGUI()
	{
		manager().getMinecraft().displayGuiScreen(new ATGuiMenu((GuiScreen) util().getCurrentScreen(), this));
	}
	
	private void continueLoading()
	{
		this.canFunction = true;
	}
	
	@Override
	public void onTick()
	{
		if (!this.hasActivated && this.config.getBoolean("start.enabled") && this.canFunction)
		{
			File[] files =
				{
					new File(Minecraft.getMinecraftDir(), "audiotori/substitute/"),
					new File(Minecraft.getMinecraftDir(), "audiotori/pony/") };
			this.atPackManager.feedAndActivate(files);
			this.hasActivated = true;
		}
		
		// CTRL-SHIFT-T
		this.key.signalState(util().areKeysDown(29, 42, 20));
		
	}
	
	public void log(String contents)
	{
		System.out.println("(Audiotori) " + contents);
		
	}
	
	public void debug(String contents)
	{
		System.out.println("(Audiotori-debug) " + contents);
		
	}
	
	public ATPackManager getPackManager()
	{
		return this.atPackManager;
	}
	
	public ConfigProperty getConfig()
	{
		return this.config;
	}
	
	public void saveConfig()
	{
		// If there were changes...
		if (this.config.commit())
		{
			log("Saving configuration...");
			
			// Write changes on disk.
			this.config.save();
		}
	}
	
	public void printChat(Object... args)
	{
		final Object[] in = new Object[] { Ha3Utility.COLOR_WHITE, "Audiotori: " };
		
		Object[] dest = new Object[in.length + args.length];
		System.arraycopy(in, 0, dest, 0, in.length);
		System.arraycopy(args, 0, dest, in.length, args.length);
		
		util().printChat(dest);
		
	}
	
	public void printChatShort(Object... args)
	{
		final Object[] in = new Object[] { Ha3Utility.COLOR_WHITE, "" };
		
		Object[] dest = new Object[in.length + args.length];
		System.arraycopy(in, 0, dest, 0, in.length);
		System.arraycopy(args, 0, dest, in.length, args.length);
		
		util().printChat(dest);
		
	}
	
}
