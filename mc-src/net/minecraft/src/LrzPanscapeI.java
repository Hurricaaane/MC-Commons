package net.minecraft.src;

public interface LrzPanscapeI
{
	public boolean generate();
	
	public String getStoredLegitPrint();
	
	public String calculateActualLegitPrint();
	
	public boolean isLegit();
}
