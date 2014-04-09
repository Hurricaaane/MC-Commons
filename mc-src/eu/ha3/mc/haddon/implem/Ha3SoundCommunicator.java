package eu.ha3.mc.haddon.implem;

import net.minecraft.src.Minecraft;
import net.minecraft.src.SoundManager;
import net.minecraft.src.SoundPoolEntry;
import net.minecraft.src.SoundPool;
import paulscode.sound.SoundSystem;
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
	
	public SoundSystem getSoundSystem()
	{
		try
		{
			return (SoundSystem) this.mod
				.getUtility()
				.getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, Minecraft.getMinecraft().sndManager, "b", 1);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public SoundManager getSoundManager()
	{
		return Minecraft.getMinecraft().sndManager;
	}
	
	public void playSoundViaManager(String sound, float x, float y, float z, float vol, float pitch)
	{
		getSoundManager().playSound(sound, x, y, z, vol, pitch);
	}
	
	public void playSound(String sound, float x, float y, float z, float vol, float pitch)
	{
		try
		{
			float soundVolume = Minecraft.getMinecraft().gameSettings.soundVolume;
			
			if (soundVolume == 0.0F)
				return;
			
			// soundPoolSounds
			// XXX Get rid of private value getting on runtime
			SoundPoolEntry soundpoolentry =
				((SoundPool) this.mod
					.getUtility()
					.getPrivateValueLiteral(net.minecraft.src.SoundManager.class, getSoundManager(), "d", 3))
					.getRandomSoundFromSoundPool(sound);
			if (soundpoolentry != null && vol > 0.0F)
			{
				SoundSystem sndSystem = getSoundSystem();
				
				this.lastSoundID = (this.lastSoundID + 1) % this.maxIDs;
				String sourceName = this.prefix + this.lastSoundID;
				float rollf = 16F;
				if (vol > 1.0F)
				{
					rollf *= vol;
				}
				sndSystem.newSource(
					vol > 1.0F, sourceName, soundpoolentry.getSoundUrl() /* soundURL */,
					soundpoolentry.getSoundName() /* soundName */, false, x, y, z, 2, rollf);
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
			float soundVolume = Minecraft.getMinecraft().gameSettings.soundVolume;
			
			if (soundVolume == 0.0F)
				return;
			
			// soundPoolSounds
			// XXX Get rid of private value getting on runtime
			SoundPoolEntry soundpoolentry =
				((SoundPool) this.mod
					.getUtility()
					.getPrivateValueLiteral(net.minecraft.src.SoundManager.class, sndManager, "d", 3))
					.getRandomSoundFromSoundPool(sound);
			
			if (soundpoolentry != null && vol > 0.0F)
			{
				this.lastSoundID = (this.lastSoundID + 1) % this.maxIDs;
				String sourceName = this.prefix + this.lastSoundID;
				
				sndSystem.newSource(
					vol > 1.0F, sourceName, soundpoolentry.getSoundUrl(), soundpoolentry.getSoundName(), false, x,
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
			e.printStackTrace();
		}
	}
	
}
