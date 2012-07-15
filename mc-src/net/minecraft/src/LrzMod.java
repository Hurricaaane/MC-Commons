package net.minecraft.src;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import eu.ha3.mc.haddon.SupportsFrameEvents;

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

public class LrzMod extends HaddonImpl implements SupportsFrameEvents
{
	final static public Logger LOGGER = Logger.getLogger("Lowrizon");
	final public int VERSION = 0;
	
	private ConsoleHandler conMod;
	private ConsoleHandler conEngine;
	
	LrzMod()
	{
		Formatter formatter = new Formatter() {
			@Override
			public String format(LogRecord record)
			{
				return "(" + record.getLoggerName() + " : " + record.getLevel()
						+ ") " + record.getMessage() + "\n";
			}
		};
		conMod = new ConsoleHandler();
		conMod.setFormatter(formatter);
		
		conEngine = new ConsoleHandler();
		conEngine.setFormatter(formatter);
		
		// TODO Customizable level
		Level levelMod = Level.INFO;
		LrzMod.LOGGER.addHandler(conMod);
		LrzMod.LOGGER.setUseParentHandlers(false);
		LrzMod.LOGGER.setLevel(levelMod);
		conMod.setLevel(levelMod);
		
	}
	
	private LrzWorldCacheI worldCache;
	
	@Override
	public void onLoad()
	{
		manager().hookFrameEvents(true);
		
		worldCache = new LrzWorldCache(8, 64, "poland", this);
		
	}
	
	@Override
	public void onFrame(float fspan)
	{
		if (util().getClientTick() % 50 != 0)
			return;
		
		EntityPlayer player = manager().getMinecraft().thePlayer;
		if (player != null)
		{
			for (int i = -6; i < 6; i++)
				for (int j = -6; j < 6; j++)
					worldCache.requestAverage((int) player.posX + i * 16,
							(int) player.posZ + j * 16);
			
			worldCache.save();
			
		}
		
	}
	
}
