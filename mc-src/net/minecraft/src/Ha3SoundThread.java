package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3Signal;
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

public class Ha3SoundThread extends Thread
{
	final int sleepTime = 500;
	
	private Ha3SoundCommunicator sndComm;
	private Ha3Signal onSuccess;
	private Ha3Signal onFailure;
	
	Ha3SoundThread(Ha3SoundCommunicator sndCommIn, Ha3Signal onSuccessIn,
			Ha3Signal onFailureIn)
			{
		this.setDaemon(true);
		
		sndComm = sndCommIn;
		onSuccess = onSuccessIn;
		onFailure = onFailureIn;
		
			}
	
	@Override
	public void run()
	{
		try
		{
			while (!sndComm.loadSoundManager())
			{
				sleep(sleepTime);
			}
			while (!sndComm.loadSoundSystem())
			{
				sleep(sleepTime);
			}
			if (onSuccess != null)
				onSuccess.signal();
			
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
			
			if (onFailure != null)
				onFailure.signal();
			
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			
			if (onFailure != null)
				onFailure.signal();
			
		}
		
	}
	
}
