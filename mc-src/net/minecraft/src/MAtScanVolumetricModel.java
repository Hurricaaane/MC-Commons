package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3Signal;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
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
	
	MAtScanVolumetricModel(MAtMod modIn)
	{
		mod = modIn;
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
		
		int worldHeight = mod.corn().util().getWorldHeight();
		
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
