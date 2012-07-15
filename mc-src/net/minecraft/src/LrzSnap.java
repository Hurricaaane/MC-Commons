package net.minecraft.src;

import java.awt.image.BufferedImage;

import net.minecraft.client.Minecraft;

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

public class LrzSnap implements LrzSnapI
{
	private int coordA;
	private int coordB;
	private boolean changed;
	private BufferedImage image;
	
	private LrzWorldCacheI worldCache;
	private LrzMagI[][] mags;
	
	private int[][] gatherBuffer;
	
	private int[][] failedBuffer;
	
	public LrzSnap(LrzWorldCacheI worldCache, int snapCoordA, int snapCoordB)
	{
		this.worldCache = worldCache;
		this.coordA = snapCoordA;
		this.coordB = snapCoordB;
		this.changed = false;
		this.gatherBuffer = new int[this.worldCache.getSplit()][this.worldCache.getSplit()];
		
		buildEmptyImage();
		
		buildMags();
		
		buildFailedBuffer();
		
	}
	
	public LrzSnap(LrzWorldCacheI worldCache, int snapCoordA, int snapCoordB, BufferedImage bufferedImage)
	{
		this.worldCache = worldCache;
		this.coordA = snapCoordA;
		this.coordB = snapCoordB;
		this.changed = false;
		this.gatherBuffer = new int[this.worldCache.getSplit()][this.worldCache.getSplit()];
		
		this.image = bufferedImage; // FIXME: This assumes the image has the correct dimensions
		
		buildMags();
		
		buildFailedBuffer();
		
	}
	
	private void buildFailedBuffer()
	{
		int s = this.worldCache.getSplit();
		int m = s - 1;
		int v = 0xFFFFFF;
		this.failedBuffer = new int[s][s];
		
		for (int i = 0; i < s; i++)
		{
			this.failedBuffer[0][i] = v;
			this.failedBuffer[m][i] = v;
			this.failedBuffer[i][0] = v;
			this.failedBuffer[i][m] = v;
			
		}
		
	}
	
	private void buildEmptyImage()
	{
		int size = this.worldCache.getSideCount() * this.worldCache.getSplit();
		this.image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		
	}
	
	private void buildMags()
	{
		int sideCount = this.worldCache.getSideCount();
		this.mags = new LrzMag[sideCount][sideCount];
		for (int i = 0; i < sideCount; i++)
		{
			for (int j = 0; j < sideCount; j++)
			{
				this.mags[i][j] = new LrzMag(this);
			}
		}
		
	}
	
	private int modulus(int a, int n)
	{
		return (int) (a - n * Math.floor((float) a / n));
		
	}
	
	@Override
	public int requestAverage(int worldX, int worldZ)
	{
		int iMag = modulus(worldX / LrzWorldCache.CHUNK_SIZE, this.worldCache.getSideCount());
		int jMag = modulus(worldZ / LrzWorldCache.CHUNK_SIZE, this.worldCache.getSideCount());
		int split = this.worldCache.getSplit();
		
		int average = 0;
		
		LrzMagI mag = this.mags[iMag][jMag];
		
		int tick = this.worldCache.mod().util().getClientTick();
		if (mag.hasTimeout(tick))
		{
			boolean gathered = gatherChunk(iMag, jMag);
			if (mag.isGathered())
			{
				mag.setTimeout(tick + LrzWorldCache.TICKS_TO_CACHE_TIMEOUT);
			}
			else
			{
				mag.setTimeout(tick + LrzWorldCache.TICKS_TO_RETRY_TIMEOUT);
			}
			
			if (gathered)
			{
				mag.markGathered();
			}
			
			this.changed = true;
			
		}
		
		if (mag.isGathered())
		{
			average =
				this.image.getRGB(iMag * split + modulus(worldX / (LrzWorldCache.CHUNK_SIZE / split), split), jMag
					* split + modulus(worldZ / (LrzWorldCache.CHUNK_SIZE / split), split)) & 0xFF;
			
		}
		else
		{
			average = 0;
			
		}
		
		return average;
	}
	
	private int sane(float a)
	{
		return (int) Math.floor(a);
		
	}
	
	private boolean gatherChunk(int iMag, int jMag)
	{
		int xOrigin =
			this.coordA * this.worldCache.getSideCount() * LrzWorldCache.CHUNK_SIZE + iMag * LrzWorldCache.CHUNK_SIZE;
		int zOrigin =
			this.coordB * this.worldCache.getSideCount() * LrzWorldCache.CHUNK_SIZE + jMag * LrzWorldCache.CHUNK_SIZE;
		int splits = this.worldCache.getSplit();
		int splitPhysicalSize = LrzWorldCache.CHUNK_SIZE / splits;
		int splitPhysicalSizeSquared = splitPhysicalSize * splitPhysicalSize;
		Minecraft mc = this.worldCache.mod().manager().getMinecraft();
		
		boolean fail = false;
		
		for (int b = 0; b < splits && !fail; b++)
		{
			int zPush = b * splitPhysicalSize;
			
			for (int a = 0; a < splits && !fail; a++)
			{
				int xPush = a * splitPhysicalSize;
				
				int average = 0;
				for (int xPand = 0; xPand < splitPhysicalSize; xPand++)
				{
					for (int zPand = 0; zPand < splitPhysicalSize; zPand++)
					{
						average =
							average + mc.theWorld.getHeightValue(xOrigin + xPush + xPand, zOrigin + zPush + zPand);
						
					}
				}
				average = average / splitPhysicalSizeSquared;
				if (average == 0)
				{
					fail = true;
				}
				int o = average;
				this.gatherBuffer[a][b] = o << 16 | o << 8 | o;
				
			}
		}
		if (!fail)
		{
			for (int b = 0; b < splits && !fail; b++)
			{
				for (int a = 0; a < splits && !fail; a++)
				{
					this.image.setRGB(iMag * splits + a, jMag * splits + b, this.gatherBuffer[a][b]);
					
				}
			}
			
		}
		else
		{
			for (int b = 0; b < splits && !fail; b++)
			{
				for (int a = 0; a < splits && !fail; a++)
				{
					this.image.setRGB(iMag * splits + a, jMag * splits + b, this.failedBuffer[a][b]);
					
				}
			}
			LrzMod.LOGGER.warning("FAILED with " + iMag + " " + jMag);
		}
		
		return !fail;
		
	}
	
	@Override
	public boolean hasChanged()
	{
		return this.changed;
	}
	
	@Override
	public void sendMeta(String metaString) throws LrzInvalidDataException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public BufferedImage getImage()
	{
		return this.image;
	}
	
	@Override
	public String getMetaString()
	{
		// TODO Implement me
		return "";
	}
	
	@Override
	public int getCoordA()
	{
		return this.coordA;
	}
	
	@Override
	public int getCoordB()
	{
		return this.coordB;
	}
	
	@Override
	public void clearChangeState()
	{
		this.changed = false;
		
	}
	
}
