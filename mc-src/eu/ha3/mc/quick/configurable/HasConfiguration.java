package eu.ha3.mc.quick.configurable;

import eu.ha3.util.property.simple.ConfigProperty;

/*
--filenotes-placeholder
*/

public interface HasConfiguration
{
	public ConfigProperty getConfig();
	
	public void saveConfig();
}
