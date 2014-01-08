package eu.ha3.mc.quick.keys;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.settings.KeyBinding;
import eu.ha3.mc.haddon.supporting.SupportsKeyEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;

/*
--filenotes-placeholder
*/

public class KeyWatcher implements SupportsTickEvents
{
	private final SupportsKeyEvents watcher;
	private final List<KeyBinding> keys;
	
	public KeyWatcher(SupportsKeyEvents keyWatcher)
	{
		this.watcher = keyWatcher;
		this.keys = new ArrayList<KeyBinding>();
	}
	
	public void add(KeyBinding key)
	{
		this.keys.add(key);
	}
	
	@Override
	public void onTick()
	{
		for (KeyBinding key : this.keys)
			if (key.func_151470_d())
			{
				this.watcher.onKey(key);
			}
	}
}
