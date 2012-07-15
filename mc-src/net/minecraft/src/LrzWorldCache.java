package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

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

public class LrzWorldCache implements LrzWorldCacheI
{
	public static final int CHUNK_SIZE = 16;
	
	public static int TICKS_TO_CACHE_TIMEOUT = 8000;
	public static int TICKS_TO_RETRY_TIMEOUT = 60;
	private int SPLIT;
	private int SIDECOUNT;
	private String worldIdentifier;
	
	private File cacheDataDirectory;
	
	private Map<Integer, Map<Integer, LrzSnapI>> snaps;
	private LrzMod mod;
	
	public LrzWorldCache(int split, int sidecount, String worldIdentifier, LrzMod mod)
	{
		this.mod = mod;
		this.SPLIT = split;
		this.SIDECOUNT = sidecount;
		this.worldIdentifier = worldIdentifier;
		this.cacheDataDirectory = new File(mod.manager().getMinecraft().mcDataDir, this.worldIdentifier + "/");
		
		this.snaps = new HashMap<Integer, Map<Integer, LrzSnapI>>();
		
	}
	
	@Override
	public LrzMod mod()
	{
		return this.mod;
		
	}
	
	@Override
	public int getSplit()
	{
		return this.SPLIT;
		
	}
	
	@Override
	public int getSideCount()
	{
		return this.SIDECOUNT;
	}
	
	@Override
	public int requestAverage(int worldX, int worldZ)
	{
		int snapA = chunkToSnapCoord(worldToChunkCoord(worldX));
		int snapB = chunkToSnapCoord(worldToChunkCoord(worldZ));
		
		LrzSnapI snap = pickSnap(snapA, snapB);
		return snap.requestAverage(worldX - snapA * LrzWorldCache.CHUNK_SIZE * this.SIDECOUNT, worldZ
			- snapB * this.SIDECOUNT * LrzWorldCache.CHUNK_SIZE * this.SIDECOUNT);
		
	}
	
	@Override
	public boolean save()
	{
		if (!this.cacheDataDirectory.exists())
		{
			this.cacheDataDirectory.mkdirs();
		}
		
		for (Map<Integer, LrzSnapI> maps : this.snaps.values())
		{
			for (LrzSnapI snap : maps.values())
			{
				if (snap.hasChanged())
				{
					BufferedImage image = snap.getImage();
					String metaString = snap.getMetaString();
					
					try
					{
						File imageFile = getBufferFile(snap.getCoordA(), snap.getCoordB());
						if (imageFile.exists() && !imageFile.canWrite())
							throw new IOException();
						
						ImageIO.write(image, "png", imageFile);
					}
					catch (IOException e)
					{
						LrzMod.LOGGER.severe("Failed to write image to disk!");
					}
					
					FileWriter writer = null;
					try
					{
						File metaFile = getMetaFile(snap.getCoordA(), snap.getCoordB());
						writer = new FileWriter(metaFile);
						if (metaFile.exists() && !metaFile.canWrite())
							throw new IOException();
						
						writer.write(metaString);
						
					}
					catch (IOException e)
					{
						LrzMod.LOGGER.severe("Failed to write meta to disk!");
					}
					finally
					{
						try
						{
							writer.close();
						}
						catch (IOException ignore)
						{
						}
						
					}
					
					snap.clearChangeState();
					
				}
				
			}
		}
		
		return false;
	}
	
	private int sane(float a)
	{
		return (int) Math.floor(a);
		
	}
	
	private int worldToChunkCoord(int worldCoordinate)
	{
		return sane((float) worldCoordinate / CHUNK_SIZE);
		
	}
	
	private int chunkToSnapCoord(int chunkCoordinate)
	{
		return sane((float) chunkCoordinate / this.SIDECOUNT);
		
	}
	
	private boolean isSnapCached(int snapCoordA, int snapCoordB)
	{
		if (!this.snaps.containsKey(snapCoordA))
			return false;
		
		return this.snaps.get(snapCoordA).containsKey(snapCoordB);
		
	}
	
	private LrzSnapI pickSnap(int snapCoordA, int snapCoordB)
	{
		LrzSnapI snap = null;
		
		if (isSnapCached(snapCoordA, snapCoordB))
		{
			snap = this.snaps.get(snapCoordA).get(snapCoordB);
		}
		else if (isSnapOnDisk(snapCoordA, snapCoordB))
		{
			snap = cacheSnapFromDisk(snapCoordA, snapCoordB);
		}
		else
		{
			snap = cacheEmptySnap(snapCoordA, snapCoordB);
		}
		
		return snap;
		
	}
	
	private LrzSnapI cacheEmptySnap(int snapCoordA, int snapCoordB)
	{
		LrzSnapI snap = null;
		snap = new LrzSnap(this, snapCoordA, snapCoordB);
		
		if (!this.snaps.containsKey(snapCoordA))
		{
			Map<Integer, LrzSnapI> inMap = new HashMap<Integer, LrzSnapI>();
			
			this.snaps.put(snapCoordA, inMap);
			
			inMap.put(snapCoordB, snap);
			
		}
		else
		{
			LrzSnapI old = this.snaps.get(snapCoordA).put(snapCoordB, snap);
			if (old != null)
			{
				LrzMod.LOGGER.severe("Caching a new Snap over an existing one! This should never happen");
			}
			
		}
		
		return snap;
		
	}
	
	private File getBufferFile(int snapCoordA, int snapCoordB)
	{
		return new File(this.cacheDataDirectory, "snap." + snapCoordA + "." + snapCoordB + ".png");
		
	}
	
	private File getMetaFile(int snapCoordA, int snapCoordB)
	{
		return new File(this.cacheDataDirectory, "snap." + snapCoordA + "." + snapCoordB + ".meta");
		
	}
	
	private boolean isSnapOnDisk(int snapCoordA, int snapCoordB)
	{
		return getBufferFile(snapCoordA, snapCoordB).exists() && getMetaFile(snapCoordA, snapCoordB).exists();
		
	}
	
	private LrzSnapI cacheSnapFromDisk(int snapCoordA, int snapCoordB)
	{
		LrzSnapI snap = null;
		
		try
		{
			File bufferedImageFile = getBufferFile(snapCoordA, snapCoordB);
			File metaFile = getBufferFile(snapCoordA, snapCoordB);
			
			if (!bufferedImageFile.exists() || !metaFile.exists())
				throw new IOException();
			
			//FIXME not working
			//String metaString = new Scanner(metaFile).useDelimiter("\\A")
			//		.next();
			String metaString = ""; // FIXME !!
			
			BufferedImage bufferedImage = ImageIO.read(bufferedImageFile);
			
			if (bufferedImage.getWidth() != this.SIDECOUNT * this.SPLIT)
				throw new LrzInvalidDataException();
			if (bufferedImage.getHeight() != this.SIDECOUNT * this.SPLIT)
				throw new LrzInvalidDataException();
			//if (bufferedImage.getType() != BufferedImage.TYPE_INT_ARGB)
			//	throw new LrzInvalidDataException();
			
			snap = new LrzSnap(this, snapCoordA, snapCoordB, bufferedImage);
			snap.sendMeta(metaString);
			
			//throw new LrzNotImplementedException(); // TODO Test my implementation
			
		}
		catch (IOException e)
		{
			LrzMod.LOGGER.severe("Failed to load snap from disk!");
			
			snap = cacheEmptySnap(snapCoordA, snapCoordB);
		}
		catch (LrzInvalidDataException e)
		{
			LrzMod.LOGGER.severe("Snap data on disk is invalid!");
			
			snap = cacheEmptySnap(snapCoordA, snapCoordB);
		}
		
		return snap;
		
	}
	
}
