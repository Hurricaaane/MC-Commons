package eu.ha3.mc.convenience;

import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.src.KeyBinding;

/* x-placeholder-wtfplv2 */

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
