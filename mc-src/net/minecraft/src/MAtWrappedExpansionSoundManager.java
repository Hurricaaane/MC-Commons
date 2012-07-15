package net.minecraft.src;

import eu.ha3.matmos.engine.MAtmosSoundManager;

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
