package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import eu.ha3.mc.haddon.PrivateAccessException;

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

public class ATSystem
{
	private HaddonImpl mod;
	
	private Map<String, ATConversible> substituantFiles;
	private List<Entry<String, ATConversible>> cumulantsFiles;
	
	private boolean replaceMusWithOggFiles;
	
	private boolean debugging;
	
	private int debuggingLevel;
	
	private Map<SoundPool, List<SoundPoolEntry>> stash;
	
	private boolean doStashForMusic;
	int stashCount;
	
	public ATSystem(HaddonImpl mod)
	{
		this.mod = mod;
		this.substituantFiles = new LinkedHashMap<String, ATConversible>();
		this.cumulantsFiles = new ArrayList<Entry<String, ATConversible>>();
		
		this.replaceMusWithOggFiles = true;
		
		this.stash = new HashMap<SoundPool, List<SoundPoolEntry>>();
		
	}
	
	public Map<String, ATConversible> getSubstituantMap()
	{
		return this.substituantFiles;
	}
	
	public List<Entry<String, ATConversible>> getCumulantsList()
	{
		return this.cumulantsFiles;
	}
	
	public int getStashSize()
	{
		return this.stashCount;
	}
	
	public void applySubstituantLocation(File location, boolean replaceMusWithOggFiles)
	{
		File[] asLocations = { location };
		applySubstituantLocations(asLocations, replaceMusWithOggFiles);
		
	}
	
	public void applySubstituantLocations(File[] locations, boolean replaceMusWithOggFiles)
	{
		this.replaceMusWithOggFiles = replaceMusWithOggFiles;
		
		this.substituantFiles.clear();
		this.cumulantsFiles.clear();
		
		for (File location : locations)
		{
			cacheSubstituants(location.toURI(), location);
		}
		
		performSubstitutions();
		
	}
	
	private void cacheSubstituants(URI originURI, File directory)
	{
		if (directory.isDirectory())
		{
			for (File file : directory.listFiles())
			{
				if (file.isDirectory())
				{
					if (!file.getName().contains("."))
					{
						cacheSubstituants(originURI, file);
					}
					else
					{
						debug(
							"Found directory called " + originURI.relativize(file.toURI()).toString() + ", ignoring.",
							2);
					}
				}
				else
				{
					// Ignore all files that have no extension
					// Ignore all files that begin with a dot
					if (file.getName().contains(".") && !file.getName().startsWith("."))
					{
						ATConversible conv = new ATConversible(file);
						
						this.substituantFiles.put(originURI.relativize(file.toURI()).toString(), conv);
						this.cumulantsFiles.add(new AbstractMap.SimpleEntry<String, ATConversible>(originURI
							.relativize(file.toURI()).toString(), conv));
					}
					else
					{
						debug("Found file called " + originURI.relativize(file.toURI()).toString() + ", ignoring.", 2);
					}
				}
				
			}
		}
		else if (directory.isFile())
		{
			ZipInputStream zis = null;
			try
			{
				URL zipUrl = directory.toURI().toURL();
				
				FileInputStream fis = new FileInputStream(directory);
				zis = new ZipInputStream(fis);
				
				ZipEntry entry = zis.getNextEntry();
				while (entry != null)
				{
					if (!entry.isDirectory())
					{
						String entryName = entry.getName();
						if (!entryName.startsWith(".") && !entryName.contains("/."))
						{
							// Ignore all files that have no extension
							// Ignore all files that begin with a dot
							
							String fileName = entryName.substring(entryName.lastIndexOf("/") + 1);
							
							if (fileName.contains(".") && !fileName.startsWith("."))
							{
								URL fileUrl = new URL(String.format("jar:%s!/%s", zipUrl, entryName));
								ATConversible conv = new ATConversible(fileUrl);
								
								this.substituantFiles.put(entryName, conv);
								this.cumulantsFiles.add(new AbstractMap.SimpleEntry<String, ATConversible>(
									entryName, conv));
							}
							else
							{
								debug("Found file inside zip called " + fileName + ", ignoring.", 2);
							}
						}
					}
					
					entry = zis.getNextEntry();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if (zis != null)
					{
						zis.close();
					}
				}
				catch (Exception e)
				{
				}
			}
			
		}
		
	}
	
	public void clearSubstitutions()
	{
		try
		{
			// soundPoolSounds
			// soundPoolStreaming
			// soundPoolMusic
			// XXX Get rid of private value getting on runtime
			SoundPool soundPoolSounds =
				(SoundPool) this.mod.util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "b", 1);
			SoundPool soundPoolStreaming =
				(SoundPool) this.mod.util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "c", 2);
			SoundPool soundPoolMusic =
				(SoundPool) this.mod.util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "d", 3);
			
			// Unstash before restoring: This prevents issues.
			// Do not check is stashing is enabled before unstashing.
			// This helps if the player disables stashing when it's already stashed.
			
			restoreOriginalFromStash(soundPoolMusic);
			
			restoreSubstitutions(soundPoolSounds);
			restoreSubstitutions(soundPoolStreaming);
			restoreSubstitutions(soundPoolMusic);
			
			if (this.stashCount != 0)
			{
				log("Stash did not reset to zero!");
				this.stashCount = 0;
				
			}
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	private void performSubstitutions()
	{
		log("Performing all substitutions: BEGIN");
		try
		{
			String[] musicDirectories = { "music/", "newmusic/" };
			
			clearSubstitutions();
			
			SoundPool soundPoolSounds =
				(SoundPool) this.mod.util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "b", 1);
			SoundPool soundPoolStreaming =
				(SoundPool) this.mod.util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "c", 2);
			SoundPool soundPoolMusic =
				(SoundPool) this.mod.util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "d", 3);
			
			performSubstitutions(soundPoolSounds, "sound3/");
			performSubstitutions(soundPoolStreaming, "streaming/");
			performCumulations(soundPoolMusic, musicDirectories);
			
			// Stash after performing the substitutions
			if (this.doStashForMusic)
			{
				stashOriginalFromSoundList(soundPoolMusic);
			}
			
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		log("Performing all substitutions: END");
	}
	
	private void stashOriginalFromSoundList(SoundPool soundPool) throws PrivateAccessException
	{
		if (this.stash.get(soundPool) == null)
		{
			this.stash.put(soundPool, new ArrayList<SoundPoolEntry>());
		}
		
		List<SoundPoolEntry> poolStash = this.stash.get(soundPool);
		
		if (!poolStash.isEmpty())
		{
			log("Trying to stash over an unstashed pool! This is an unexpected behavior!");
			poolStash.clear();
		}
		
		List allSoundPoolEntries =
			(List) this.mod.util().getPrivateValueLiteral(net.minecraft.src.SoundPool.class, soundPool, "e", 2);
		
		for (Object entry : allSoundPoolEntries)
		{
			if (!(entry instanceof ATSoundWrapper))
			{
				poolStash.add((SoundPoolEntry) entry);
			}
		}
		
		if (poolStash.size() == allSoundPoolEntries.size())
		{
			log("Did not stash: No custom entries found.");
			poolStash.clear();
			return;
		}
		
		for (SoundPoolEntry entry : poolStash)
		{
			allSoundPoolEntries.remove(entry);
		}
		
		log("Stashed " + poolStash.size() + " original entries.");
		this.stashCount = this.stashCount + poolStash.size();
		
	}
	
	private void restoreOriginalFromStash(SoundPool soundPool) throws PrivateAccessException
	{
		if (this.stash.get(soundPool) == null)
			return;
		
		List<SoundPoolEntry> poolStash = this.stash.get(soundPool);
		
		List allSoundPoolEntries =
			(List) this.mod.util().getPrivateValueLiteral(net.minecraft.src.SoundPool.class, soundPool, "e", 2);
		
		for (SoundPoolEntry entry : poolStash)
		{
			allSoundPoolEntries.add(entry);
		}
		
		log("Restored " + poolStash.size() + " entries from stash.");
		
		this.stashCount = this.stashCount - poolStash.size();
		poolStash.clear();
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void restoreSubstitutions(SoundPool soundPool) throws PrivateAccessException
	{
		// nameToSoundPoolEntriesMapping
		Map<String, ArrayList> nameToSoundPoolEntriesMapping =
			(Map<String, ArrayList>) this.mod.util().getPrivateValueLiteral(
				net.minecraft.src.SoundPool.class, soundPool, "d", 1);
		
		// allSoundPoolEntries
		List allSoundPoolEntries =
			(List) this.mod.util().getPrivateValueLiteral(net.minecraft.src.SoundPool.class, soundPool, "e", 2);
		
		for (Entry<String, ArrayList> entry : nameToSoundPoolEntriesMapping.entrySet())
		{
			ArrayList variousSounds = entry.getValue();
			
			int i = 0;
			while (i < variousSounds.size())
			{
				SoundPoolEntry sound = (SoundPoolEntry) variousSounds.get(i);
				if (sound instanceof ATSoundSubstitute)
				{
					debug("Unsubstituing " + sound.soundName);
					variousSounds.set(i, ((ATSoundSubstitute) sound).getOriginal());
					i++;
					
					int indexInAllList = allSoundPoolEntries.indexOf(sound);
					if (indexInAllList != -1)
					{
						allSoundPoolEntries.set(indexInAllList, ((ATSoundSubstitute) sound).getOriginal());
					}
					else
					{
						log(sound.soundName
							+ " was present in sound mapping but not in the sound list (unsubstitution)!");
					}
				}
				else if (sound instanceof ATSoundOrphan)
				{
					debug("Uninstalling " + sound.soundName);
					variousSounds.remove(i);
					
					int indexInAllList = allSoundPoolEntries.indexOf(sound);
					if (indexInAllList != -1)
					{
						allSoundPoolEntries.remove(indexInAllList);
					}
					else
					{
						log(sound.soundName
							+ " was present in sound mapping but not in the sound list (uninstallation)!");
					}
				}
				else if (sound instanceof ATSoundCumulation)
				{
					debug("Uncumulating " + sound.soundName);
					variousSounds.remove(i);
					
					int indexInAllList = allSoundPoolEntries.indexOf(sound);
					if (indexInAllList != -1)
					{
						allSoundPoolEntries.remove(indexInAllList);
					}
					else
					{
						log(sound.soundName + " was present in sound mapping but not in the sound list (uncumulation)!");
					}
				}
				else
				{
					debug("Passing " + sound.soundName, 2);
					i++;
				}
				
			}
			
		}
		
	}
	
	private void performSubstitutions(SoundPool soundPool, String subLocation) throws PrivateAccessException
	{
		String[] subLocations = { subLocation };
		performSubstitutions(soundPool, subLocations);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void performSubstitutions(SoundPool soundPool, String[] subLocations) throws PrivateAccessException
	{
		Set<String> allNames = this.substituantFiles.keySet();
		Set<String> notLoadedNames = new LinkedHashSet<String>(this.substituantFiles.keySet());
		Set<String> loadedNames = new HashSet<String>();
		
		// nameToSoundPoolEntriesMapping
		Map<String, ArrayList> nameToSoundPoolEntriesMapping =
			(Map<String, ArrayList>) this.mod.util().getPrivateValueLiteral(
				net.minecraft.src.SoundPool.class, soundPool, "d", 1);
		
		// allSoundPoolEntries
		List allSoundPoolEntries =
			(List) this.mod.util().getPrivateValueLiteral(net.minecraft.src.SoundPool.class, soundPool, "e", 2);
		
		for (Entry<String, ArrayList> entry : nameToSoundPoolEntriesMapping.entrySet())
		{
			//String cuteNameWithDots = entry.getKey();
			ArrayList variousSounds = entry.getValue();
			
			for (int i = 0; i < variousSounds.size(); i++)
			{
				SoundPoolEntry sound = (SoundPoolEntry) variousSounds.get(i);
				
				boolean hasFoundSubstitute = false;
				boolean hasFoundSubstituteMus = false;
				for (String subLocation : subLocations)
				{
					if (allNames.contains(subLocation + sound.soundName))
					{
						if (!hasFoundSubstitute)
						{
							hasFoundSubstitute = true;
							
							debug(sound.soundName + " has a substitute in " + subLocation + "!");
							
							ATSoundSubstitute substitute =
								new ATSoundSubstitute(sound, sound.soundName, this.substituantFiles.get(subLocation
									+ sound.soundName));
							
							variousSounds.set(i, substitute);
							loadedNames.add(subLocation + sound.soundName);
							
							int indexInAllList = allSoundPoolEntries.indexOf(sound);
							if (indexInAllList != -1)
							{
								allSoundPoolEntries.set(indexInAllList, substitute);
							}
							else
							{
								log(sound.soundName
									+ " is present in sound mapping but not in the sound list (substitution)!");
							}
							
						}
						else
						{
							debug(sound.soundName
								+ " has a substitute in " + subLocation
								+ ", but we already performed a substitution earlier from another sublocation!");
							
						}
					}
					
					// The reason why this is not an elseif is because
					// if we find, say 13.ogg and 13.mus in the same folder,
					// we want to have BOTH overridden by the same file.
					
					if (this.replaceMusWithOggFiles && sound.soundName.toLowerCase().endsWith(".mus"))
					{
						String oggifiedSoundName = sound.soundName.substring(0, sound.soundName.length() - 4) + ".ogg";
						
						if (allNames.contains(subLocation + oggifiedSoundName))
						{
							if (!hasFoundSubstituteMus)
							{
								hasFoundSubstituteMus = true;
								
								debug(sound.soundName + " has a substitute in " + subLocation + "!");
								
								ATSoundSubstitute substitute =
									new ATSoundSubstitute(
										sound, oggifiedSoundName, this.substituantFiles.get(subLocation
											+ oggifiedSoundName));
								
								variousSounds.set(i, substitute);
								
								loadedNames.add(subLocation + oggifiedSoundName);
								
								int indexInAllList = allSoundPoolEntries.indexOf(sound);
								if (indexInAllList != -1)
								{
									allSoundPoolEntries.set(indexInAllList, substitute);
								}
								else
								{
									log(sound.soundName
										+ " is present in sound mapping but not in the sound list (substitution)!");
								}
							}
							else
							{
								debug(sound.soundName
									+ " has a substitute in " + subLocation
									+ ", but we already performed a substitution earlier from another sublocation!");
								
							}
						}
					}
				}
			}
			
		}
		
		for (String name : loadedNames)
		{
			notLoadedNames.remove(name);
		}
		
		// Install orphan sounds
		int orphanCount = 0;
		for (String name : notLoadedNames)
		{
			for (String subLocation : subLocations)
			{
				if (name.startsWith(subLocation))
				{
					if (!soundPool.isGetRandomSound || name.contains(".") && !isMadeOfDigits(soundNamePart(name)))
					{
						debug("Installing orphan resource " + name);
						installResource(name, this.substituantFiles.get(name));
						orphanCount++;
					}
					else
					{
						log("Did not install resource " + name + " because the name is invalid in this context!");
					}
				}
			}
			
		}
		
		// Wrap orphan sounds
		for (Entry<String, ArrayList> entry : nameToSoundPoolEntriesMapping.entrySet())
		{
			//String cuteNameWithDots = entry.getKey();
			ArrayList variousSounds = entry.getValue();
			
			for (int i = 0; i < variousSounds.size(); i++)
			{
				SoundPoolEntry sound = (SoundPoolEntry) variousSounds.get(i);
				for (String subLocation : subLocations)
				{
					if (notLoadedNames.contains(subLocation + sound.soundName))
					{
						ATSoundOrphan orphan = new ATSoundOrphan(sound);
						
						variousSounds.set(i, orphan);
						
						int indexInAllList = allSoundPoolEntries.indexOf(sound);
						if (indexInAllList != -1)
						{
							allSoundPoolEntries.set(indexInAllList, orphan);
						}
						else
						{
							log(sound.soundName
								+ " is present in sound mapping but not in the sound list (installation)!");
						}
					}
				}
			}
			
		}
		
		log("Performed " + loadedNames.size() + " substitutions (" + subLocations[0] + ").");
		log("Performed " + orphanCount + " installations (" + subLocations[0] + ").");
		
	}
	
	private void installResource(String name, ATConversible conv)
	{
		if (conv.isFile())
		{
			this.mod.getManager().getMinecraft().installResource(name, conv.getFile());
		}
		else
		{
			try
			{
				int quant = name.indexOf("/");
				String pool = name.substring(0, quant);
				name = name.substring(quant + 1);
				
				if (pool.equalsIgnoreCase("sound3"))
				{
					SoundPool soundPoolSounds =
						(SoundPool) this.mod.util().getPrivateValueLiteral(
							net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "b", 1);
					soundPoolSounds.addSound(name, conv.getURL());
				}
				else if (pool.equalsIgnoreCase("streaming"))
				{
					SoundPool soundPoolStreaming =
						(SoundPool) this.mod.util().getPrivateValueLiteral(
							net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "c", 2);
					soundPoolStreaming.addSound(name, conv.getURL());
				}
				else if (pool.equalsIgnoreCase("music") || pool.equalsIgnoreCase("newmusic"))
				{
					SoundPool soundPoolMusic =
						(SoundPool) this.mod.util().getPrivateValueLiteral(
							net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "d", 3);
					soundPoolMusic.addSound(name, conv.getURL());
				}
			}
			catch (PrivateAccessException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void performCumulations(SoundPool soundPool, String[] subLocations) throws PrivateAccessException
	{
		List<Entry<String, ATConversible>> pairsToLoad = new ArrayList<Entry<String, ATConversible>>();
		List<URL> cumulatedURLs = new ArrayList<URL>();
		
		// nameToSoundPoolEntriesMapping
		Map<String, ArrayList> nameToSoundPoolEntriesMapping =
			(Map<String, ArrayList>) this.mod.util().getPrivateValueLiteral(
				net.minecraft.src.SoundPool.class, soundPool, "d", 1);
		
		// allSoundPoolEntries
		List allSoundPoolEntries =
			(List) this.mod.util().getPrivateValueLiteral(net.minecraft.src.SoundPool.class, soundPool, "e", 2);
		
		for (Entry<String, ATConversible> entry : this.cumulantsFiles)
		{
			for (String subLocation : subLocations)
			{
				if (entry.getKey().startsWith(subLocation))
				{
					pairsToLoad.add(entry);
				}
			}
			
		}
		
		// Install cumulated sounds
		int orphanCount = 0;
		for (Entry<String, ATConversible> pair : pairsToLoad)
		{
			String name = pair.getKey();
			
			if (!soundPool.isGetRandomSound || name.contains(".") && !isMadeOfDigits(soundNamePart(name)))
			{
				debug("Installing cumulative resource " + name);
				
				installResource(name, pair.getValue());
				orphanCount++;
				cumulatedURLs.add(pair.getValue().asURL());
			}
			else
			{
				log("Did not install resource " + name + " because the name is invalid in this context!");
			}
			
		}
		
		// Wrap cumulated sounds
		for (Entry<String, ArrayList> entry : nameToSoundPoolEntriesMapping.entrySet())
		{
			//String cuteNameWithDots = entry.getKey();
			ArrayList variousSounds = entry.getValue();
			
			for (int i = 0; i < variousSounds.size(); i++)
			{
				SoundPoolEntry sound = (SoundPoolEntry) variousSounds.get(i);
				if (cumulatedURLs.contains(sound.soundUrl))
				{
					ATSoundCumulation cumulation = new ATSoundCumulation(sound);
					
					variousSounds.set(i, cumulation);
					
					int indexInAllList = allSoundPoolEntries.indexOf(sound);
					if (indexInAllList != -1)
					{
						allSoundPoolEntries.set(indexInAllList, cumulation);
					}
					else
					{
						log(sound.soundName + " is present in sound mapping but not in the sound list (cumulation)!");
					}
				}
			}
			
		}
		
		log("Performed " + orphanCount + " cumulations (" + subLocations[0] + ").");
		
	}
	
	private String soundNamePart(String name)
	{
		name = name.substring(0, name.indexOf("."));
		name = name.substring(name.indexOf("/") + 1);
		
		return name;
	}
	
	private boolean isMadeOfDigits(String name)
	{
		for (int i = 0; i < name.length(); i++)
		{
			if (!Character.isDigit(name.charAt(i)))
				return false;
		}
		
		return true;
	}
	
	public void log(String contents)
	{
		System.out.println("(ATSystem) " + contents);
		
	}
	
	public void debug(String contents)
	{
		debug(contents, 1);
	}
	
	public void debug(String contents, int level)
	{
		if (this.debugging && this.debuggingLevel >= level)
		{
			System.out.println("(ATSystem) " + contents);
		}
	}
	
	public void setDebugging(boolean enabled)
	{
		this.debugging = enabled;
		
	}
	
	public void setDebuggingLevel(int level)
	{
		this.debuggingLevel = level;
		
	}
	
	public void setStashForMusic(boolean enabled)
	{
		this.doStashForMusic = enabled;
		
	}
}
