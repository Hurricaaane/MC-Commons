package net.minecraft.src;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
			Minecraft mc = this.mod.manager().getMinecraft();
			restoreSubstitutions(mc.sndManager.soundPoolSounds);
			restoreSubstitutions(mc.sndManager.soundPoolStreaming);
			restoreSubstitutions(mc.sndManager.soundPoolMusic);
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
			Minecraft mc = this.mod.manager().getMinecraft();
			String[] musicDirectories = { "music/", "newmusic/" };
			
			clearSubstitutions();
			
			performSubstitutions(mc.sndManager.soundPoolSounds, "sound3/");
			performSubstitutions(mc.sndManager.soundPoolStreaming, "streaming/");
			performSubstitutions(mc.sndManager.soundPoolMusic, musicDirectories);
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
		Map<String, ArrayList> nameToSoundPoolEntriesMapping =
			(Map<String, ArrayList>) this.mod.util().getPrivateValueLiteral(
				net.minecraft.src.SoundPool.class, soundPool, "b", 1);
		
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
				}
				else if (sound instanceof ATSoundOrphan)
				{
					debug("Uninstalling " + sound.soundName);
					variousSounds.remove(i);
				}
				else
				{
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
		
		Map<String, ArrayList> nameToSoundPoolEntriesMapping =
			(Map<String, ArrayList>) this.mod.util().getPrivateValueLiteral(
				net.minecraft.src.SoundPool.class, soundPool, "b", 1);
		
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
							variousSounds.set(
								i,
								new ATSoundSubstitute(sound, sound.soundName, this.substituantFiles.get(subLocation
									+ sound.soundName)));
							loadedNames.add(subLocation + sound.soundName);
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
								variousSounds.set(
									i,
									new ATSoundSubstitute(sound, oggifiedSoundName, this.substituantFiles
										.get(subLocation + oggifiedSoundName)));
								
								loadedNames.add(subLocation + oggifiedSoundName);
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
						variousSounds.set(i, new ATSoundOrphan(sound));
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
		if (this.debugging)
		{
			System.out.println("(ATSystem) " + contents);
		}
		
	}
	
	public void setDebugging(boolean enabled)
	{
		this.debugging = enabled;
		
	}
}
