package eu.ha3.mc.convenience;

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
		
		this.time = 0;
		this.diffKey = 0;
		this.pending = false;
		
	}
	
	KeyBinding getKeyBinding()
	{
		return this.mckeybinding;
		
	}
	
	void handleBefore()
	{
		if (this.time == 0)
		{
			this.keyactions.doBefore();
		}
		
		this.pending = true;
		this.diffKey = 0;
		this.time++;
		
	}
	
	void handle()
	{
		if (!this.pending)
			return;
		
		// This gets incremented, and reset if button is pressed (HandleBefore)
		this.diffKey++;
		
		// tolerence because don't know which will get executed first (keypress or think)
		if (this.diffKey > this.tolerence)
		{
			this.keyactions.doAfter(this.time);
			
			this.pending = false;
			this.time = 0;
			
		}
		else
		{
			this.keyactions.doDuring(this.time);
			
		}
		
	}
	
}
