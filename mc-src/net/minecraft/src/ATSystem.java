package net.minecraft.src;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.client.Minecraft;
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
	
	private Map<String, File> substituantFiles;
	
	private boolean replaceMusWithOggFiles;
	
	private boolean debugging;
	
	private int debuggingLevel;
	
	public ATSystem(HaddonImpl mod)
	{
		this.mod = mod;
		this.substituantFiles = new LinkedHashMap<String, File>();
		
		this.replaceMusWithOggFiles = true;
		
	}
	
	public Map<String, File> getSubstituantMap()
	{
		return this.substituantFiles;
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
		
		for (File location : locations)
		{
			cacheSubstituants(location.toURI(), location);
		}
		
		performSubstitutions();
		
	}
	
	private void cacheSubstituants(URI originURI, File directory)
	{
		for (File file : directory.listFiles())
		{
			if (file.isDirectory())
			{
				cacheSubstituants(originURI, file);
			}
			else
			{
				this.substituantFiles.put(originURI.relativize(file.toURI()).toString(), file);
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
			
			restoreSubstitutions(soundPoolSounds);
			restoreSubstitutions(soundPoolStreaming);
			restoreSubstitutions(soundPoolMusic);
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
			performSubstitutions(soundPoolMusic, musicDirectories);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		log("Performing all substitutions: END");
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
		Minecraft mc = this.mod.manager().getMinecraft();
		for (String name : notLoadedNames)
		{
			for (String subLocation : subLocations)
			{
				if (name.startsWith(subLocation))
				{
					debug("Installing orphan resource " + name);
					mc.installResource(name, this.substituantFiles.get(name));
					orphanCount++;
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
}
