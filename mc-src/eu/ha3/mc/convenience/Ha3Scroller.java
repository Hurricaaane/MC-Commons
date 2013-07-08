package eu.ha3.mc.convenience;

import net.minecraft.src.Minecraft;

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

public abstract class Ha3Scroller
{
	private Minecraft minecraft;
	
	private boolean isRunning;
	
	private float pitchBase;
	private float pitchGlobal;
	
	protected abstract void doDraw(float fspan);
	
	protected abstract void doRoutineBefore();
	
	protected abstract void doRoutineAfter();
	
	protected abstract void doStart();
	
	protected abstract void doStop();
	
	public Ha3Scroller(Minecraft managerIn)
	{
		this.minecraft = managerIn;
		this.pitchBase = 0;
		this.pitchGlobal = 0;
		
	}
	
	protected Minecraft getMinecraft()
	{
		return this.minecraft;
		
	}
	
	public float getInitialPitch()
	{
		return this.pitchBase;
		
	}
	
	public float getPitch()
	{
		return this.pitchGlobal;
		
	}
	
	public void draw(float fspan)
	{
		if (!this.isRunning)
			return;
		
		doDraw(fspan);
		
	}
	
	public void routine()
	{
		if (!this.isRunning)
			return;
		
		doRoutineBefore();
		
		this.pitchGlobal = this.minecraft.thePlayer.rotationPitch;
		
		doRoutineAfter();
		
	}
	
	public void start()
	{
		if (this.isRunning)
			return;
		
		this.isRunning = true;
		
		this.pitchBase = this.minecraft.thePlayer.rotationPitch;
		
		doStart();
		
	}
	
	public void stop()
	{
		if (!this.isRunning)
			return;
		
		this.isRunning = false;
		
		doStop();
		
	}
	
	public boolean isRunning()
	{
		return this.isRunning;
		
	}
	
}
