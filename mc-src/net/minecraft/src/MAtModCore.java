package net.minecraft.src;


import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3Personalizable;
import eu.ha3.mc.convenience.Ha3Signal;
import eu.ha3.mc.mod.Ha3Mod;
import eu.ha3.mc.mod.Ha3ModCore;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
 */

public class MAtModCore implements Ha3ModCore, Ha3Personalizable
{
	private MAtMod mod;
	private Ha3Utility utility;
	private Ha3SoundCommunicator sndComm;
	
	private final boolean defBypassResourceLoaderWait = true;
	private final boolean defAsyncLoad = true;
	private final boolean defAllowDump = true;
	private boolean bypassResourceLoaderWait;
	private boolean asyncLoad;
	private boolean allowDump;
	
	private MAtUserControl userControl;
	private MAtDataGatherer dataGatherer;
	private MAtExpansionLoader expansionLoader;
	private MAtOptions options;
	private MAtUpdateNotifier updateNotifier;
	
	private MAtSoundManagerConfigurable soundManager;
	
	private boolean isRunning;
	private int lastTick;
	
	private Map<String, Object> arbitraryPool;
	
	private boolean isReady;
	private boolean fatalError;
	
	private boolean userKnowsFatalError;
	private boolean firstTickPassed;
	
	private MAtModPhase phase;
	
	private Properties config;
	
	@Override
	public void setMod(Ha3Mod modIn)
	{
		mod = (MAtMod) modIn;
		phase = MAtModPhase.NOT_INITIALIZED;
		
	}
	
	@Override
	public void load()
	{
		isRunning = false;
		isReady = false;
		
		bypassResourceLoaderWait = defBypassResourceLoaderWait;
		asyncLoad = defAsyncLoad;
		allowDump = defAllowDump;
		
		arbitraryPool = new HashMap<String, Object>();
		
		mod.manager().setUsesFrame(true);
		
		utility = new Ha3Utility(mod);
		sndComm = new Ha3SoundCommunicator(mod, "MAtmos_");
		
		userControl = new MAtUserControl(mod);
		dataGatherer = new MAtDataGatherer(mod);
		expansionLoader = new MAtExpansionLoader(mod);
		options = new MAtOptions(mod);
		updateNotifier = new MAtUpdateNotifier(mod);
		
		soundManager = new MAtSoundManagerConfigurable(mod);
		
		doLoad();
		
	}
	
	public void doLoad()
	{
		phase = MAtModPhase.CONSTRUCTING;
		
		MAtMod.LOGGER.info("Constructing.");
		
		arbitraryPool.put("sndcomm_startload", new Long(System
				.currentTimeMillis()));
		userControl.load();
		dataGatherer.load();
		// note: soundManager needs to be loaded post sndcomms
		
		options.registerPersonalizable(mod);
		options.registerPersonalizable(this);
		options.registerPersonalizable(soundManager);
		options.registerPersonalizable(updateNotifier);
		options.loadOptions(); // TODO Options
		sndComm.load(new Ha3Signal() {
			
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
		
		expansionLoader.renewProngs();
		expansionLoader.loadExpansions();
		
	}
	
	public MAtSoundManagerConfigurable soundManager()
	{
		return soundManager;
		
	}
	
	public MAtDataGatherer dataGatherer()
	{
		return dataGatherer;
		
	}
	
	public MAtOptions options()
	{
		return options;
		
	}
	
	// XXX Blatant design.
	public MAtExpansionLoader expansionLoader()
	{
		return expansionLoader;
		
	}
	
	private void sndCommFailed()
	{
		long diff = (System.currentTimeMillis() - (Long) arbitraryPool
				.get("sndcomm_startload"));
		float diffs = diff / 1000F;
		
		phase = MAtModPhase.SOUNDCOMMUNICATOR_FAILURE;
		MAtMod.LOGGER
		.severe("CRITICAL Error with SoundCommunicator (after "
				+ diffs + " s.). Will not load.");
		
		fatalError = true;
		phase = MAtModPhase.SOUNDCOMMUNICATOR_FAILURE;
		
	}
	
	private void sndCommLoadFinishedPhaseOne()
	{
		phase = MAtModPhase.RESOURCE_LOADER;
		
		long diff = (System.currentTimeMillis() - (Long) arbitraryPool
				.get("sndcomm_startload"));
		float diffs = diff / 1000F;
		
		MAtMod.LOGGER
		.info("SoundCommunicator loaded (after " + diffs + " s.).");
		
		if (!bypassResourceLoaderWait)
		{
			new MAtResourceReloader(mod, new Ha3Signal() {
				
				@Override
				public void signal()
				{
					sndCommLoaded();
					
				}
				
			}).start();
			
		}
		else
		{
			MAtMod.LOGGER
			.info("Bypassing Resource Reloader. This may cause issues.");
			
			new MAtResourceReloader(mod, null).reloadResources();
			sndCommLoaded();
			
		}
		
	}
	
	private void sndCommLoaded()
	{
		phase = MAtModPhase.FINAL_PHASE;
		
		long diff = (System.currentTimeMillis() - (Long) arbitraryPool
				.get("sndcomm_startload"));
		float diffs = diff / 1000F;
		
		MAtMod.LOGGER.info("ResourceReloader finished (after " + diffs
				+ " s.).");
		
		// options.loadPostSndComms();
		// soundManager.load();
		
		// XXX TEMPORARY FIX: DISABLED ASYNCHRONOUS LOADING COMPLETELY
		if (false && asyncLoad)
		{
			Thread t = new Thread() {
				@Override
				public void run()
				{
					expansionLoader.signalBuildKnowledge();
					
				}
			};
			t.start();
		}
		else
			expansionLoader.signalBuildKnowledge();
		
		phase = MAtModPhase.READY;
		
		isReady = true;
		MAtMod.LOGGER.info("Ready.");
		
		//printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Loading...");
		startRunning();
		
	}
	
	/*@Override
	public void unload()
	{
		// Do nothing
		
	}*/
	
	@Override
	public void doFrame(float fspan)
	{
		int tick = utility.getClientTick();
		if (utility.getClientTick() != lastTick)
		{
			lastTick = tick;
			onTick();
			
		}
		
		onFrame(fspan);
		
	}
	
	private void onFrame(float fspan)
	{
		if (!fatalError)
		{
			userControl.frameRoutine(fspan);
			
			if (isRunning)
			{
				expansionLoader.soundRoutine();
				
			}
			
		}
		
	}
	
	private void onTick()
	{
		if (!fatalError)
		{
			userControl.tickRoutine();
			if (isRunning)
			{
				dataGatherer.tickRoutine();
				expansionLoader.dataRoutine();
				
			}
			
		}
		else if (!userKnowsFatalError)
		{
			userKnowsFatalError = true;
			printChat(Ha3Utility.COLOR_YELLOW,
					"A fatal error has occured. MAtmos will not load.");
			
		}
		if (!firstTickPassed)
		{
			firstTickPassed = true;
			updateNotifier.attempt();
			
		}
		
	}
	
	@Override
	public void doKeyBindingEvent(KeyBinding event)
	{
		userControl.communicateKeyBindingEvent(event);
		
	}
	
	@Override
	public void doManagerReady()
	{
		// TODO MAtmos could be disabled by default?
		
	}
	
	public void reloadAndStart()
	{
		if (!isReady)
			return;
		
		if (isRunning)
			return;
		
		new Thread() {
			@Override
			public void run()
			{
				expansionLoader.loadExpansions();
				
			}
		}.start();
		startRunning();
		
	}
	
	public void startRunning()
	{
		if (!isReady)
			return;
		
		if (isRunning)
			return;
		
		isRunning = true;
		
		//printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Loading...");
		
		MAtMod.LOGGER.fine("Loading...");
		expansionLoader.signalStatusChange();
		MAtMod.LOGGER.fine("Loaded.");
		
	}
	
	public void stopRunning()
	{
		if (!isReady)
			return;
		
		if (!isRunning)
			return;
		
		isRunning = false;
		
		MAtMod.LOGGER.fine("Stopping...");
		expansionLoader.signalStatusChange();
		MAtMod.LOGGER.fine("Stopped.");
		
		createDataDump();
		
		/*printChat(Ha3Utility.COLOR_YELLOW, "Stopped. Press ",
				Ha3Utility.COLOR_WHITE,
				userControl.getKeyBindingMainFriendlyName(),
				Ha3Utility.COLOR_YELLOW, " to re-enable.");*/
		
	}
	
	public void createDataDump()
	{
		if (!allowDump)
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
	
	public Ha3Utility util()
	{
		return utility;
		
	}
	
	public Ha3SoundCommunicator sound()
	{
		return sndComm;
		
	}
	
	public boolean isReady()
	{
		return isReady;
		
	}
	
	public boolean isRunning()
	{
		return isRunning;
		
	}
	
	public boolean isFatalError()
	{
		return fatalError;
		
	}
	
	public MAtModPhase getPhase()
	{
		return phase;
		
	}
	
	@Override
	public void inputOptions(Properties options)
	{
		if (config == null)
			config = createDefaultOptions();
		
		try
		{
			{
				String query = "core.init.bypassresourcereloaderwait.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					bypassResourceLoaderWait = Integer.parseInt(prop) == 1
							? true : false;
					config.put(query, prop);
				}
				
			}
			{
				String query = "core.init.asyncstart.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					asyncLoad = Integer.parseInt(prop) == 1 ? true : false;
					config.put(query, prop);
				}
				
			}
			{
				String query = "core.data.dump.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					allowDump = Integer.parseInt(prop) == 1 ? true : false;
					config.put(query, prop);
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
		if (config == null)
			return createDefaultOptions();
		
		config.setProperty("core.init.bypassresourcereloaderwait.use",
				bypassResourceLoaderWait ? "1" : "0");
		config.setProperty("core.init.asyncstart.use", asyncLoad ? "1" : "0");
		config.setProperty("core.data.dump.use", allowDump ? "1" : "0");
		
		return config;
	}
	
	@Override
	public void defaultOptions()
	{
		inputOptions(createDefaultOptions());
		
	}
	
	private Properties createDefaultOptions()
	{
		Properties options = new Properties();
		options.setProperty("core.init.bypassresourcereloaderwait.use",
				defBypassResourceLoaderWait ? "1" : "0");
		options.setProperty("core.init.asyncstart.use", defAsyncLoad ? "1"
				: "0");
		options.setProperty("core.data.dump.use", defAllowDump ? "1" : "0");
		
		return options;
		
	}
	
}
