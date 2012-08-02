package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

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
		super(null, null, "MATMOS-" + identifierIn);
		setDaemon(true);
		
		this.loader = loader;
		this.identifier = identifierIn;
		
	}
	
	public void getDatabase(URL urlIn)
	{
		if (isAlive())
		{
			interrupt();
		}
		
		this.url = urlIn;
		start();
		
	}
	
	@Override
	public void run()
	{
		try
		{
			InputStream is = this.url.openStream();
			String dumped = "";
			try
			{
				dumped = new Scanner(is).useDelimiter("\\A").next();
			}
			catch (NoSuchElementException e)
			{
			}
			finally
			{
				is.close();
			}
			
			byte[] bytes = dumped.getBytes("UTF-8");
			final InputStream ios = new ByteArrayInputStream(bytes);
			
			/*this.loader.putTask(new Runnable() {
				@Override
				public void run()
				{*/
			MAtExpansionFetcher.this.loader.fetcherSuccess(MAtExpansionFetcher.this.identifier, ios);
			/*	}
			});*/
			
		}
		catch (IOException e)
		{
			/*this.loader.putTask(new Runnable() {
				@Override
				public void run()
				{*/
			MAtMod.LOGGER.warning("Error with I/O on fetcher " + MAtExpansionFetcher.this.url.toString());
			MAtMod.LOGGER.warning("(This may be a network error)");
			MAtExpansionFetcher.this.loader.fetcherFailure(MAtExpansionFetcher.this.identifier);
			/*	}
			});*/
			
		}
		
	}
	
}
