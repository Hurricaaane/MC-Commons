package eu.ha3.mc.convenience;

import net.minecraft.src.KeyBinding;

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

class Ha3KeyBinding
{
	final int tolerence = 2;

	private int time;
	private int diffKey;
	private boolean pending;
	
	private KeyBinding mckeybinding;
	private Ha3KeyActions keyactions;
	
	Ha3KeyBinding(KeyBinding mckeyIn, Ha3KeyActions keyactionsIn)
	{
		this.mckeybinding = mckeyIn;
		this.keyactions = keyactionsIn;
		
		time = 0;
		diffKey = 0;
		pending = false;
		
	}
	
	KeyBinding getKeyBinding()
	{
		return mckeybinding;
		
	}
	
	void handleBefore()
	{
		if (time == 0)
			keyactions.doBefore();
		
		pending = true;
		diffKey = 0;
		time++;
		
	}
	
	void handle()
	{
		if (!pending) return;
		
		// This gets incremented, and reset if button is pressed (HandleBefore)
		diffKey++;
		
		// tolerence because don't know which will get executed first (keypress or think)
		if (diffKey > tolerence)
		{
			keyactions.doAfter(time);
			
			pending = false;
			time = 0;
			
		}
		else
		{
			keyactions.doDuring(time);
			
		}
		
	}
	
}
