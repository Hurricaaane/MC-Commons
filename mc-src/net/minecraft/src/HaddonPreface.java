package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3EdgeModel;
import eu.ha3.mc.convenience.Ha3EdgeTrigger;
import eu.ha3.mc.haddon.SupportsTickEvents;

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

public class HaddonPreface extends HaddonImpl implements SupportsTickEvents
{
	private static final HaddonPreface instance = new HaddonPreface();
	
	private boolean defined;
	private Ha3EdgeTrigger trigger;
	
	private HaddonPreface()
	{
		this.defined = false;
		this.trigger = new Ha3EdgeTrigger(new Ha3EdgeModel() {
			
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
		return this.defined;
		
	}
	
	public void define()
	{
		if (isDefined())
			return;
		
		this.defined = true;
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
		this.trigger.signalState(util().areKeysDown(29, 42, 35));
		System.out.println("ff");
		
	}
	
	protected void open()
	{
		manager().getMinecraft().displayGuiScreen(null);
		System.out.println("ff");
		
	}
	
}
