package net.minecraft.src;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import eu.ha3.mc.convenience.Ha3StaticUtilities;

public class mod_ModProductionErrorChecker extends HaddonBridgeModLoader
{
	public mod_ModProductionErrorChecker()
	{
		super(new HaddonImpl() {
			@Override
			public void onLoad()
			{
				if (Ha3StaticUtilities.classExists("net.minecraft.src.Block", this))
				{
					System.out.println("ModProductionErrorChecker is not running in a compiled Minecraft.");
					return;
					
				}
				System.out.println("ModProductionErrorChecker is activated.");
				
				Formatter formatter = new Formatter() {
					@Override
					public String format(LogRecord record)
					{
						return "("
							+ record.getLoggerName() + " : " + record.getLevel() + ") " + record.getMessage() + "\n";
					}
				};
				ConsoleHandler conMod = new ConsoleHandler();
				conMod.setFormatter(formatter);
				
				Level levelMod = Level.FINE;
				HaddonUtilitySingleton.LOGGER.addHandler(conMod);
				HaddonUtilitySingleton.LOGGER.setUseParentHandlers(false);
				HaddonUtilitySingleton.LOGGER.setLevel(levelMod);
				conMod.setLevel(levelMod);
				
			}
			
		});
		
	}
	
}
