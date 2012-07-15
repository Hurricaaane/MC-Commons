package net.minecraft.src;

import eu.ha3.matmos.engine.MAtmosData;

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

public abstract class MAtScanCoordsPipeline implements MAtScanCoordsOps
{
	private MAtMod mod;
	private MAtmosData data;
	
	private MAtScanCoordsPipeline next;
	
	MAtScanCoordsPipeline(MAtMod mod2, MAtmosData dataIn)
	{
		mod = mod2;
		data = dataIn;
		next = null;
		
	}
	
	public MAtMod mod()
	{
		return mod;
		
	}
	
	public MAtmosData data()
	{
		return data;
		
	}
	
	abstract void doBegin();
	
	abstract void doInput(long x, long y, long z);
	
	abstract void doFinish();
	
	public void append(MAtScanCoordsPipeline operator)
	{
		if (next == null)
			next = operator;
		
		else
			next.append(operator);
		
	}
	
	@Override
	public void begin()
	{
		doBegin();
		
		if (next != null)
			next.begin();
		
	}
	
	@Override
	public void finish()
	{
		doFinish();
		
		if (next != null)
			next.finish();
		
	}
	
	@Override
	public void input(long x, long y, long z)
	{
		doInput(x, y, z);
		
		if (next != null)
			next.input(x, y, z);
		
	}
	
}
