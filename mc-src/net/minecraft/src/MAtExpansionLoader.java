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
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
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
		
		expansionsFolder = new File(Minecraft.getMinecraftDir(),
				"matmos_expansions_r7/");
		onlineStorageFolder = new File(Minecraft.getMinecraftDir(),
				"matmos_internal/storage/");
		
		if (!expansionsFolder.exists())
			expansionsFolder.mkdirs();
		
		if (!onlineStorageFolder.exists())
			onlineStorageFolder.mkdirs();
		
		tasks = new ArrayList<Runnable>();

	}
	
	public synchronized void renewProngs()
	{
		for (MAtExpansion expansion : expansions.values())
		{
			renewExpansionProngs(expansion);
			
		}
		
	}
	
	private synchronized void renewExpansionProngs(MAtExpansion expansion)
	{
		expansion.setSoundManager(mod.soundManager());
		expansion.setData(mod.dataGatherer().getData());
		
	}
	
	public void addExpansionFromFile(String userDefinedIdentifier, File file)
	{
		try
		{
			addExpansion(userDefinedIdentifier, new FileInputStream(file));
			
		}
		catch (FileNotFoundException e)
		{
			MAtMod.LOGGER
			.warning("Error with FileNotFound on ExpansionLoader (on file "
					+ file.getAbsolutePath() + ").");
			dummy.add(userDefinedIdentifier);
			
		}
		
	}
	
	public void addExpansionFromURL(String userDefinedIdentifier,
			URL url)
	{
		MAtExpansionFetcher fetcher = new MAtExpansionFetcher(this,
				userDefinedIdentifier);
		limbo.add(userDefinedIdentifier);
		
		for (MAtExpansionEventListener listener : eventListeners)
			listener.addedLimbo(userDefinedIdentifier);
		
		fetcher.getDatabase(url);
		
	}
	
	private void fetcherSignalAfterLimboRemoval()
	{
		if (limbo.size() == 0)
			MAtMod.LOGGER.info("ExpansionLoader Limbo is now empty.");
		
	}
	
	public synchronized void fetcherSuccess(String userDefinedIdentifier,
			InputStream stream)
	{
		MAtMod.LOGGER.info("ExpansionLoader fetched " + userDefinedIdentifier
				+ ".");
		
		limbo.remove(userDefinedIdentifier);
		
		for (MAtExpansionEventListener listener : eventListeners)
			listener.successLimbo(userDefinedIdentifier);
		
		fetcherSignalAfterLimboRemoval();
		
		addExpansion(userDefinedIdentifier, stream);
		if (expansions.get(userDefinedIdentifier).hasStructure())
		{
			writeExpansion(userDefinedIdentifier);
			
		}
		else
		{
			expansions.remove(userDefinedIdentifier);
			fetherRescue(userDefinedIdentifier);
			
		}
		
	}
	
	public void fetcherFailure(String userDefinedIdentifier)
	{
		MAtMod.LOGGER.info("ExpansionLoader failed fetching "
				+ userDefinedIdentifier + ".");
		
		limbo.remove(userDefinedIdentifier);
		
		for (MAtExpansionEventListener listener : eventListeners)
			listener.failureLimbo(userDefinedIdentifier);
		
		fetcherSignalAfterLimboRemoval();
		
		fetherRescue(userDefinedIdentifier);
		
	}
	
	private void fetherRescue(String userDefinedIdentifier)
	{
		//System.out.println(identifierToStorage(userDefinedIdentifier));
		addExpansionFromFile(userDefinedIdentifier,
				identifierToStorage(userDefinedIdentifier));
		
	}
	
	private void writeExpansion(String userDefinedIdentifier)
	{
		if (!expansions.containsKey(userDefinedIdentifier))
			return;
		
		String form = expansions.get(userDefinedIdentifier)
				.getDocumentStringForm();
		
		if (form != null)
		{
			FileWriter fileWriter;
			try
			{
				fileWriter = new FileWriter(
						identifierToStorage(userDefinedIdentifier));
				fileWriter.write(form);
				fileWriter.close();
			}
			catch (IOException e)
			{
				MAtMod.LOGGER
				.warning("Error with I/O on ExpansionLoader about "
						+ userDefinedIdentifier + "(Could not store).");
				e.printStackTrace();
			}
			
		}
		
	}
	
	private File identifierToStorage(String userDefinedIdentifier)
	{
		return new File(onlineStorageFolder, userDefinedIdentifier + ".xmlo");
		
	}
	
	public synchronized void removeExpansion(String userDefinedIdentifier)
	{
		if (expansions.containsKey(userDefinedIdentifier))
		{
			MAtExpansion expansion = expansions.get(userDefinedIdentifier);
			
			for (MAtExpansionEventListener listener : eventListeners)
				listener.removedExpansion(expansion);
			
			expansion.turnOff();
			expansions.remove(userDefinedIdentifier);
		}
		
		
	}
	
	public synchronized void addExpansion(String userDefinedIdentifier,
			InputStream stream)
	{
		MAtExpansion expansion = new MAtExpansion(userDefinedIdentifier);
		loading.add(expansion.getUserDefinedName());
		renewExpansionProngs(expansion);
		expansion.inputStructure(stream);
		expansions.put(userDefinedIdentifier, expansion);
		
		for (MAtExpansionEventListener listener : eventListeners)
			listener.addedExpansion(expansion);
		
		tryBuildKnowledge(expansion);
		
	}
	
	private synchronized void tryBuildKnowledge(MAtExpansion expansion)
	{
		if (!canBuildKnowledge)
			return;
		
		// TODO If the expansion is set by the user not to load, don't load it
		// XXX If the expansion is set by the user not to load, don't load it
		
		expansion.buildKnowledge();
		loading.remove(expansion.getUserDefinedName());
		
		MAtMod.LOGGER.info("Expansion " + expansion.getUserDefinedName()
				+ " loaded.");
		
		if ((loading.size() == 0) && (limbo.size() == 0))
			MAtMod.LOGGER
			.info("ExpansionLoader Loading and Limbo are now empty.");
		
		if (!expansion.hasStructure())
			MAtMod.LOGGER.warning("Expansion " + expansion.getUserDefinedName()
					+ " has no structure.");
		
		postBuildKnowledge(expansion.getUserDefinedName());
		
	}
	
	
	public synchronized void signalBuildKnowledge()
	{
		canBuildKnowledge = true;
		
		MAtMod.LOGGER.info("ExpansionLoader signaled. Currently has "
				+ expansions.size() + " expansions and " + limbo.size()
				+ " in limbo.");
		
		// Try build the knowledge of expansions that were structured before buildknowledge was ready
		for (MAtExpansion expansion : expansions.values())
			tryBuildKnowledge(expansion);
		
	}
	
	private void postBuildKnowledge(String userDefinedIdentifier)
	{
		// This is not called at all for expansions that are
		// set by the user not to load
		if (mod.isRunning())
			expansions.get(userDefinedIdentifier).turnOn();
		
		else
			expansions.get(userDefinedIdentifier).turnOff();
		
		// Debugging
		// expansions.get(userDefinedIdentifier).printKnowledge();
		
	}
	
	public synchronized void signalStatusChange()
	{
		// TODO Separate boolean for user defined preferences
		for (MAtExpansion expansion : expansions.values())
		{
			if (mod.isRunning())
				expansion.turnOn(); // Turn on contains a "build knowledge if needed".
			
			else
				expansion.turnOff();
			
		}
		
	}
	
	public int getLoadingCount()
	{
		return loading.size();
		
	}
	
	public void soundRoutine()
	{
		for (MAtExpansion expansion : expansions.values())
		{
			expansion.soundRoutine();
			
		}
		
	}
	
	public void dataRoutine()
	{
		if (!tasks.isEmpty())
		{
			for (Runnable runnable : tasks)
			{
				try
				{
					runnable.run();
					
				}
				catch (Exception e)
				{
				}
				
			}
			tasks.clear();
			
		}

		for (MAtExpansion expansion : expansions.values())
		{
			expansion.dataRoutine();
			
		}
		
	}
	
	public void clearExpansions()
	{
		for (MAtExpansion expansion : expansions.values())
		{
			expansion.turnOff();
			expansion.patchKnowledge();
			
		}
		expansions.clear();
		
	}
	
	public void loadExpansions()
	{
		clearExpansions();
		addOnlineFromFolderRecursive(expansionsFolder, "");
		addExpansionFromFolderRecursive(expansionsFolder, "");
		
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
				MAtMod.LOGGER.info("ExpansionLoader found potential expansion "
						+ individual.getName() + ".");
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
				MAtMod.LOGGER.info("ExpansionLoader found potential online "
						+ individual.getName() + ".");
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
			FileReader fr = new FileReader(file);
			
			try
			{
				BufferedReader buff = new BufferedReader(fr);
				addExpansionFromURL(userDefinedIdentifier, new URL(buff
						.readLine()));
				
				win = true;
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			fr.close();
			
		}
		catch (FileNotFoundException e)
		{
			MAtMod.LOGGER
			.warning("Error with FileNotFound on ExpansionLoader (on file "
					+ file.getAbsolutePath() + ").");
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if (!win)
			dummy.add(userDefinedIdentifier);
		
	}
	
	public void addEventListener(MAtExpansionEventListener listener)
	{
		eventListeners.add(listener);
		
	}
	
	private List<Runnable> tasks;
	
	public void putTask(Runnable runnable)
	{
		tasks.add(runnable);
		
	}

}
