package eu.ha3.mc.convenience;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

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

public class Ha3Options implements Ha3Personalizable
{
	private Collection<Ha3Personalizable> personalizables;
	
	public Ha3Options()
	{
		this.personalizables = new HashSet<Ha3Personalizable>();
		
	}
	
	public void registerPersonalizable(Ha3Personalizable personalizable)
	{
		this.personalizables.add(personalizable);
		
	}
	
	@Override
	public void inputOptions(Properties options)
	{
		for (Ha3Personalizable personalizable : this.personalizables)
		{
			personalizable.inputOptions(options);
		}
		
	}
	
	@Override
	public Properties outputOptions()
	{
		Properties options = new Properties();
		for (Ha3Personalizable personalizable : this.personalizables)
		{
			options.putAll(personalizable.outputOptions());
		}
		
		return options;
		
	}
	
	@Override
	public void defaultOptions()
	{
		for (Ha3Personalizable personalizable : this.personalizables)
		{
			personalizable.defaultOptions();
		}
		
	}
	
}
