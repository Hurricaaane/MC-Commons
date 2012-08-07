package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;

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

public class MAtExpansionLoader
{
	private File expansionsFolder;
	private File onlineStorageFolder;
	
	private MAtMod mod;
	
	private Map<String, MAtExpansion> expansions;
	private List<String> limbo;
	private List<String> dummy;
	private List<String> loading;
	
	private boolean canBuildKnowledge;
	private List<MAtExpansionEventListener> eventListeners;
	
	MAtExpansionLoader(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		this.canBuildKnowledge = false;
		
		this.expansions = new ConcurrentHashMap<String, MAtExpansion>();
		this.dummy = new ArrayList<String>();
		this.limbo = new ArrayList<String>();
		this.loading = new ArrayList<String>();
		
		this.eventListeners = new ArrayList<MAtExpansionEventListener>();
		
		this.expansionsFolder = new File(Minecraft.getMinecraftDir(), "matmos/expansions_r12/");
		this.onlineStorageFolder = new File(Minecraft.getMinecraftDir(), "matmos/internal/storage/");
		
		if (!this.expansionsFolder.exists())
		{
			this.expansionsFolder.mkdirs();
		}
		
		if (!this.onlineStorageFolder.exists())
		{
			this.onlineStorageFolder.mkdirs();
		}
		
		this.tasks = new ArrayList<Runnable>();
		
	}
	
	public synchronized void renewProngs()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			renewExpansionProngs(expansion);
			
		}
		
	}
	
	private synchronized void renewExpansionProngs(MAtExpansion expansion)
	{
		expansion.setSoundManager(this.mod.soundManager());
		expansion.setData(this.mod.dataGatherer().getData());
		
	}
	
	public void addExpansionFromFile(String userDefinedIdentifier, File file)
	{
		try
		{
			addExpansion(userDefinedIdentifier, new FileInputStream(file));
			
		}
		catch (FileNotFoundException e)
		{
			MAtMod.LOGGER.warning("Error with FileNotFound on ExpansionLoader (on file "
				+ file.getAbsolutePath() + ").");
			this.dummy.add(userDefinedIdentifier);
			
		}
		
	}
	
	public void addExpansionFromURL(String userDefinedIdentifier, URL url)
	{
		MAtExpansionFetcher fetcher = new MAtExpansionFetcher(this, userDefinedIdentifier);
		this.limbo.add(userDefinedIdentifier);
		
		for (MAtExpansionEventListener listener : this.eventListeners)
		{
			listener.addedLimbo(userDefinedIdentifier);
		}
		
		fetcher.getDatabase(url);
		
	}
	
	private void fetcherSignalAfterLimboRemoval()
	{
		if (this.limbo.size() == 0)
		{
			MAtMod.LOGGER.info("ExpansionLoader Limbo is now empty.");
		}
		
	}
	
	public synchronized void fetcherSuccess(String userDefinedIdentifier, InputStream stream)
	{
		MAtMod.LOGGER.info("ExpansionLoader fetched " + userDefinedIdentifier + ".");
		
		this.limbo.remove(userDefinedIdentifier);
		
		for (MAtExpansionEventListener listener : this.eventListeners)
		{
			listener.successLimbo(userDefinedIdentifier);
		}
		
		fetcherSignalAfterLimboRemoval();
		
		addExpansion(userDefinedIdentifier, stream);
		if (this.expansions.get(userDefinedIdentifier).hasStructure())
		{
			writeExpansion(userDefinedIdentifier);
			
		}
		else
		{
			this.expansions.remove(userDefinedIdentifier);
			fetherRescue(userDefinedIdentifier);
			
		}
		
	}
	
	public void fetcherFailure(String userDefinedIdentifier)
	{
		MAtMod.LOGGER.info("ExpansionLoader failed fetching " + userDefinedIdentifier + ".");
		
		this.limbo.remove(userDefinedIdentifier);
		
		for (MAtExpansionEventListener listener : this.eventListeners)
		{
			listener.failureLimbo(userDefinedIdentifier);
		}
		
		fetcherSignalAfterLimboRemoval();
		
		fetherRescue(userDefinedIdentifier);
		
	}
	
	private void fetherRescue(String userDefinedIdentifier)
	{
		//System.out.println(identifierToStorage(userDefinedIdentifier));
		addExpansionFromFile(userDefinedIdentifier, identifierToStorage(userDefinedIdentifier));
		
	}
	
	private void writeExpansion(String userDefinedIdentifier)
	{
		if (!this.expansions.containsKey(userDefinedIdentifier))
			return;
		
		String form = this.expansions.get(userDefinedIdentifier).getDocumentStringForm();
		
		if (form != null)
		{
			FileWriter fileWriter;
			try
			{
				fileWriter = new FileWriter(identifierToStorage(userDefinedIdentifier));
				fileWriter.write(form);
				fileWriter.close();
			}
			catch (IOException e)
			{
				MAtMod.LOGGER.warning("Error with I/O on ExpansionLoader about "
					+ userDefinedIdentifier + "(Could not store).");
				e.printStackTrace();
			}
			
		}
		
	}
	
	private File identifierToStorage(String userDefinedIdentifier)
	{
		return new File(this.onlineStorageFolder, userDefinedIdentifier + ".xmlo");
		
	}
	
	public synchronized void removeExpansion(String userDefinedIdentifier)
	{
		if (this.expansions.containsKey(userDefinedIdentifier))
		{
			MAtExpansion expansion = this.expansions.get(userDefinedIdentifier);
			
			for (MAtExpansionEventListener listener : this.eventListeners)
			{
				listener.removedExpansion(expansion);
			}
			
			expansion.turnOff();
			this.expansions.remove(userDefinedIdentifier);
		}
		
	}
	
	public synchronized void addExpansion(String userDefinedIdentifier, InputStream stream)
	{
		MAtExpansion expansion = new MAtExpansion(userDefinedIdentifier);
		this.loading.add(expansion.getUserDefinedName());
		renewExpansionProngs(expansion);
		expansion.inputStructure(stream);
		this.expansions.put(userDefinedIdentifier, expansion);
		
		for (MAtExpansionEventListener listener : this.eventListeners)
		{
			listener.addedExpansion(expansion);
		}
		
		tryBuildKnowledge(expansion);
		
	}
	
	private synchronized void tryBuildKnowledge(MAtExpansion expansion)
	{
		if (!this.canBuildKnowledge)
			return;
		
		// TODO If the expansion is set by the user not to load, don't load it
		// XXX If the expansion is set by the user not to load, don't load it
		
		expansion.buildKnowledge();
		this.loading.remove(expansion.getUserDefinedName());
		
		MAtMod.LOGGER.info("Expansion " + expansion.getUserDefinedName() + " loaded.");
		
		if (this.loading.size() == 0 && this.limbo.size() == 0)
		{
			MAtMod.LOGGER.info("ExpansionLoader Loading and Limbo are now empty.");
		}
		
		if (!expansion.hasStructure())
		{
			MAtMod.LOGGER.warning("Expansion " + expansion.getUserDefinedName() + " has no structure.");
		}
		
		postBuildKnowledge(expansion.getUserDefinedName());
		
	}
	
	public synchronized void signalBuildKnowledge()
	{
		this.canBuildKnowledge = true;
		
		MAtMod.LOGGER.info("ExpansionLoader signaled. Currently has "
			+ this.expansions.size() + " expansions and " + this.limbo.size() + " in limbo.");
		
		// Try build the knowledge of expansions that were structured before buildknowledge was ready
		for (MAtExpansion expansion : this.expansions.values())
		{
			tryBuildKnowledge(expansion);
		}
		
	}
	
	private void postBuildKnowledge(String userDefinedIdentifier)
	{
		// This is not called at all for expansions that are
		// set by the user not to load
		if (this.mod.isRunning())
		{
			this.expansions.get(userDefinedIdentifier).turnOn();
		}
		else
		{
			this.expansions.get(userDefinedIdentifier).turnOff();
		}
		
		// Debugging
		// expansions.get(userDefinedIdentifier).printKnowledge();
		
	}
	
	public synchronized void signalStatusChange()
	{
		// TODO Separate boolean for user defined preferences
		for (MAtExpansion expansion : this.expansions.values())
		{
			if (this.mod.isRunning())
			{
				expansion.turnOn(); // Turn on contains a "build knowledge if needed".
			}
			else
			{
				expansion.turnOff();
			}
			
		}
		
	}
	
	public int getLoadingCount()
	{
		return this.loading.size();
		
	}
	
	public void soundRoutine()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			expansion.soundRoutine();
			
		}
		
	}
	
	private void loadTask()
	{
		if (this.tasks.isEmpty())
			return;
		
		try
		{
			this.tasks.remove(0).run();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void dataRoutine()
	{
		loadTask();
		
		for (MAtExpansion expansion : this.expansions.values())
		{
			expansion.dataRoutine();
			
		}
		
	}
	
	public void clearExpansions()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			expansion.turnOff();
			expansion.patchKnowledge();
			
		}
		this.expansions.clear();
		
	}
	
	public void loadExpansions()
	{
		clearExpansions();
		addOnlineFromFolderRecursive(this.expansionsFolder, "");
		addExpansionFromFolderRecursive(this.expansionsFolder, "");
		
	}
	
	private void addExpansionFromFolderRecursive(File file, String s)
	{
		if (!file.exists())
			return;
		
		for (File individual : file.listFiles())
		{
			if (individual.isDirectory())
			{
				/*addExpansionFromFolderRecursive(afile[i], s + afile[i].getName() + "/");
				continue;*/
				;
				// TODO What should be done about expansions in subfolders?
				
			}
			else if (individual.getName().endsWith(".xml"))
			{
				MAtMod.LOGGER.info("ExpansionLoader found potential expansion " + individual.getName() + ".");
				addExpansionFromFile(individual.getName(), individual);
				
			}
			
		}
		
	}
	
	private void addOnlineFromFolderRecursive(File file, String s)
	{
		if (!file.exists())
			return;
		
		for (File individual : file.listFiles())
		{
			if (individual.isDirectory())
			{
				;
				
			}
			else if (individual.getName().endsWith(".xrl"))
			{
				MAtMod.LOGGER.info("ExpansionLoader found potential online " + individual.getName() + ".");
				addOnlineFromFile(individual.getName(), individual);
				
			}
			
		}
		
	}
	
	private void addOnlineFromFile(String userDefinedIdentifier, File file)
	{
		boolean win = false;
		
		// TODO Weird exception handling
		try
		{
			BufferedReader buff = new BufferedReader(new FileReader(file));
			try
			{
				addExpansionFromURL(userDefinedIdentifier, new URL(buff.readLine()));
				
				win = true;
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				buff.close();
				
			}
			
		}
		catch (FileNotFoundException e)
		{
		}
		catch (IOException e)
		{
			e.printStackTrace();
			MAtMod.LOGGER.warning(e.getMessage());
		}
		
		if (!win)
		{
			this.dummy.add(userDefinedIdentifier);
		}
		
	}
	
	public void addEventListener(MAtExpansionEventListener listener)
	{
		this.eventListeners.add(listener);
		
	}
	
	private List<Runnable> tasks;
	
	/**
	 * Adds a task to the list of tasks to run by the expansion loader.
	 * 
	 * @param runnable
	 */
	public void putTask(Runnable runnable)
	{
		this.tasks.add(runnable);
		
	}
	
	/**
	 * Routing that executes when Minecraft is in low usage mode. Typically,
	 * this occurs in the main menu of Minecraft.
	 * 
	 */
	public void lowUsageRoutine()
	{
		if (this.tasks.isEmpty())
			return;
		
		long startTime = System.currentTimeMillis();
		MAtMod.LOGGER.info("Loading tasks...");
		
		int taskCount = 0;
		while (!this.tasks.isEmpty())
		{
			loadTask();
			taskCount++;
		}
		
		MAtMod.LOGGER.info("Took "
			+ (System.currentTimeMillis() - startTime) / 1000f + "s to finish loading " + taskCount + " tasks.");
		
	}
	
}
