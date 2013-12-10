package eu.ha3.mc.convenience;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

/* x-placeholder-wtfplv2 */

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
