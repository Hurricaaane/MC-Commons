package eu.ha3.mc.haddon.litemod;

import net.minecraft.client.settings.KeyBinding;

import com.mumfrey.liteloader.core.LiteLoader;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.OperatorKeyer;

/*
--filenotes-placeholder
*/

public class TempLiteKey extends LiteBase implements OperatorKeyer
{
	public TempLiteKey(Haddon haddon)
	{
		super(haddon);
	}
	
	@Override
	public void addKeyBinding(KeyBinding bind)
	{
		LiteLoader.getInput().registerKeyBinding(bind);
	}
	
	@Override
	public void removeKeyBinding(KeyBinding bind)
	{
		LiteLoader.getInput().unRegisterKeyBinding(bind);
	}
}
