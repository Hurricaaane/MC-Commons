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
	
	private String prefix;
	private int maxIDs;
	
	private int lastSoundID;
	
	public Ha3SoundCommunicator(Haddon haddon, String prefix)
	{
		this.mod = haddon;
		this.prefix = prefix;
		this.maxIDs = 256;
		
	}
	
	public void setMaxIDs(int max)
	{
		if (max <= 0)
			throw new IllegalArgumentException();
		
		this.maxIDs = max;
		
	}
	
	@Deprecated
	public boolean isUseable()
	{
		return true;
	}
	
	@Deprecated
	public void load(Ha3Signal onSuccess, Ha3Signal onFailure)
	{
		onSuccess.signal();
		
	}
	
	public SoundSystem getSoundSystem()
	{
		try
		{
			return (SoundSystem) this.mod
				.getManager()
				.getUtility()
				.getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, this.mod.getManager().getMinecraft().sndManager, "b", 1);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public SoundManager getSoundManager()
	{
		return this.mod.getManager().getMinecraft().sndManager;
		
	}
	
	public void playSoundViaManager(String sound, float x, float y, float z, float vol, float pitch)
	{
		if (!isUseable())
			return;
		
		getSoundManager().playSound(sound, x, y, z, vol, pitch);
		
	}
	
	public void playSound(String sound, float x, float y, float z, float vol, float pitch)
	{
		if (!isUseable())
			return;
		
		SoundManager sndManager = getSoundManager();
		SoundSystem sndSystem = getSoundSystem();
		
		try
		{
			float soundVolume = this.mod.getManager().getMinecraft().gameSettings.soundVolume;
			
			if (soundVolume == 0.0F)
				return;
			
			// soundPoolSounds
			// XXX Get rid of private value getting on runtime
			SoundPoolEntry soundpoolentry =
				((SoundPool) this.mod
					.getManager().getUtility()
					.getPrivateValueLiteral(net.minecraft.src.SoundManager.class, sndManager, "d", 3))
					.getRandomSoundFromSoundPool(sound);
			if (soundpoolentry != null && vol > 0.0F)
			{
				this.lastSoundID = (this.lastSoundID + 1) % this.maxIDs;
				String sourceName = this.prefix + this.lastSoundID;
				float rollf = 16F;
				if (vol > 1.0F)
				{
					rollf *= vol;
				}
				sndSystem.newSource(
					vol > 1.0F, sourceName, soundpoolentry.func_110457_b() /* soundURL */,
					soundpoolentry.func_110458_a() /* soundName */, false, x, y, z, 2, rollf);
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
	
	public void playSound(String sound, float x, float y, float z, float vol, float pitch, int attnm, float rollf)
	{
		SoundManager sndManager = getSoundManager();
		SoundSystem sndSystem = getSoundSystem();
		
		try
		{
			float soundVolume = this.mod.getManager().getMinecraft().gameSettings.soundVolume;
			
			if (soundVolume == 0.0F)
				return;
			
			// soundPoolSounds
			// XXX Get rid of private value getting on runtime
			SoundPoolEntry soundpoolentry =
				((SoundPool) this.mod
					.getManager().getUtility()
					.getPrivateValueLiteral(net.minecraft.src.SoundManager.class, sndManager, "d", 3))
					.getRandomSoundFromSoundPool(sound);
			
			if (soundpoolentry != null && vol > 0.0F)
			{
				this.lastSoundID = (this.lastSoundID + 1) % this.maxIDs;
				String sourceName = this.prefix + this.lastSoundID;
				
				sndSystem.newSource(
					vol > 1.0F, sourceName, soundpoolentry.func_110457_b(), soundpoolentry.func_110458_a(), false, x,
					y, z, attnm, rollf);
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
