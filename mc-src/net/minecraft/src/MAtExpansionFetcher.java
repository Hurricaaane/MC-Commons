package net.minecraft.src;

import java.io.IOException;
import java.net.URL;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

public class MAtExpansionFetcher extends Thread
{
	private MAtExpansionLoader loader;
	
	private URL url;
	private String identifier;
	
	public MAtExpansionFetcher(MAtExpansionLoader loader, String identifierIn)
	{
		this.setDaemon(true);
		this.loader = loader;
		
		setName("MAtmos Fetcher " + identifierIn);
		setDaemon(true);
		
		identifier = identifierIn;
		
	}
	
	public void getDatabase(URL urlIn)
	{
		if (this.isAlive())
			this.interrupt();
		
		url = urlIn;
		this.start();
		
	}
	
	@Override
	public void run()
	{
		try
		{
			loader.fetcherSuccess(identifier, url.openStream());
			
		}
		catch (IOException e)
		{
			MAtMod.LOGGER
			.warning("Error with I/O on fetcher " + url.toString());
			MAtMod.LOGGER.warning("(This may be a network error)");
			loader.fetcherFailure(identifier);
			
		}
		
	}
	
}
