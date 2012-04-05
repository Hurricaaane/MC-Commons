package net.minecraft.src;

import eu.ha3.matmos.engine.MAtmosSoundManager;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

/**
 * 
 * Wrapped sound manager for output security control
 * 
 * @author Hurry
 * 
 */
public class MAtWrappedExpansionSoundManager implements MAtmosSoundManager
{
	private MAtExpansion expansion;
	private MAtmosSoundManager soundManager;
	
	MAtWrappedExpansionSoundManager(MAtExpansion expansion,
			MAtmosSoundManager soundManager)
			{
		this.expansion = expansion;
		this.soundManager = soundManager;
		
			}
	
	@Override
	public void routine()
	{
		soundManager.routine();
	}
	
	@Override
	public void cacheSound(String path)
	{
		soundManager.cacheSound(path);
	}
	
	@Override
	public void playSound(String path, float volume, float pitch, int meta)
	{
		soundManager.playSound(path, volume * expansion.getVolume(), pitch,
				meta);
		
	}
	
	@Override
	public int getNewStreamingToken()
	{
		return soundManager.getNewStreamingToken();
	}
	
	@Override
	public boolean setupStreamingToken(int token, String path, float volume,
			float pitch)
	{
		return soundManager.setupStreamingToken(token, path, volume, pitch);
		
	}
	
	@Override
	public void startStreaming(int token, float fadeDuration, int timesToPlay)
	{
		soundManager.startStreaming(token, fadeDuration, timesToPlay);
		
	}
	
	@Override
	public void stopStreaming(int token, float fadeDuration)
	{
		soundManager.stopStreaming(token, fadeDuration);
		
	}
	
	@Override
	public void pauseStreaming(int token, float fadeDuration)
	{
		soundManager.pauseStreaming(token, fadeDuration);
		
	}
	
	@Override
	public void eraseStreamingToken(int token)
	{
		soundManager.eraseStreamingToken(token);
		
	}
	
}
