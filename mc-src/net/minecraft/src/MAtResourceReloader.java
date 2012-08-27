package net.minecraft.src;

import java.io.File;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3Signal;

public class MAtResourceReloader extends Thread
{
	private MAtMod mod;
	private Ha3Signal onSuccess;
	
	MAtResourceReloader(MAtMod modIn, Ha3Signal onSuccess)
	{
		this.mod = modIn;
		setDaemon(true);
		
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
			if (this.mod.manager().getMinecraft().ingameGUI == null)
			{
				MAtMod.LOGGER.info("ResourceReloader started too early! Waiting for synchronization.");
				long startLoad = System.currentTimeMillis();
				
				while (this.mod.manager().getMinecraft().ingameGUI == null)
				{
					// Put it asleep since we can't use synchronizing methods
					Thread.sleep(200);
					
				}
				
				long diff = System.currentTimeMillis() - startLoad;
				float diffs = diff / 1000F;
				MAtMod.LOGGER.info("ResourceReloader can now start (took " + diffs + " s.).");
				
			}
			
			for (Thread thread : Thread.getAllStackTraces().keySet())
			{
				if (thread instanceof ThreadDownloadResources)
				{
					if (thread.isAlive())
					{
						waiter = thread;
					}
					
				}
				
			}
			
			if (waiter != null && waiter.isAlive())
			{
				MAtMod.LOGGER.info("ThreadDownloadResources found. Resource Reloader on hold.");
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
							if (thread.isAlive())
							{
								waiter = thread;
							}
							
						}
						
					}
					
				}
				
				long diff = System.currentTimeMillis() - startLoad;
				float diffs = diff / 1000F;
				MAtMod.LOGGER.info("ThreadDownloadResources finished (took " + diffs + " s.).");
				
			}
			
			reloadResources();
			
			this.onSuccess.signal();
			
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			
		}
		
	}
	
	public void reloadResources()
	{
		cpy_reloadResources();
		
	}
	
	/**
	 * Reloads the resource folder and passes the resources to Minecraft to
	 * install.
	 */
	private void cpy_reloadResources()
	{
		loadResource(new File(Minecraft.getMinecraftDir(), "resources/"), "");
	}
	
	/**
	 * Loads a resource and passes it to Minecraft to install.
	 */
	private void loadResource(File par1File, String par2Str)
	{
		File[] var3 = par1File.listFiles();
		File[] var4 = var3;
		int var5 = var3.length;
		
		for (int var6 = 0; var6 < var5; ++var6)
		{
			File var7 = var4[var6];
			
			if (var7.isDirectory())
			{
				loadResource(var7, par2Str + var7.getName() + "/");
			}
			else
			{
				try
				{
					this.mod.getManager().getMinecraft().installResource(par2Str + var7.getName(), var7);
				}
				catch (Exception var9)
				{
					System.out.println("Failed to add " + par2Str + var7.getName());
				}
			}
		}
	}
	
}
