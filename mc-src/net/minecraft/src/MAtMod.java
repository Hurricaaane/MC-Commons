package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import eu.ha3.matmos.engine.MAtmosLogger;
import eu.ha3.mc.convenience.Ha3Personalizable;
import eu.ha3.mc.convenience.Ha3Signal;
import eu.ha3.mc.convenience.Ha3StaticUtilities;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsKeyEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;

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

public class MAtMod extends HaddonImpl
	implements SupportsFrameEvents, SupportsTickEvents, SupportsKeyEvents, Ha3Personalizable
{
	final static public Logger LOGGER = Logger.getLogger("MAtmos");
	final public int VERSION = 13; // Remember to change the thing on mod_Matmos_forModLoader
	private Properties config;
	private ConsoleHandler conMod;
	private ConsoleHandler conEngine;
	
	private Ha3SoundCommunicator sndComm;
	
	private final boolean defBypassResourceLoaderWait = true;
	private final boolean defAllowDump = true;
	private boolean bypassResourceLoaderWait;
	private boolean allowDump;
	
	private MAtUserControl userControl;
	private MAtDataGatherer dataGatherer;
	private MAtExpansionLoader expansionLoader;
	private MAtOptions options;
	private MAtUpdateNotifier updateNotifier;
	
	private MAtSoundManagerConfigurable soundManager;
	
	private boolean isRunning;
	private Map<String, Object> arbitraryPool;
	
	private boolean isReady;
	private boolean fatalError;
	
	private boolean userKnowsFatalError;
	private boolean firstTickPassed;
	
	private MAtModPhase phase;
	
	public MAtMod()
	{
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
		
		// TODO Customizable level
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
	
	public void setModLogger(Level lvl)
	{
		MAtMod.LOGGER.setLevel(lvl);
		this.conMod.setLevel(lvl);
		
	}
	
	public void setEngineLogger(Level lvl)
	{
		MAtmosLogger.LOGGER.setLevel(lvl);
		this.conEngine.setLevel(lvl);
		
	}
	
	@Override
	public void onLoad()
	{
		long beginTime = System.currentTimeMillis();
		
		this.isRunning = false;
		this.isReady = false;
		
		this.bypassResourceLoaderWait = this.defBypassResourceLoaderWait;
		this.allowDump = this.defAllowDump;
		
		this.arbitraryPool = new HashMap<String, Object>();
		
		this.sndComm = new Ha3SoundCommunicator(this, "MAtmos_");
		
		this.userControl = new MAtUserControl(this);
		this.dataGatherer = new MAtDataGatherer(this);
		this.expansionLoader = new MAtExpansionLoader(this);
		this.options = new MAtOptions(this);
		this.updateNotifier = new MAtUpdateNotifier(this);
		
		this.soundManager = new MAtSoundManagerConfigurable(this);
		
		manager().hookFrameEvents(true);
		manager().hookTickEvents(true);
		
		doLoad();
		
		MAtMod.LOGGER.info("Took "
			+ Math.floor(System.currentTimeMillis() - beginTime) / 1000f + " seconds to load MAtmos.");
		
	}
	
	public void doLoad()
	{
		this.phase = MAtModPhase.CONSTRUCTING;
		
		MAtMod.LOGGER.info("Constructing.");
		
		this.arbitraryPool.put("sndcomm_startload", new Long(System.currentTimeMillis()));
		this.userControl.load();
		this.dataGatherer.load();
		// note: soundManager needs to be loaded post sndcomms
		
		this.options.registerPersonalizable(this);
		this.options.registerPersonalizable(this.soundManager);
		this.options.registerPersonalizable(this.updateNotifier);
		this.options.loadOptions(); // TODO Options
		
		this.sndComm.load(new Ha3Signal() {
			@Override
			public void signal()
			{
				sndCommLoadFinishedPhaseOne();
				
			}
		}, new Ha3Signal() {
			
			@Override
			public void signal()
			{
				sndCommFailed();
				
			}
		});
		
		this.expansionLoader.renewProngs();
		this.expansionLoader.loadExpansions();
		
	}
	
	public MAtSoundManagerConfigurable soundManager()
	{
		return this.soundManager;
		
	}
	
	public MAtDataGatherer dataGatherer()
	{
		return this.dataGatherer;
		
	}
	
	public MAtOptions options()
	{
		return this.options;
		
	}
	
	// XXX Blatant design.
	public MAtExpansionLoader expansionLoader()
	{
		return this.expansionLoader;
		
	}
	
	private void sndCommFailed()
	{
		long diff = System.currentTimeMillis() - (Long) this.arbitraryPool.get("sndcomm_startload");
		float diffs = diff / 1000F;
		
		this.phase = MAtModPhase.SOUNDCOMMUNICATOR_FAILURE;
		MAtMod.LOGGER.severe("CRITICAL Error with SoundCommunicator (after " + diffs + " s.). Will not load.");
		
		this.fatalError = true;
		this.phase = MAtModPhase.SOUNDCOMMUNICATOR_FAILURE;
		
	}
	
	private String getFirstBlocker()
	{
		manager().getMinecraft();
		File folder = new File(Minecraft.getMinecraftDir(), "matmos_audiomodlike_blacklist/");
		
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
	
	private void sndCommLoadFinishedPhaseOne()
	{
		this.phase = MAtModPhase.RESOURCE_LOADER;
		
		long diff = System.currentTimeMillis() - (Long) this.arbitraryPool.get("sndcomm_startload");
		float diffs = diff / 1000F;
		
		MAtMod.LOGGER.info("SoundCommunicator loaded (after " + diffs + " s.).");
		
		String firstBlocker = getFirstBlocker();
		if (firstBlocker != null)
		{
			MAtMod.LOGGER.warning(firstBlocker);
			MAtMod.LOGGER.warning("MAtmos will not attempt load sounds on its own at all.");
			sndCommLoaded();
			
		}
		else if (!this.bypassResourceLoaderWait)
		{
			new MAtResourceReloader(this, new Ha3Signal() {
				
				@Override
				public void signal()
				{
					sndCommLoaded();
					
				}
				
			}).start();
			
		}
		else
		{
			MAtMod.LOGGER.info("Bypassing Resource Reloader threaded wait. This may cause issues.");
			
			new MAtResourceReloader(this, null).reloadResources();
			sndCommLoaded();
			
		}
		
	}
	
	private void sndCommLoaded()
	{
		this.phase = MAtModPhase.FINAL_PHASE;
		
		long diff = System.currentTimeMillis() - (Long) this.arbitraryPool.get("sndcomm_startload");
		float diffs = diff / 1000F;
		
		MAtMod.LOGGER.info("ResourceReloader finished (after " + diffs + " s.).");
		
		// options.loadPostSndComms();
		// soundManager.load();
		
		this.expansionLoader.signalBuildKnowledge();
		
		this.phase = MAtModPhase.READY;
		
		this.isReady = true;
		MAtMod.LOGGER.info("Ready.");
		
		startRunning();
		
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
				MAtMod.this.expansionLoader.loadExpansions();
				
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
		
		//printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Loading...");
		
		MAtMod.LOGGER.fine("Loading...");
		this.expansionLoader.signalStatusChange();
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
		this.expansionLoader.signalStatusChange();
		MAtMod.LOGGER.fine("Stopped.");
		
		createDataDump();
		
	}
	
	public void createDataDump()
	{
		if (!this.allowDump)
			return;
		
		MAtMod.LOGGER.fine("Dumping data.");
		
		try
		{
			File file = new File(Minecraft.getMinecraftDir(), "data_dump.xml");
			file.createNewFile();
			
			FileWriter fw = new FileWriter(file);
			fw.write(dataGatherer().getData().createXML());
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
		if (!this.fatalError)
		{
			this.userControl.frameRoutine(semi);
			
			if (this.isRunning)
			{
				this.expansionLoader.soundRoutine();
				
			}
			
		}
		
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
				this.expansionLoader.dataRoutine();
				
			}
			
		}
		else if (!this.userKnowsFatalError)
		{
			this.userKnowsFatalError = true;
			printChat(Ha3Utility.COLOR_YELLOW, "A fatal error has occured. MAtmos will not load.");
			
		}
		if (!this.firstTickPassed)
		{
			this.firstTickPassed = true;
			this.updateNotifier.attempt();
			
		}
		
	}
	
	@Override
	public void inputOptions(Properties options)
	{
		if (this.config == null)
		{
			this.config = createDefaultOptions();
		}
		
		try
		{
			{
				String query = "debug.logger.mod.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					int lvl = Integer.parseInt(prop);
					setModLogger(lvl == 0 ? Level.INFO : lvl == 1 ? Level.FINE : lvl == 2 ? Level.FINER : lvl == 3
						? Level.FINEST : Level.INFO);
					this.config.put(query, prop);
				}
				
			}
			{
				String query = "debug.logger.engine.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					int lvl = Integer.parseInt(prop);
					setEngineLogger(lvl == 0 ? Level.INFO : lvl == 1 ? Level.FINE : lvl == 2 ? Level.FINER : lvl == 3
						? Level.FINEST : Level.INFO);
					this.config.put(query, prop);
				}
				
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace(); // TODO Logger could not input soundmanagerconf options
			
		}
		try
		{
			{
				String query = "core.init.bypassresourcereloaderwait.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					this.bypassResourceLoaderWait = Integer.parseInt(prop) == 1 ? true : false;
					this.config.put(query, prop);
				}
				
			}
			{
				String query = "core.data.dump.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					this.allowDump = Integer.parseInt(prop) == 1 ? true : false;
					this.config.put(query, prop);
				}
				
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace(); // TODO Logger could not input soundmanagerconf options
			
		}
		
	}
	
	@Override
	public Properties outputOptions()
	{
		if (this.config == null)
			return createDefaultOptions();
		
		// XXX Options do not change if changed due to code.
		//config.setProperty("debug.logger.mod.use", "0");
		//config.setProperty("debug.logger.engine.use", "0");
		
		this.config.setProperty("core.init.bypassresourcereloaderwait.use", this.bypassResourceLoaderWait ? "1" : "0");
		this.config.setProperty("core.data.dump.use", this.allowDump ? "1" : "0");
		
		return this.config;
	}
	
	@Override
	public void defaultOptions()
	{
		inputOptions(createDefaultOptions());
		
	}
	
	private Properties createDefaultOptions()
	{
		Properties options = new Properties();
		options.setProperty("debug.logger.mod.use", "0");
		options.setProperty("debug.logger.engine.use", "0");
		options.setProperty("core.init.bypassresourcereloaderwait.use", this.defBypassResourceLoaderWait ? "1" : "0");
		options.setProperty("core.data.dump.use", this.defAllowDump ? "1" : "0");
		
		return options;
		
	}
	
}
