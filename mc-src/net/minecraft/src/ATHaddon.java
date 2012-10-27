package net.minecraft.src;

import java.io.File;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3Signal;
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
	private ATSystem atSystem;
	
	@Override
	public void onLoad()
	{
		this.atSystem = new ATSystem(this);
		
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
	
	private void continueLoading()
	{
		manager().hookTickEvents(true);
	}
	
	@Override
	public void onTick()
	{
		this.atSystem.applySubstituantLocation(new File(Minecraft.getMinecraftDir(), "audiotori/substitute/"));
		manager().hookTickEvents(false);
		
	}
	
	public void log(String contents)
	{
		System.out.println("(Audiotori) " + contents);
		
	}
	
	public void debug(String contents)
	{
		System.out.println("(Audiotori-debug) " + contents);
		
	}
	
}
