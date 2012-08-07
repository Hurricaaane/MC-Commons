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

public class MAtSoundManagerProxy implements MAtmosSoundManager, MAtCustomVolume
{
	private MAtmosSoundManager wrapped;
	private float volume;
	
	public MAtSoundManagerProxy(MAtmosSoundManager wrapped)
	{
		this.wrapped = wrapped;
		this.volume = 1f;
		
	}
	
	@Override
	public void routine()
	{
	}
	
	@Override
	public void cacheSound(String path)
	{
		this.wrapped.cacheSound(path);
	}
	
	@Override
	public void playSound(String path, float volume, float pitch, int meta)
	{
		this.wrapped.playSound(path, volume * this.volume, pitch, meta);
		
	}
	
	@Override
	public int getNewStreamingToken()
	{
		return this.wrapped.getNewStreamingToken();
	}
	
	@Override
	public boolean setupStreamingToken(int token, String path, float volume, float pitch)
	{
		return this.wrapped.setupStreamingToken(token, path, volume, pitch);
	}
	
	@Override
	public void startStreaming(int token, float fadeDuration, int timesToPlay)
	{
		this.wrapped.startStreaming(token, fadeDuration, timesToPlay);
		
	}
	
	@Override
	public void stopStreaming(int token, float fadeDuration)
	{
		this.wrapped.stopStreaming(token, fadeDuration);
		
	}
	
	@Override
	public void pauseStreaming(int token, float fadeDuration)
	{
		this.wrapped.pauseStreaming(token, fadeDuration);
		
	}
	
	@Override
	public void eraseStreamingToken(int token)
	{
		this.wrapped.eraseStreamingToken(token);
		
	}
	
	@Override
	public void setVolume(float vol)
	{
		this.volume = vol;
		
	}
	
	@Override
	public float getVolume()
	{
		return this.volume;
	}
	
}
