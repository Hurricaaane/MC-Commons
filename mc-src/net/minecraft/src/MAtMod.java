package net.minecraft.src;


import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import eu.ha3.matmos.engine.MAtmosLogger;
import eu.ha3.mc.convenience.Ha3Personalizable;
import eu.ha3.mc.mod.Ha3Moddable;


/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
 */

public class MAtMod extends Ha3Moddable implements Ha3Personalizable
{
	final static public Logger LOGGER = Logger.getLogger("MAtmos");
	final public int VERSION = 13; // Remember to change the thing on mod_Matmos_forModLoader
	
	private Properties config;
	
	private ConsoleHandler conMod;
	private ConsoleHandler conEngine;
	
	MAtMod()
	{
		setCore(new MAtModCore());
		
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
		MAtMod.LOGGER.addHandler(conMod);
		MAtMod.LOGGER.setUseParentHandlers(false);
		MAtMod.LOGGER.setLevel(levelMod);
		conMod.setLevel(levelMod);
		
		Level levelEngine = Level.INFO;
		MAtmosLogger.LOGGER.addHandler(conEngine);
		MAtmosLogger.LOGGER.setUseParentHandlers(false);
		MAtmosLogger.LOGGER.setLevel(levelEngine);
		conEngine.setLevel(levelEngine);
		
	}
	
	MAtModCore corn()
	{
		return (MAtModCore) core();
		
	}
	
	public void setModLogger(Level lvl)
	{
		MAtMod.LOGGER.setLevel(lvl);
		conMod.setLevel(lvl);
		
	}
	
	public void setEngineLogger(Level lvl)
	{
		MAtmosLogger.LOGGER.setLevel(lvl);
		conEngine.setLevel(lvl);
		
	}
	
	@Override
	public void inputOptions(Properties options)
	{
		if (config == null)
			config = createDefaultOptions();
		
		try
		{
			{
				String query = "debug.logger.mod.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					int lvl = Integer.parseInt(prop);
					setModLogger(lvl == 0 ? Level.INFO : lvl == 1 ? Level.FINE
							: lvl == 2 ? Level.FINER : lvl == 3 ? Level.FINEST
									: Level.INFO);
					config.put(query, prop);
				}
				
			}
			{
				String query = "debug.logger.engine.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					int lvl = Integer.parseInt(prop);
					setEngineLogger(lvl == 0 ? Level.INFO : lvl == 1
							? Level.FINE : lvl == 2 ? Level.FINER : lvl == 3
							? Level.FINEST : Level.INFO);
					config.put(query, prop);
				}
				
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace(); // TODO Logger could not input soundmanagerconf options
			
		}
		
	}
	
	@Override
	public Properties outputOptions()
	{
		if (config == null)
			return createDefaultOptions();
		
		// XXX Options do not change if changed due to code.
		//config.setProperty("debug.logger.mod.use", "0");
		//config.setProperty("debug.logger.engine.use", "0");
		
		return config;
	}
	
	@Override
	public void defaultOptions()
	{
		inputOptions(createDefaultOptions());
		
	}
	
	private Properties createDefaultOptions()
	{
		Properties options = new Properties();
		options.setProperty("debug.logger.mod.use", "0");
		options.setProperty("debug.logger.engine.use", "0");
		
		return options;
		
	}
	
}
