package net.minecraft.src;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3Signal;
import eu.ha3.mc.haddon.PrivateAccessException;
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

public class ATHaddon extends HaddonImpl implements SupportsTickEvents
{
	private Ha3SoundCommunicator sndComms;
	
	private File audiotoriLocation;
	private Map<String, File> substituantFiles;
	
	@Override
	public void onLoad()
	{
		this.substituantFiles = new LinkedHashMap<String, File>();
		applySubstituantLocation(new File(Minecraft.getMinecraftDir(), "audiotori/substitute/"));
		
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
	}
	
	public void applySubstituantLocation(File location)
	{
		this.audiotoriLocation = location;
		this.substituantFiles.clear();
		
		cacheSubstituants(location);
		
	}
	
	private void cacheSubstituants(File directory)
	{
		URI audiotoriURI = this.audiotoriLocation.toURI();
		
		for (File file : directory.listFiles())
		{
			if (file.isDirectory())
			{
				cacheSubstituants(file);
			}
			else
			{
				this.substituantFiles.put(audiotoriURI.relativize(file.toURI()).toString(), file);
			}
			
		}
		
	}
	
	public void log(String contents)
	{
		System.out.println("(Audiotori) " + contents);
		
	}
	
	private void continueLoading()
	{
		manager().hookTickEvents(true);
	}
	
	private void performSubstitutions()
	{
		try
		{
			Minecraft mc = manager().getMinecraft();
			String[] musicDirectories = { "music/", "newmusic/" };
			
			restoreSubstitutions(mc.sndManager.soundPoolSounds);
			restoreSubstitutions(mc.sndManager.soundPoolStreaming);
			restoreSubstitutions(mc.sndManager.soundPoolMusic);
			
			performSubstitutions(mc.sndManager.soundPoolSounds, "sound3/");
			performSubstitutions(mc.sndManager.soundPoolStreaming, "streaming/");
			performSubstitutions(mc.sndManager.soundPoolMusic, musicDirectories);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void restoreSubstitutions(SoundPool soundPool) throws PrivateAccessException
	{
		Map<String, ArrayList> nameToSoundPoolEntriesMapping =
			(Map<String, ArrayList>) util()
				.getPrivateValueLiteral(net.minecraft.src.SoundPool.class, soundPool, "b", 1);
		
		for (Entry<String, ArrayList> entry : nameToSoundPoolEntriesMapping.entrySet())
		{
			ArrayList variousSounds = entry.getValue();
			
			for (int i = 0; i < variousSounds.size(); i++)
			{
				SoundPoolEntry sound = (SoundPoolEntry) variousSounds.get(i);
				if (sound instanceof ATSoundSubstitute)
				{
					variousSounds.set(i, ((ATSoundSubstitute) sound).getOriginal());
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
		Set<String> notLoadedNames = new HashSet<String>(this.substituantFiles.keySet());
		Set<String> loadedNames = new HashSet<String>();
		
		Map<String, ArrayList> nameToSoundPoolEntriesMapping =
			(Map<String, ArrayList>) util()
				.getPrivateValueLiteral(net.minecraft.src.SoundPool.class, soundPool, "b", 1);
		
		for (Entry<String, ArrayList> entry : nameToSoundPoolEntriesMapping.entrySet())
		{
			//String cuteNameWithDots = entry.getKey();
			ArrayList variousSounds = entry.getValue();
			
			for (int i = 0; i < variousSounds.size(); i++)
			{
				SoundPoolEntry sound = (SoundPoolEntry) variousSounds.get(i);
				
				boolean hasFoundSubstitute = false;
				for (String subLocation : subLocations)
				{
					if (allNames.contains(subLocation + sound.soundName))
					{
						if (!hasFoundSubstitute)
						{
							hasFoundSubstitute = true;
							
							System.out.println(sound.soundName + " has a substitute in " + subLocation + "!");
							variousSounds.set(i, new ATSoundSubstitute(sound, new File(
								this.audiotoriLocation, subLocation)));
							loadedNames.add(sound.soundName);
						}
						else
						{
							System.out.println(sound.soundName
								+ " has a substitute in " + subLocation
								+ ", but we already performed a substitution earlier!");
							
						}
					}
				}
			}
			
		}
		
		for (String name : loadedNames)
		{
			notLoadedNames.remove(name);
		}
		
		Minecraft mc = manager().getMinecraft();
		for (String name : notLoadedNames)
		{
			System.out.println("Installing orphan resource " + name);
			mc.installResource(name, this.substituantFiles.get(name));
		}
		
	}
	
	@Override
	public void onTick()
	{
		performSubstitutions();
		manager().hookTickEvents(false);
		
	}
	
}
