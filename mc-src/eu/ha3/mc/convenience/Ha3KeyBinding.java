package eu.ha3.mc.convenience;

import net.minecraft.src.KeyBinding;

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
