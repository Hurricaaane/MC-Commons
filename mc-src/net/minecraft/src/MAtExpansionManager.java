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

public class MAtExpansionManager
{
	private MAtMod mod;
	private Map<String, MAtExpansion> expansions;
	
	private File expansionsFolder;
	private File onlineStorageFolder;
	
	private boolean canBuildKnowledge;
	
	public MAtExpansionManager(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		
		this.expansions = new ConcurrentHashMap<String, MAtExpansion>();
		
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
		
	}
	
	private synchronized void renewExpansionProngs(MAtExpansion expansion)
	{
		expansion.setSoundManager(new MAtSoundManagerProxy(this.mod.getCentralSoundManager()));
		expansion.setData(this.mod.getDataGatherer().getData());
		
	}
	
	public void createExpansionEntry(String userDefinedIdentifier)
	{
		MAtExpansion expansion = new MAtExpansion(userDefinedIdentifier);
		this.expansions.put(userDefinedIdentifier, expansion);
		renewExpansionProngs(expansion);
		
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
			
		}
		
	}
	
	public void addExpansionFromURL(String userDefinedIdentifier, URL url)
	{
		MAtExpansionFetcher fetcher = new MAtExpansionFetcher(this, userDefinedIdentifier);
		//this.expansions.put(userDefinedIdentifier, null);
		
		fetcher.getDatabase(url);
		
	}
	
	public synchronized void fetcherSuccess(String userDefinedIdentifier, InputStream stream)
	{
		MAtMod.LOGGER.info("ExpansionLoader fetched " + userDefinedIdentifier + ".");
		
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
		
		fetherRescue(userDefinedIdentifier);
		
	}
	
	private void fetherRescue(String userDefinedIdentifier)
	{
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
			
			expansion.turnOff();
			this.expansions.remove(userDefinedIdentifier);
		}
		
	}
	
	public synchronized void addExpansion(String userDefinedIdentifier, InputStream stream)
	{
		if (!this.expansions.containsKey(userDefinedIdentifier))
		{
			MAtMod.LOGGER.severe("Tried to add an expansion that has no entry!");
			return;
			
		}
		
		MAtExpansion expansion = this.expansions.get(userDefinedIdentifier);
		expansion.inputStructure(stream);
		
		tryTurnOn(expansion);
		
	}
	
	private synchronized void tryTurnOn(MAtExpansion expansion)
	{
		if (expansion == null)
			return;
		
		if (!this.canBuildKnowledge)
			return;
		
		// TODO If the expansion is set by the user not to load, don't load it
		// XXX If the expansion is set by the user not to load, don't load it
		
		/*TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
		expansion.buildKnowledge();
		
		MAtMod.LOGGER.info("Expansion "
			+ expansion.getUserDefinedName() + " loaded (" + stat.getSecondsAsString(1) + "s).");
		
		if (!expansion.hasStructure())
		{
			MAtMod.LOGGER.warning("Expansion " + expansion.getUserDefinedName() + " has no structure.");
		}*/
		
		turnOnOrOff(expansion);
		
	}
	
	public synchronized void signalReadyToTurnOn()
	{
		this.canBuildKnowledge = true;
		
		// Try build the knowledge of expansions that were structured before buildknowledge was ready
		for (MAtExpansion expansion : this.expansions.values())
		{
			tryTurnOn(expansion);
		}
		
	}
	
	private void turnOnOrOff(MAtExpansion expansion)
	{
		if (expansion == null)
			return;
		
		if (this.mod.isRunning())
		{
			if (expansion.getVolume() > 0)
			{
				expansion.turnOn();
			}
		}
		else
		{
			expansion.turnOff();
		}
		
	}
	
	public synchronized void modWasTurnedOnOrOff()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			turnOnOrOff(expansion);
			
		}
	}
	
	public Map<String, MAtExpansion> getExpansions()
	{
		return this.expansions;
		
	}
	
	public void soundRoutine()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			expansion.soundRoutine();
			
		}
		
	}
	
	public void dataRoutine()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			expansion.dataRoutine();
			
		}
		
	}
	
	public void clearExpansions()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			expansion.clear();
		}
		this.expansions.clear();
		
	}
	
	public void loadExpansions()
	{
		clearExpansions();
		
		List<File> offline = new ArrayList<File>();
		List<File> online = new ArrayList<File>();
		
		gatherOffline(this.expansionsFolder, offline);
		gatherOnline(this.expansionsFolder, online);
		
		for (File file : offline)
		{
			MAtMod.LOGGER.info("ExpansionLoader found offline " + file.getName() + ".");
			createExpansionEntry(file.getName());
		}
		
		for (File file : online)
		{
			MAtMod.LOGGER.info("ExpansionLoader found online " + file.getName() + ".");
			createExpansionEntry(file.getName());
		}
		
		for (File file : online)
		{
			addOnlineFromFile(file.getName(), file);
		}
		for (File file : offline)
		{
			addExpansionFromFile(file.getName(), file);
		}
		
	}
	
	private void gatherOffline(File file, List<File> files)
	{
		if (!file.exists())
			return;
		
		for (File individual : file.listFiles())
		{
			if (individual.isDirectory())
			{
			}
			else if (individual.getName().endsWith(".xml"))
			{
				files.add(individual);
				
			}
			
		}
		
	}
	
	private void gatherOnline(File file, List<File> files)
	{
		if (!file.exists())
			return;
		
		for (File individual : file.listFiles())
		{
			if (individual.isDirectory())
			{
			}
			else if (individual.getName().endsWith(".xrl"))
			{
				files.add(individual);
				
			}
			
		}
		
	}
	
	private void addOnlineFromFile(String userDefinedIdentifier, File file)
	{
		// TODO Weird exception handling
		try
		{
			BufferedReader buff = new BufferedReader(new FileReader(file));
			try
			{
				addExpansionFromURL(userDefinedIdentifier, new URL(buff.readLine()));
				
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
		
	}
	
	public int getLoadingCount()
	{
		int count = 0;
		for (MAtExpansion e : this.expansions.values())
		{
			if (e == null)
			{
				count = count + 1;
			}
		}
		
		return count;
	}
	
}
