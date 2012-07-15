package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3Signal;

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

public class MAtScanVolumetricModel
{
	private MAtMod mod;
	
	private MAtScanCoordsOps pipeline;
	
	private long xstart;
	private long ystart;
	private long zstart;
	
	private long xsize;
	private long ysize;
	private long zsize;
	
	private long opspercall;
	
	private boolean isScanning;
	
	private long finality;
	private long progress;
	
	private Ha3Signal onDone;
	
	MAtScanVolumetricModel(MAtMod mod2)
	{
		mod = mod2;
		pipeline = null;
		isScanning = false;
		
	}
	
	void setPipeline(MAtScanCoordsOps pipelineIn)
	{
		pipeline = pipelineIn;
		
	}
	
	void startScan(long x, long y, long z, long xsizeIn, long ysizeIn,
			long zsizeIn, long opspercallIn, Ha3Signal onDoneIn) //throws MAtScannerTooLargeException
	{
		if (isScanning)
			return;
		
		if (pipeline == null)
			return;
		
		if (opspercallIn <= 0)
			throw new IllegalArgumentException();
		
		int worldHeight = mod.util().getWorldHeight();
		
		if (ysizeIn > worldHeight)
			ysizeIn = worldHeight;
		//throw new MAtScannerTooLargeException();
		
		xsize = xsizeIn;
		ysize = ysizeIn;
		zsize = zsizeIn;
		
		y = y - ysize / 2;
		
		if (y < 0)
			y = 0;
		else if (y > (worldHeight - ysize))
			y = worldHeight - ysize;
		
		xstart = x - xsize / 2;
		ystart = y; // ((y - ysize / 2)) already done before
		zstart = z - zsize / 2;
		opspercall = opspercallIn;
		
		progress = 0;
		finality = xsize * ysize * zsize;
		
		pipeline.begin();
		isScanning = true;
		
	}
	
	void routine()
	{
		if (!isScanning)
			return;
		
		long ops = 0;
		long x, y, z;
		while ((ops < opspercall) && (progress < finality))
		{
			// TODO Optimize this
			x = xstart + progress % xsize;
			z = zstart + (progress / xsize) % zsize;
			y = ystart + (progress / xsize / zsize);
			
			pipeline.input(x, y, z);
			
			ops++;
			progress++;
			
		}
		
		if (progress >= finality)
			scanDoneEvent();
		
	}
	
	void stopScan()
	{
		isScanning = false;
		
	}
	
	private void scanDoneEvent()
	{
		if (!isScanning)
			return;
		
		pipeline.finish();
		stopScan();
		
		if (onDone != null)
			onDone.signal();
		
	}
	
}
