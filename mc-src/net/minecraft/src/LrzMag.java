package net.minecraft.src;

public class LrzMag implements LrzMagI
{
	private int timeout;
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
		gathered = true;
		
	}
	
	@Override
	public void setTimeout(int timeout)
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
		return gathered;
	}
	
	@Override
	public boolean hasTimeout(int current)
	{
		return current > timeout;
	}
	
}
