package net.minecraft.src;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import paulscode.sound.SoundSystem;
import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.convenience.Ha3Signal;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.SupportsEverythingReady;
import eu.ha3.mc.haddon.SupportsKeyEvents;
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

public class ATHaddon extends HaddonImpl implements SupportsTickEvents, SupportsEverythingReady, SupportsKeyEvents
{
	// Remember to change the thing in mod_Audiotori
	public static final int VERSION = 2;
	
	private static final int REANALYSE_INITIAL_DELAY = 20 * 10;
	private static final int REANALYSE_DURING_DELAY = 20 * 30;
	private static final int RELOAD_DELAY = 20 * 5;
	
	private Ha3SoundCommunicator sndComms;
	private ATPackManager atPackManager;
	
	private EdgeTrigger key;
	private ConfigProperty config;
	
	private boolean canFunction;
	private boolean hasActivated;
	private boolean debugging;
	
	private ATUpdateNotifier updateNotifier;
	
	private boolean firstTickPassed;
	private int ticksUntilReanalyse;
	private boolean reanalyseImpliesReload;
	private int lastGenuineCount;
	
	private int[] splitKeys;
	
	@Override
	public void onLoad()
	{
		if (!new File(Minecraft.getMinecraftDir(), "audiotori/").exists())
		{
			new File(Minecraft.getMinecraftDir(), "audiotori/").mkdirs();
		}
		
		this.atPackManager = new ATPackManager(this);
		this.updateNotifier = new ATUpdateNotifier(this);
		
		// Create default configuration
		this.config = new ConfigProperty();
		this.config.setProperty("start.enabled", true);
		this.config.setProperty("debug.enabled", false);
		this.config.setProperty("debug.level", 1);
		this.config.setProperty("afterloadingscreen.enabled", false);
		this.config.setProperty("gui.hints.enabled", true);
		this.config.setProperty("packs.order", "");
		this.config.setProperty("keybinding.enable", true);
		this.config.setProperty("key.combo", "29,42,23"); // Remember to change it in the excaption handling
		this.config.setProperty("update_found.enabled", true);
		this.config.setProperty("update_found.version", ATHaddon.VERSION);
		this.config.setProperty("update_found.display.remaining.value", 0);
		this.config.setProperty("update_found.display.count.value", 3);
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
		
		this.updateNotifier.loadConfig(this.config);
		
		try
		{
			String[] splitString = this.config.getString("key.combo").split(",");
			this.splitKeys = new int[splitString.length];
			
			for (int i = 0; i < this.splitKeys.length; i++)
			{
				this.splitKeys[i] = Integer.parseInt(splitString[i]);
			}
		}
		catch (Exception e)
		{
			log("Error while parsing key combo");
			this.splitKeys = new int[] { 29, 42, 23 }; // Remember to change it in the config property
		}
		
		if (this.config.getBoolean("debug.enabled"))
		{
			this.debugging = true;
			this.atPackManager.getSystem().setDebugging(true);
			this.atPackManager.getSystem().setDebuggingLevel(this.config.getInteger("debug.level"));
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
				openGUI();
			}
			
			@Override
			public void onFalseEdge()
			{
			}
		});
		
		if (this.config.getBoolean("keybinding.enable"))
		{
			manager().addKeyBinding(new KeyBinding("key.audiotori", 67), "Audiotori");
		}
		manager().hookTickEvents(true);
		
	}
	
	public void openGUI()
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
			/*Set<Thread> threads = Thread.getAllStackTraces().keySet();
			
			boolean hasTDR = false;
			Iterator<Thread> iter = threads.iterator();
			while (!hasTDR && iter.hasNext())
			{
				if (iter.next() instanceof ThreadDownloadResources)
				{
					hasTDR = true;
				}
			}
			
			if (!hasTDR)
			{*/
			if (this.config.getBoolean("start.enabled"))
			{
				this.atPackManager.cacheAndActivate(true);
			}
			
			this.hasActivated = true;
			/*}
			else
			{
				System.out.println("TDR found, waiting...");
			}*/
			
			this.lastGenuineCount = countGenuineFromAllPools();
			log("Counted " + this.lastGenuineCount + " genuine (original deck) sounds.");
			
			this.ticksUntilReanalyse = ATHaddon.REANALYSE_INITIAL_DELAY;
		}
		else if (this.hasActivated && this.canFunction && this.atPackManager.isActivated())
		{
			if (this.ticksUntilReanalyse < 0)
			{
				int count = countGenuineFromAllPools();
				if (count != this.lastGenuineCount)
				{
					log("Found a different genuine count (was "
						+ this.lastGenuineCount + ", now is " + count + "). Preparing a reload...");
					
					this.lastGenuineCount = count;
					this.reanalyseImpliesReload = true;
					
					this.ticksUntilReanalyse = ATHaddon.RELOAD_DELAY;
				}
				else
				{
					if (this.reanalyseImpliesReload)
					{
						log("Reapplying all packs...");
						this.reanalyseImpliesReload = false;
						this.atPackManager.applyAllPacks(true);
					}
					
					this.ticksUntilReanalyse = ATHaddon.REANALYSE_DURING_DELAY;
				}
			}
			else
			{
				this.ticksUntilReanalyse = this.ticksUntilReanalyse - 1;
			}
			
		}
		
		// CTRL-SHIFT-T
		this.key.signalState(util().areKeysDown(this.splitKeys));
		
		if (!this.firstTickPassed)
		{
			this.firstTickPassed = true;
			this.updateNotifier.attempt();
			
		}
	}
	
	private int countGenuineFromAllPools()
	{
		try
		{
			// Copy from ATSystem
			SoundPool soundPoolSounds =
				(SoundPool) util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, manager().getMinecraft().sndManager, "b", 1);
			SoundPool soundPoolStreaming =
				(SoundPool) util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, manager().getMinecraft().sndManager, "c", 2);
			SoundPool soundPoolMusic =
				(SoundPool) util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, manager().getMinecraft().sndManager, "d", 3);
			
			return countGenuineFromAllPool(soundPoolSounds)
				+ countGenuineFromAllPool(soundPoolStreaming) + countGenuineFromAllPool(soundPoolMusic);
		}
		catch (PrivateAccessException e)
		{
			// XXX ???
			e.printStackTrace();
			return 0;
		}
		
	}
	
	private int countGenuineFromAllPool(SoundPool soundPool) throws PrivateAccessException
	{
		// Copy from ATSystem
		@SuppressWarnings("rawtypes")
		List allSoundPoolEntries =
			(List) util().getPrivateValueLiteral(net.minecraft.src.SoundPool.class, soundPool, "e", 2);
		
		int installations = 0;
		for (Object o : allSoundPoolEntries)
		{
			if (o instanceof ATSoundOrphan)
			{
				installations++;
			}
		}
		
		return allSoundPoolEntries.size() - installations;
		
	}
	
	public void playMusic()
	{
		SoundManager sndManager = manager().getMinecraft().sndManager;
		
		stopMusic();
		
		try
		{
			// XXX check me
			// ticksBeforeMusic
			util().setPrivateValueLiteral(SoundManager.class, sndManager, "j", 9, 0);
			sndManager.playRandomMusicIfReady();
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	public void stopMusic()
	{
		SoundSystem system = this.sndComms.getSoundSystem();
		if (system.playing("BgMusic"))
		{
			system.stop("BgMusic");
		}
	}
	
	public boolean isMusicPlaying()
	{
		SoundSystem system = this.sndComms.getSoundSystem();
		return system.playing("BgMusic");
	}
	
	public void log(String contents)
	{
		System.out.println("(Audiotori) " + contents);
		
	}
	
	public void debug(String contents)
	{
		if (this.debugging)
		{
			System.out.println("(Audiotori-debug) " + contents);
		}
		
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
	
	public boolean canFunction()
	{
		return this.canFunction;
	}
	
	@Override
	public void onEverythingReady()
	{
		if (this.config.getBoolean("afterloadingscreen.enabled")
			&& this.config.getBoolean("start.enabled") && this.canFunction)
		{
			this.atPackManager.cacheAndActivate(true);
		}
		
	}
	
	@Override
	public void onKey(KeyBinding event)
	{
		if (!(util().getCurrentScreen() instanceof ATGuiMenu))
		{
			openGUI();
		}
	}
	
}
