package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3EdgeModel;
import eu.ha3.mc.convenience.Ha3EdgeTrigger;
import eu.ha3.mc.haddon.SupportsTickEvents;

public class HaddonPreface extends HaddonImpl implements SupportsTickEvents
{
	private static final HaddonPreface instance = new HaddonPreface();
	
	private boolean defined;
	private Ha3EdgeTrigger trigger;
	
	private HaddonPreface()
	{
		defined = false;
		trigger = new Ha3EdgeTrigger(new Ha3EdgeModel() {
			
			@Override
			public void onTrueEdge()
			{
				open();
			}
			
			@Override
			public void onFalseEdge()
			{
			}
		});
		
	}
	
	public static HaddonPreface getInstance()
	{
		return instance;
		
	}
	
	public boolean isDefined()
	{
		return defined;
		
	}
	
	public void define()
	{
		if (isDefined())
			return;
		
		defined = true;
		System.out.println("HaddonPreface is now defined.");
		
	}
	
	@Override
	public void onLoad()
	{
		// Is never called, do not use.
		
	}
	
	@Override
	public void onTick()
	{
		trigger.signalState(util().areKeysDown(29, 42, 35));
		System.out.println("ff");
		
	}
	
	protected void open()
	{
		manager().getMinecraft().displayGuiScreen(null);
		System.out.println("ff");
		
	}
	
}
