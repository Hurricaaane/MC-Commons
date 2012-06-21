package net.minecraft.src;

import eu.ha3.matmos.engine.MAtmosData;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
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
