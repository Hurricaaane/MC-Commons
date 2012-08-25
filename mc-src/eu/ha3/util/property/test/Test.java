package eu.ha3.util.property.test;

import java.util.Map.Entry;

import eu.ha3.util.property.contract.PropertyHolder;
import eu.ha3.util.property.simple.ConfigProperty;

public class Test implements Runnable
{
	public static void main(String[] args)
	{
		new Test().run();
	}
	
	@Override
	public void run()
	{
		ConfigProperty config = new ConfigProperty();
		config.setProperty("derp", "Yes");
		config.setProperty("boo", true);
		config.commit();
		config.setSource("user.cfg");
		config.load();
		config.save();
		
		printHolder(config);
		
	}
	
	private void printHolder(PropertyHolder holder)
	{
		System.out.println("- " + holder.toString());
		for (Entry<String, String> property : holder.getAllProperties().entrySet())
		{
			System.out.println("  - " + property.getKey().toString() + "\t: " + property.getValue().toString());
			
		}
	}
}
