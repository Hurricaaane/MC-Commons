package net.minecraft.src;

public interface LrzMagI
{
	public void markGathered();
	
	//public boolean isLegit();
	
	public void setTimeout(int timeout);
	
	public boolean isGathered();
	
	public boolean hasTimeout(int current);
	
}
