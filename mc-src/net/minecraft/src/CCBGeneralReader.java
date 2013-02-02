package net.minecraft.src;

import java.util.Random;

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

public class CCBGeneralReader implements CCBReader
{
	final protected CCBHaddon mod;
	
	protected boolean isPegasus;
	protected float dmwBase;
	
	protected int hoof;
	
	public CCBGeneralReader(CCBHaddon mod)
	{
		this.mod = mod;
	}
	
	@Override
	public void frame(EntityPlayer ply)
	{
		if (this.dmwBase > ply.distanceWalkedModified)
		{
			this.dmwBase = 0;
		}
		
		float dwm = ply.distanceWalkedModified - this.dmwBase;
		
		float speed = (float) Math.sqrt(ply.motionX * ply.motionX + ply.motionZ * ply.motionZ);
		float distance = 0.85f;
		
		if (speed > 0.13f)
		{
			if (this.hoof == 3)
			{
				distance = 0.7f;
			}
			else if (this.hoof == 2)
			{
				distance = 0.15f;
			}
			else
			{
				distance = 0.25f;
			}
		}
		
		if (dwm > distance)
		{
			float range = 0.1f;
			
			int xx = MathHelper.floor_double(ply.posX);
			int yy = MathHelper.floor_double(ply.posY - 0.20000000298023224D - ply.yOffset);
			int zz = MathHelper.floor_double(ply.posZ);
			
			World world = this.mod.manager().getMinecraft().theWorld;
			
			int block = world.getBlockId(xx, yy, zz);
			if (block == 0)
			{
				int mm = world.func_85175_e(xx, yy - 1, zz);
				
				if (mm == 11 || mm == 32 || mm == 21)
				{
					block = world.getBlockId(xx, yy - 1, zz);
				}
			}
			if (block > 0)
			{
				ply.playStepSound(xx, yy, zz, block);
				
				float randomPitch = 1f + new Random().nextFloat() * range * 2 - range;
				this.mod.manager().getMinecraft().theWorld.playSound(
					ply.posX, ply.posY + 100d, ply.posZ, "ccb_sounds.hoof_relaxed_one", 7f, randomPitch, false);
			}
			
			this.dmwBase = ply.distanceWalkedModified;
			
			this.hoof = (this.hoof + 1) % 4;
		}
		
	}
	
}
