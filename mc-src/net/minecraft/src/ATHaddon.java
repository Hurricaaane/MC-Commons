package net.minecraft.src;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

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
	private Ha3SoundCommunicator sndcomms;
	private SoundPool sndPool;
	private boolean recursed;
	
	@Override
	public void onLoad()
	{
		this.sndcomms = new Ha3SoundCommunicator(this, "AT_");
		this.sndcomms.load(new Ha3Signal() {
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
	
	public void log(String contents)
	{
		System.out.println("(Audiotori) " + contents);
		
	}
	
	private void continueLoading()
	{
		try
		{
			this.sndPool =
				(SoundPool) util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, manager().getMinecraft().sndManager, "b", 1);
			//recurse();
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
		manager().hookTickEvents(true);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void recurse() throws PrivateAccessException
	{
		Map<String, ArrayList> nameToSoundPoolEntriesMapping =
			(Map<String, ArrayList>) util().getPrivateValueLiteral(
				net.minecraft.src.SoundPool.class, this.sndPool, "b", 1);
		
		URI minecraftURI = manager().getMinecraft().getMinecraftDir().toURI();
		
		for (Entry<String, ArrayList> entry : nameToSoundPoolEntriesMapping.entrySet())
		{
			String cuteNameWithDots = entry.getKey();
			ArrayList variousSounds = entry.getValue();
			
			System.out.println(cuteNameWithDots);
			for (Object object : variousSounds)
			{
				SoundPoolEntry sound = (SoundPoolEntry) object;
				try
				{
					System.out.println("    "
						+ sound.soundName + " " + minecraftURI.relativize(sound.soundUrl.toURI()).toString());
				}
				catch (URISyntaxException e)
				{
					System.out.println("    " + sound.soundName + " (" + sound.soundUrl.toString() + ")");
					
				}
				
			}
			System.out.println("");
			
		}
	}
	
	@Override
	public void onTick()
	{
		if (!this.recursed)
		{
			try
			{
				recurse();
			}
			catch (PrivateAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			manager().hookTickEvents(false);
			
		}
		
	}
	
}
