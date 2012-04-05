package eu.ha3.mc.convenience;

import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.src.KeyBinding;

public class Ha3KeyManager
{
	HashMap<KeyBinding, Ha3KeyBinding> keys;
	
	public Ha3KeyManager()
	{
		keys = new HashMap<KeyBinding, Ha3KeyBinding>();
		
	}
	
	public void addKeyBinding(KeyBinding mckeybinding, Ha3KeyActions keyactions)
	{
		keys.put(mckeybinding/*.keyCode*/, new Ha3KeyBinding(mckeybinding,
				keyactions));
		
	}
	public void handleKeyDown(KeyBinding event)
	{
		if (keys.containsKey(event))
			keys.get(event).handleBefore();
		
	}
	
	public void handleRuntime()
	{
		Iterator<Ha3KeyBinding> iter = keys.values().iterator();
		
		while (iter.hasNext())
			iter.next().handle();
		
	}
	
}

