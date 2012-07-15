package net.minecraft.src;

import paulscode.sound.SoundSystem;
import eu.ha3.mc.convenience.Ha3Signal;
import eu.ha3.mc.haddon.Haddon;
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

public class Ha3SoundCommunicator
{
	private Haddon mod;
	
	private SoundManager sndManager;
	private SoundSystem sndSystem;
	
	private Ha3SoundThread soundThread;
	
	private String prefix;
	private int maxIDs;
	
	private int lastSoundID;
	
	Ha3SoundCommunicator(Haddon haddon, String prefix)
	{
		this.mod = haddon;
		this.prefix = prefix;
		maxIDs = 256;
		
	}
	
	public void setMaxIDs(int max)
	{
		if (max <= 0)
			throw new IllegalArgumentException();
		
		maxIDs = max;
		
	}
	
	
	public boolean isUseable()
	{
		return (sndSystem != null) && (sndManager != null);
		
	}
	
	/**
	 * Calling this creates a thread if the sound communicator is not yet ready.
	 * 
	 * @param onSuccess
	 * @param onFailure
	 */
	public void load(Ha3Signal onSuccess, Ha3Signal onFailure)
	{
		if (!isUseable())
		{
			soundThread = new Ha3SoundThread(this, onSuccess,
					onFailure);
			soundThread.start();
			
		}
		
	}
	
	public boolean loadSoundManager()
	{
		if (sndManager == null)
			sndManager = mod.getManager().getMinecraft().sndManager;
		
		return (sndManager != null);
		
	}
	
	public SoundSystem getSoundSystem()
	{
		return sndSystem;
		
	}
	
	public SoundManager getSoundManager()
	{
		return sndManager;
		
	}
	
	public boolean loadSoundSystem() throws PrivateAccessException
	{
		if (sndSystem == null)
			sndSystem = (SoundSystem) mod.getManager().getUtility()
			.getPrivateValue(
					net.minecraft.src.SoundManager.class,
							mod.getManager().getMinecraft().sndManager, "a", 0);
		
		return (sndSystem != null);
		
	}
	
	public void playSoundViaManager(String sound, float x, float y, float z,
			float vol, float pitch)
	{
		if (!isUseable())
			return;
		
		sndManager.playSound(sound, x, y, z, vol, pitch);
		
	}
	
	public void playSound(String sound, float x, float y, float z, float vol,
			float pitch)
	{
		if (!isUseable())
			return;
		
		try
		{
			// Private value is: GameSettings options;
			// XXX Get rid of private value getting on runtime
			
			float soundVolume = ((GameSettings) mod.getManager().getUtility()
					.getPrivateValue(
							net.minecraft.src.SoundManager.class, sndManager, 5)).soundVolume;
			
			if (soundVolume == 0.0F)
			{
				return;
			}
			
			// Private value is: SoundPool soundPoolSounds;
			// XXX Get rid of private value getting on runtime
			SoundPoolEntry soundpoolentry = ((SoundPool) mod.getManager()
					.getUtility()
					.getPrivateValue(net.minecraft.src.SoundManager.class,
							sndManager, 1)).getRandomSoundFromSoundPool(sound);
			if (soundpoolentry != null && vol > 0.0F)
			{
				lastSoundID = (lastSoundID + 1) % maxIDs;
				String sourceName = prefix + lastSoundID;
				float rollf = 16F;
				if (vol > 1.0F)
				{
					rollf *= vol;
				}
				sndSystem.newSource(vol > 1.0F, sourceName,
						soundpoolentry.soundUrl, soundpoolentry.soundName,
						false, x, y, z, 2, rollf);
				sndSystem.setPitch(sourceName, pitch);
				if (vol > 1.0F)
				{
					vol = 1.0F;
				}
				sndSystem.setVolume(sourceName, vol * soundVolume);
				sndSystem.play(sourceName);
			}
			
		}
		catch (PrivateAccessException e)
		{
			; // XXX Hidden exception
			
		}
		
	}
	
	public void playSound(String sound, float x, float y, float z, float vol,
			float pitch, int attnm, float rollf)
	{
		if (!isUseable())
			return;
		
		try
		{
			// Private value is: GameSettings options;
			// XXX Get rid of private value getting on runtime
			
			//float soundVolume = ((GameSettings) mod.manager().getPrivateValue(
			//		net.minecraft.src.SoundManager.class, sndManager, 5)).soundVolume;
			
			float soundVolume = mod.getManager().getMinecraft().gameSettings.soundVolume;
			
			if (soundVolume == 0.0F)
			{
				return;
			}
			
			// Private value is: SoundPool soundPoolSounds;
			// XXX Get rid of private value getting on runtime
			SoundPoolEntry soundpoolentry = ((SoundPool) mod.getManager()
					.getUtility()
					.getPrivateValue(net.minecraft.src.SoundManager.class,
							sndManager, 1)).getRandomSoundFromSoundPool(sound);
			if (soundpoolentry != null && vol > 0.0F)
			{
				lastSoundID = (lastSoundID + 1) % maxIDs;
				String sourceName = prefix + lastSoundID;
				
				sndSystem.newSource(vol > 1.0F, sourceName,
						soundpoolentry.soundUrl, soundpoolentry.soundName,
						false, x, y, z, attnm, rollf);
				sndSystem.setPitch(sourceName, pitch);
				
				if (vol > 1.0F)
				{
					vol = 1.0F;
				}
				sndSystem.setVolume(sourceName, vol * soundVolume);
				sndSystem.play(sourceName);
			}
			
		}
		catch (PrivateAccessException e)
		{
			; // XXX Hidden exception
			
		}
		
	}
	
}
