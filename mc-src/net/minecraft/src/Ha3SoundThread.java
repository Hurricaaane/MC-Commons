package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3Signal;
import eu.ha3.mc.haddon.PrivateAccessException;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
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
