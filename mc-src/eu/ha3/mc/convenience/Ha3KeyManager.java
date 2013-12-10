package eu.ha3.mc.convenience;

import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.client.settings.KeyBinding;

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

public class Ha3KeyManager
{
	HashMap<KeyBinding, Ha3KeyBinding> keys;
	
	public Ha3KeyManager()
	{
		this.keys = new HashMap<KeyBinding, Ha3KeyBinding>();
		
	}
	
	public void addKeyBinding(KeyBinding mckeybinding, Ha3KeyActions keyactions)
	{
		this.keys.put(mckeybinding/*.keyCode*/, new Ha3KeyBinding(mckeybinding, keyactions));
		
	}
	
	public void handleKeyDown(KeyBinding event)
	{
		if (this.keys.containsKey(event))
		{
			this.keys.get(event).handleBefore();
		}
		
	}
	
	public void handleRuntime()
	{
		Iterator<Ha3KeyBinding> iter = this.keys.values().iterator();
		
		while (iter.hasNext())
		{
			iter.next().handle();
		}
		
	}
	
}
