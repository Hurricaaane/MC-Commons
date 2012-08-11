package net.minecraft.src;

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

public class LrzMag implements LrzMagI
{
	private long timeout;
	//private boolean legit;
	private boolean gathered;
	
	private LrzSnapI snap;
	
	public LrzMag(LrzSnapI snap)
	{
		this.snap = snap;
		
		this.timeout = -16384; // Random, assume the stamp is always outdated
		//this.legit = false;
		this.gathered = false;
		
	}
	
	@Override
	public void markGathered()
	{
		this.gathered = true;
		
	}
	
	@Override
	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
		
	}
	
	/*@Override
	public boolean isLegit()
	{
		return legit;
	}*/
	
	@Override
	public boolean isGathered()
	{
		return this.gathered;
	}
	
	@Override
	public boolean hasTimeout(long current)
	{
		return current > this.timeout;
	}
	
}
