package net.minecraft.src;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3Signal;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
 */

public class MAtResourceReloader extends Thread
{
	private MAtMod mod;
	private Ha3Signal onSuccess;
	
	MAtResourceReloader(MAtMod modIn, Ha3Signal onSuccess)
	{
		this.mod = modIn;
		this.setDaemon(true);
		
		this.onSuccess = onSuccess;
		
	}
	
	@Override
	public void run()
	{
		try
		{
			// Wait for Minecraft resource download thread to end
			// The thread cannot be access from a Minecraft field so we're looking at the concurrent threads.
			Thread waiter = null;
			
			// mod.manager().getMinecraft().ingameGUI is defined AFTER ThreadDownloadResources is defined
			// Sometimes, the previous thread for initialization is too fast, and since
			// ModLoader loads before ThreadDownloadResources, it may not be ready for testing.
			// This check makes it sure it has time to even start.
			if (mod.manager().getMinecraft().ingameGUI == null)
			{
				MAtMod.LOGGER
				.info("ResourceReloader started too early! Waiting for synchronization.");
				long startLoad = System.currentTimeMillis();
				
				while (mod.manager().getMinecraft().ingameGUI == null)
				{
					// Put it asleep since we can't use synchronizing methods
					Thread.sleep(200);
					
				}
				
				long diff = (System.currentTimeMillis() - startLoad);
				float diffs = diff / 1000F;
				MAtMod.LOGGER.info("ResourceReloader can now start (took "
						+ diffs + " s.).");
				
			}
			
			for (Thread thread : Thread.getAllStackTraces().keySet())
			{
				if (thread instanceof ThreadDownloadResources)
				{
					if (thread.isAlive()) // Dunno if it's useful
						waiter = thread;
					
				}
				
			}
			
			if (waiter != null && waiter.isAlive())
			{
				MAtMod.LOGGER
				.info("ThreadDownloadResources found. Resource Reloader on hold.");
				long startLoad = System.currentTimeMillis();
				
				while (waiter != null && waiter.isAlive())
				{
					while (waiter != null && waiter.isAlive())
					{
						// Put it asleep since we can't use syncronizing methods
						Thread.sleep(200);
						
					}
					
					for (Thread thread : Thread.getAllStackTraces().keySet())
					{
						if (thread instanceof ThreadDownloadResources)
						{
							if (thread.isAlive()) // Dunno if it's useful
								waiter = thread;
							
						}
						
					}
					
				}
				
				long diff = (System.currentTimeMillis() - startLoad);
				float diffs = diff / 1000F;
				MAtMod.LOGGER.info("ThreadDownloadResources finished (took "
						+ diffs + " s.).");
				
			}
			
			reloadResources();
			
			onSuccess.signal();
			
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			
		}
		
	}
	
	public void reloadResources()
	{
		ThreadDownloadResources loader = (new ThreadDownloadResources(Minecraft
				.getMinecraftDir(), mod.manager().getMinecraft()));
		loader.reloadResources(); // This is not threaded
		
	}
	
}
