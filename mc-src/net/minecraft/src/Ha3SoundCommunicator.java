package net.minecraft.src;

import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;
import eu.ha3.mc.haddon.Haddon;

@Deprecated
public class Ha3SoundCommunicator
{
	public Ha3SoundCommunicator(Haddon haddon, String prefix)
	{
		System.err
			.println("Ha3SoundCommunicator is deprecated: Calling any of its methods will result in a forced crash.");
	}
	
	private void error()
	{
		throw new RuntimeException(
			"Ha3SoundCommunicator is deprecated: Calling any of its methods results in a forced crash.");
	}
	
	@Deprecated
	public void setMaxIDs(int max)
	{
		error();
	}
	
	@Deprecated
	public SoundSystem getSoundSystem()
	{
		error();
		return null;
	}
	
	@Deprecated
	public SoundManager getSoundManager()
	{
		error();
		return null;
	}
	
	/**
	 * Plays a sound by passing it to actual sound manager method.
	 * 
	 * @param sound
	 * @param x
	 * @param y
	 * @param z
	 * @param vol
	 * @param pitch
	 */
	@Deprecated
	public void playSoundViaManager(String sound, float x, float y, float z, float vol, float pitch)
	{
		error();
	}
	
	/**
	 * Play a sound according to this sound communicator rules.
	 * 
	 * @param sound
	 * @param x
	 * @param y
	 * @param z
	 * @param vol
	 * @param pitch
	 */
	@Deprecated
	public void playSound(String sound, float x, float y, float z, float vol, float pitch)
	{
		error();
	}
	
	/**
	 * Play a sound according to this sound communicator rules, with a custom
	 * roll factor.
	 * 
	 * @param sound
	 * @param x
	 * @param y
	 * @param z
	 * @param vol
	 * @param pitch
	 * @param attnm
	 * @param rollf
	 */
	@Deprecated
	public void playSound(String sound, float x, float y, float z, float vol, float pitch, int attnm, float rollf)
	{
		error();
	}
	
}
