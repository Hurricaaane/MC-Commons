package net.minecraft.src;


public interface LrzWorldCacheI
{
	public LrzMod mod();
	public int getSplit();
	public int getSideCount();
	public int requestAverage(int worldX, int worldZ);
	public boolean save();
	
}
