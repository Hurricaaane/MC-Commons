package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine.MAtmosLogger;
import eu.ha3.mc.convenience.Ha3Signal;
import eu.ha3.mc.convenience.Ha3StaticUtilities;
import eu.ha3.mc.haddon.SupportsFrameEvents;
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

public class MAtMod extends HaddonImpl implements SupportsFrameEvents, SupportsTickEvents, SupportsKeyEvents/*, SupportsGuiTickEvents, Ha3Personalizable*/
{
	final static public Logger LOGGER = Logger.getLogger("MAtmos");
	final static public int VERSION = 17; // Remember to change the thing on mod_Matmos_forModLoader
	
	private ConsoleHandler conMod;
	private ConsoleHandler conEngine;
	
	private Ha3SoundCommunicator sndComm;
	
	private boolean shouldSkipResourceReloader = true;
	private boolean shouldDumpData = true;
	
	private MAtUserControl userControl;
	private MAtDataGatherer dataGatherer;
	private MAtExpansionManager expansionManager;
	private MAtUpdateNotifier updateNotifier;
	
	private MAtSoundManagerMaster soundManagerMaster;
	
	private boolean isRunning;
	private TimeStatistic timeStatistic;
	
	private boolean isReady;
	private boolean fatalError;
	
	private boolean userKnowsFatalError;
	private boolean firstTickPassed;
	
	private MAtModPhase phase;
	
	private ConfigProperty configuration;
	
	public MAtMod()
	{
		// This is the constructor, so don't do anything
		// related to Minecraft.
		
		// Haddon constructors don't have superclass constructor calls
		// for convenience, so nothing is initialized.
		
		this.phase = MAtModPhase.NOT_INITIALIZED;
		
		Formatter formatter = new Formatter() {
			@Override
			public String format(LogRecord record)
			{
				return "(" + record.getLoggerName() + " : " + record.getLevel() + ") " + record.getMessage() + "\n";
			}
		};
		
		this.conMod = new ConsoleHandler();
		this.conMod.setFormatter(formatter);
		
		this.conEngine = new ConsoleHandler();
		this.conEngine.setFormatter(formatter);
		
		Level levelMod = Level.INFO;
		MAtMod.LOGGER.addHandler(this.conMod);
		MAtMod.LOGGER.setUseParentHandlers(false);
		MAtMod.LOGGER.setLevel(levelMod);
		this.conMod.setLevel(levelMod);
		
		Level levelEngine = Level.INFO;
		MAtmosLogger.LOGGER.addHandler(this.conEngine);
		MAtmosLogger.LOGGER.setUseParentHandlers(false);
		MAtmosLogger.LOGGER.setLevel(levelEngine);
		this.conEngine.setLevel(levelEngine);
		
	}
	
	/**
	 * Sets the logger level for MAtmos mod.
	 * 
	 * @param lvl
	 */
	public void setModLogger(Level lvl)
	{
		MAtMod.LOGGER.setLevel(lvl);
		this.conMod.setLevel(lvl);
		
	}
	
	/**
	 * Sets the logger level for MAtmos Engine (minecraft independent)
	 * 
	 * @param lvl
	 */
	public void setEngineLogger(Level lvl)
	{
		MAtmosLogger.LOGGER.setLevel(lvl);
		this.conEngine.setLevel(lvl);
		
	}
	
	@Override
	public void onLoad()
	{
		if (!new File(Minecraft.getMinecraftDir(), "matmos/").exists())
		{
			this.fatalError = true;
			manager().hookTickEvents(true);
			return;
			
		}
		
		this.timeStatistic = new TimeStatistic(Locale.ENGLISH);
		
		this.sndComm = new Ha3SoundCommunicator(this, "MAtmos_");
		
		this.userControl = new MAtUserControl(this);
		this.dataGatherer = new MAtDataGatherer(this);
		this.expansionManager = new MAtExpansionManager(this);
		this.updateNotifier = new MAtUpdateNotifier(this);
		
		this.soundManagerMaster = new MAtSoundManagerMaster(this);
		
		manager().hookFrameEvents(true);
		manager().hookTickEvents(true);
		
		this.configuration = new ConfigProperty();
		this.configuration.setProperty("dump.enabled", true);
		this.configuration.setProperty("start.enabled", true);
		this.configuration.setProperty("globalvolume.scale", 1f);
		this.configuration.setProperty("update_found.enabled", true);
		this.configuration.setProperty("update_found.version", MAtMod.VERSION);
		this.configuration.setProperty("update_found.display.remaining.value", 0);
		this.configuration.setProperty("update_found.display.count.value", 3);
		this.configuration.commit();
		try
		{
			this.configuration.setSource(new File(Minecraft.getMinecraftDir(), "matmos/userconfig.cfg")
				.getCanonicalPath());
			this.configuration.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error caused config not to work: " + e.getMessage());
		}
		
		this.shouldDumpData = this.configuration.getBoolean("dump.enabled");
		this.soundManagerMaster.setVolume(this.configuration.getFloat("globalvolume.scale"));
		this.updateNotifier.loadConfiguration(this.configuration);
		
		MAtMod.LOGGER.info("Took " + this.timeStatistic.getSecondsAsString(1) + " seconds to load MAtmos.");
		
		preLoad();
		
	}
	
	private void preLoad()
	{
		MAtMod.LOGGER.info("Pre-loading.");
		
		this.userControl.load();
		
		this.phase = MAtModPhase.NOT_YET_ENABLED;
		if (this.configuration.getBoolean("start.enabled"))
		{
			doLoad();
		}
		
	}
	
	public void doLoad()
	{
		if (this.phase != MAtModPhase.NOT_YET_ENABLED)
			return;
		
		this.timeStatistic = new TimeStatistic(Locale.ENGLISH);
		
		this.phase = MAtModPhase.CONSTRUCTING;
		
		MAtMod.LOGGER.info("Constructing.");
		
		this.dataGatherer.load();
		// note: soundManager needs to be loaded post sndcomms
		
		this.sndComm.load(new Ha3Signal() {
			@Override
			public void signal()
			{
				loadResourcesPhase();
				
			}
		}, new Ha3Signal() {
			
			@Override
			public void signal()
			{
				sndCommFailed();
				
			}
		});
		
		this.expansionManager.loadExpansions();
		MAtMod.LOGGER.info("Took " + this.timeStatistic.getSecondsAsString(1) + " seconds to enable MAtmos.");
		
	}
	
	public MAtSoundManagerMaster getSoundManagerMaster()
	{
		return this.soundManagerMaster;
		
	}
	
	public MAtDataGatherer getDataGatherer()
	{
		return this.dataGatherer;
		
	}
	
	// XXX Blatant design.
	public MAtExpansionManager getExpansionLoader()
	{
		return this.expansionManager;
		
	}
	
	private void sndCommFailed()
	{
		this.phase = MAtModPhase.SOUNDCOMMUNICATOR_FAILURE;
		MAtMod.LOGGER.severe("CRITICAL Error with SoundCommunicator (after "
			+ this.timeStatistic.getSecondsAsString(3) + " s.). Will not load.");
		
		this.fatalError = true;
		this.phase = MAtModPhase.SOUNDCOMMUNICATOR_FAILURE;
		
	}
	
	private String getFirstBlocker()
	{
		// Disabled because even if we bypass that it causes an issue where
		// sounds from MAtmos are not loaded in memory al all (resources not installed)
		/*try
		{
			// Try to check if ThreadDownloadResources is altered by a third party
			// Like SoundModEnabler
			HaddonUtilitySingleton.getInstance().getPrivateValueViaName(ThreadDownloadResources.class, null, "logger");
			// If it doesn't throw an exception, it means it is a third party class
			return "SoundModEnabler was likely detected. The unobfuscated field called \"logger\" exists, assume SoundModEnabled is installed.";
			
		}
		catch (PrivateAccessException e2)
		{
			// Else, it's not a third party class, or it may not have been detected as such.
		}*/
		
		File folder = new File(Minecraft.getMinecraftDir(), "matmos/reloader_blacklist/");
		
		if (!folder.exists())
			return null;
		
		for (File file : folder.listFiles())
		{
			if (file.getName().endsWith(".list"))
			{
				BufferedReader reader;
				try
				{
					reader = new BufferedReader(new FileReader(file));
					try
					{
						String line;
						while ((line = reader.readLine()) != null)
						{
							String[] contents = line.split("\t");
							if (contents.length > 0 && contents[0].length() > 0)
							{
								if (Ha3StaticUtilities.classExists(contents[0], this))
								{
									if (contents.length > 1)
										return contents[1];
									else
										return "A blocker was detected.";
									
								}
								
							}
							
						}
						
					}
					catch (IOException e)
					{
						
					}
					finally
					{
						try
						{
							reader.close();
						}
						catch (IOException e)
						{
						}
						
					}
				}
				catch (FileNotFoundException e1)
				{
					e1.printStackTrace();
				}
				
			}
			
		}
		
		return null;
		
	}
	
	private void loadResourcesPhase()
	{
		this.phase = MAtModPhase.RESOURCE_LOADER;
		
		MAtMod.LOGGER.info("SoundCommunicator loaded (after " + this.timeStatistic.getSecondsAsString(3) + " s.).");
		
		String firstBlocker = getFirstBlocker();
		if (firstBlocker != null)
		{
			MAtMod.LOGGER.warning(firstBlocker);
			MAtMod.LOGGER.warning("MAtmos will not attempt load sounds on its own at all.");
			loadFinalPhase();
			
		}
		else if (!this.shouldSkipResourceReloader)
		{
			new MAtResourceReloader(this, new Ha3Signal() {
				
				@Override
				public void signal()
				{
					loadFinalPhase();
					
				}
				
			}).start();
			
		}
		else
		{
			MAtMod.LOGGER.info("Bypassing Resource Reloader threaded wait. This may cause issues.");
			
			try
			{
				new MAtResourceReloader(this, null).reloadResources();
			}
			catch (Exception e)
			{
				MAtMod.LOGGER.severe("A severe error has occured while trying to reload resources.");
				MAtMod.LOGGER.severe("MAtmos may not function properly.");
				e.printStackTrace();
				
				try
				{
					Writer writer = new FileWriter(new File(Minecraft.getMinecraftDir(), "matmos_error.log"), true);
					PrintWriter pw = new PrintWriter(writer);
					e.printStackTrace(pw);
					pw.close();
					
				}
				catch (Exception eee)
				{
				}
			}
			loadFinalPhase();
			
		}
		
	}
	
	private void loadFinalPhase()
	{
		this.phase = MAtModPhase.FINAL_PHASE;
		
		MAtMod.LOGGER.info("ResourceReloader finished (after " + this.timeStatistic.getSecondsAsString(3) + " s.).");
		
		//this.expansionManager.signalBuildKnowledge();
		
		this.phase = MAtModPhase.READY;
		
		this.isReady = true;
		MAtMod.LOGGER.info("Ready.");
		
		startRunning();
		this.expansionManager.signalReadyToTurnOn();
		
	}
	
	public void reloadAndStart()
	{
		if (!this.isReady)
			return;
		
		if (this.isRunning)
			return;
		
		new Thread() {
			@Override
			public void run()
			{
				TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
				MAtMod.this.expansionManager.loadExpansions();
				MAtMod.LOGGER.info("Expansions loaded (" + stat.getSecondsAsString(1) + "s).");
				
			}
		}.start();
		startRunning();
		
	}
	
	public void startRunning()
	{
		if (!this.isReady)
			return;
		
		if (this.isRunning)
			return;
		
		this.isRunning = true;
		
		MAtMod.LOGGER.fine("Loading...");
		this.expansionManager.modWasTurnedOnOrOff();
		MAtMod.LOGGER.fine("Loaded.");
		
	}
	
	public void stopRunning()
	{
		if (!this.isReady)
			return;
		
		if (!this.isRunning)
			return;
		
		this.isRunning = false;
		
		MAtMod.LOGGER.fine("Stopping...");
		this.expansionManager.modWasTurnedOnOrOff();
		MAtMod.LOGGER.fine("Stopped.");
		
		createDataDump();
		
	}
	
	private void createDataDump()
	{
		if (!this.shouldDumpData)
			return;
		
		MAtMod.LOGGER.fine("Dumping data.");
		
		try
		{
			File file = new File(Minecraft.getMinecraftDir(), "data_dump.xml");
			file.createNewFile();
			
			FileWriter fw = new FileWriter(file);
			fw.write(getDataGatherer().getData().createXML());
			fw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void printChat(Object... args)
	{
		final Object[] in = new Object[] { Ha3Utility.COLOR_WHITE, "MAtmos: " };
		
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
	
	public Ha3SoundCommunicator sound()
	{
		return this.sndComm;
		
	}
	
	public boolean isReady()
	{
		return this.isReady;
		
	}
	
	public boolean isRunning()
	{
		return this.isRunning;
		
	}
	
	public boolean isFatalError()
	{
		return this.fatalError;
		
	}
	
	public MAtModPhase getPhase()
	{
		return this.phase;
		
	}
	
	@Override
	public void onKey(KeyBinding event)
	{
		this.userControl.communicateKeyBindingEvent(event);
		
	}
	
	@Override
	public void onFrame(float semi)
	{
		if (this.fatalError)
			return;
		
		if (!this.isRunning)
			return;
		
		this.expansionManager.soundRoutine();
		this.soundManagerMaster.routine();
		
	}
	
	@Override
	public void onTick()
	{
		if (!this.fatalError)
		{
			this.userControl.tickRoutine();
			if (this.isRunning)
			{
				this.dataGatherer.tickRoutine();
				this.expansionManager.dataRoutine();
				
			}
			
			if (!this.firstTickPassed)
			{
				this.firstTickPassed = true;
				this.updateNotifier.attempt();
				
			}
			
		}
		else if (!this.userKnowsFatalError)
		{
			this.userKnowsFatalError = true;
			
			printChat(Ha3Utility.COLOR_YELLOW, "A fatal error has occured. MAtmos will not load.");
			if (!new File(Minecraft.getMinecraftDir(), "matmos/").exists())
			{
				printChat(Ha3Utility.COLOR_WHITE, "Are you sure you installed MAtmos correctly?");
				printChat(
					Ha3Utility.COLOR_WHITE, "The folder called ", Ha3Utility.COLOR_YELLOW, ".minecraft/matmos/",
					Ha3Utility.COLOR_YELLOW, " was NOT found. This folder should exist on a normal installation.");
				
			}
			manager().hookTickEvents(false);
			manager().hookFrameEvents(false);
			
		}
		
	}
	
	public void saveConfig()
	{
		if (this.configuration.commit())
		{
			this.configuration.save();
		}
		
	}
	
	public ConfigProperty getConfiguration()
	{
		return this.configuration;
		
	}
	
	public boolean isStartEnabled()
	{
		return this.configuration.getBoolean("start.enabled");
	}
	
	public void setStartEnabled(boolean startEnabled)
	{
		this.configuration.setProperty("start.enabled", startEnabled);
		
	}
	
}
