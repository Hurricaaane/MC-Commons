package net.minecraft.src;

import java.awt.image.BufferedImage;

public interface LrzSnapI
{
	public int requestAverage(int worldX, int worldZ);
	
	public boolean hasChanged();
	
	public void clearChangeState();
	
	public void sendMeta(String metaString) throws LrzInvalidDataException;
	
	public BufferedImage getImage();
	
	public String getMetaString();
	
	public int getCoordA();
	
	public int getCoordB();
	
}
