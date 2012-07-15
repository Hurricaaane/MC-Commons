package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

public class MAtExpansionFetcher extends Thread
{
	private MAtExpansionLoader loader;
	
	private URL url;
	private String identifier;
	
	public MAtExpansionFetcher(MAtExpansionLoader loader, String identifierIn)
	{
		this.setDaemon(true);
		this.loader = loader;
		
		setName("MATMOS-" + identifierIn);
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
			final InputStream is = url.openStream();
			loader.putTask(new Runnable() {
				@Override
				public void run()
				{
					loader.fetcherSuccess(identifier, is);
				}
			});
			
		}
		catch (IOException e)
		{
			loader.putTask(new Runnable() {
				@Override
				public void run()
				{
					MAtMod.LOGGER
					.warning("Error with I/O on fetcher " + url.toString());
					MAtMod.LOGGER.warning("(This may be a network error)");
					loader.fetcherFailure(identifier);
				}
			});
			
		}
		
	}
	
}
