package eu.ha3.mc.convenience;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.settings.KeyBinding;
import eu.ha3.mc.haddon.Keyer;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;

/*
--filenotes-placeholder
*/

public class Ha3KeyManager_2 implements SupportsTickEvents
{
	private final Keyer keyer;
	private Map<KeyBinding, Ha3KeyActions> keys = new HashMap<KeyBinding, Ha3KeyActions>();
	private Map<KeyBinding, Integer> state = new HashMap<KeyBinding, Integer>();
	
	public Ha3KeyManager_2(Keyer keyer)
	{
		this.keyer = keyer;
	}
	
	public Ha3KeyManager_2()
	{
		this.keyer = null;
	}
	
	public void addKeyBinding(KeyBinding bind, Ha3KeyActions keyActions)
	{
		if (this.keyer != null)
		{
			this.keyer.addKeyBinding(bind);
		}
		
		this.keys.put(bind, keyActions);
		this.state.put(bind, 0);
	}
	
	@Override
	public void onTick()
	{
		for (KeyBinding bind : this.keys.keySet())
		{
			if (bind.getIsKeyPressed())
			{
				int oldVal = this.state.get(bind);
				this.state.put(bind, oldVal + 1);
				
				if (oldVal == 0)
				{
					this.keys.get(bind).doBefore();
				}
				else
				{
					this.keys.get(bind).doDuring(oldVal);
				}
			}
			else
			{
				int state = this.state.get(bind);
				if (state > 0)
				{
					this.keys.get(bind).doAfter(state);
					this.state.put(bind, 0);
				}
			}
		}
	}
}
