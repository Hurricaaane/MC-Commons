package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.Set;

import eu.ha3.util.property.simple.ConfigProperty;

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

public class MinapticsVariator
{
	public int ZOOM_KEY = 15;
	public int ZOOM_DURATION = 0;
	public int TWEAK_TRIGGER = 4;
	public float FOV_MIN = 0.001f;
	public float FOV_MAX = 0.65f;
	
	public boolean SLIDER_ENABLE = false;
	public boolean NOTOGGLE_ENABLE = false;
	
	public boolean SMOOTHER_ENABLE = false;
	public boolean SMOOTHER_WHILE_ZOOMED = false;
	public float SMOOTHER_INTENSITY_IDLE = 4f;
	
	public void loadConfig(ConfigProperty config)
	{
		Set<String> keys = config.getAllProperties().keySet();
		
		// I am feeling SUPER LAZY today
		Field[] fields = CCBVariator.class.getDeclaredFields();
		for (Field field : fields)
		{
			try
			{
				String fieldName = field.getName().toLowerCase();
				if (keys.contains(fieldName))
				{
					if (field.getType() == Float.TYPE)
					{
						field.setFloat(this, config.getFloat(fieldName));
					}
					else if (field.getType() == Integer.TYPE)
					{
						field.setInt(this, config.getInteger(fieldName));
					}
					else if (field.getType() == Boolean.TYPE)
					{
						field.setBoolean(this, config.getBoolean(fieldName));
					}
				}
			}
			catch (Throwable e)
			{
				CCBHaddon.log(e.getClass().getName() + ": " + field.getName());
			}
		}
	}
	
}
