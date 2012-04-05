package net.minecraft.src;

import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import eu.ha3.mc.mod.Ha3Moddable;

public class LrzMod extends Ha3Moddable
{
	final static public Logger LOGGER = Logger.getLogger("Lowrizon");
	final public int VERSION = 0;
	
	private Properties config;
	
	private ConsoleHandler conMod;
	private ConsoleHandler conEngine;
	
	LrzMod()
	{
		setCore(new LrzModCore());
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
	
	LrzModCore corn()
	{
		return (LrzModCore) core();
		
	}
}
