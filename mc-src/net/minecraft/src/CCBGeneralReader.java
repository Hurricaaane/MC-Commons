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
	protected double yPosition;
	protected float dwmYChange;
	
	protected boolean isFlying;
	protected long airborneTime;
	protected long immobileTime;
	protected float fallDistance;
	
	public CCBGeneralReader(CCBHaddon mod)
	{
		this.mod = mod;
	}
	
	@Override
	public void frame(EntityPlayer ply)
	{
		simulateHoofsteps(ply);
		simulateWings(ply);
		
	}
	
	protected void simulateWings(EntityPlayer ply)
	{
		double xpd = ply.motionX * ply.motionX + ply.motionZ * ply.motionZ;
		float speed = (float) Math.sqrt(xpd);
		float volumetricSpeed = (float) Math.sqrt(xpd + ply.motionY * ply.motionY);
		
		if ((ply.onGround || ply.isOnLadder()) == this.isFlying)
		{
			this.isFlying = !this.isFlying;
			if (this.isFlying)
			{
				this.airborneTime = System.currentTimeMillis() + 700;
			}
			
			if (!this.isFlying && this.fallDistance > 3f || speed > 0.2f)
			{
				float volume = 0.3f;
				if (speed <= 0.2f)
				{
					volume = 0.1f + 0.4f * scalex(this.fallDistance, 3f, 20f);
				}
				
				if (!this.isFlying)
				{
					this.mod.manager().getMinecraft().theWorld.playSound(
						ply.posX, ply.posY, ply.posZ, "ccb_sounds.airborne_land", volume, randomPitch(1f, 0.2f), false);
				}
				else
				{
					this.mod.manager().getMinecraft().theWorld.playSound(
						ply.posX, ply.posY, ply.posZ, "ccb_sounds.pegasus_dashing", volume, randomPitch(1f, 0.1f),
						false);
				}
				
			}
		}
		
		// Fall distance is used by non-pegasi
		if (this.isFlying)
		{
			if (volumetricSpeed != 0)
			{
				this.immobileTime = System.currentTimeMillis();
			}
			this.fallDistance = ply.fallDistance;
		}
		
		// Only play wing sounds if pegasus
		if (this.isPegasus && this.isFlying && System.currentTimeMillis() > this.airborneTime)
		{
			int period = 400;
			if (volumetricSpeed > 0.2f)
			{
				period = (int) (period - 150 * scalex(volumetricSpeed, 0.2f, 0.3f));
				
			}
			
			this.airborneTime = System.currentTimeMillis() + period;
			
			float volume = 0.5f;
			long diffImmobile = System.currentTimeMillis() - this.immobileTime;
			if (System.currentTimeMillis() - this.immobileTime > 20000)
			{
				volume = 0.5f * (1f - scalex(diffImmobile, 20000, 20000));
			}
			
			if (volume > 0f)
			{
				this.mod.manager().getMinecraft().theWorld.playSound(
					ply.posX, ply.posY, ply.posZ, "ccb_sounds.wing_flap", volume, randomPitch(1f, 0.05f), false);
			}
			
		}
		
	}
	
	protected void simulateHoofsteps(EntityPlayer ply)
	{
		final float distanceReference = ply.field_82151_R;
		//System.out.println(distanceReference);
		if (this.dmwBase > distanceReference)
		{
			this.dmwBase = 0;
			this.dwmYChange = 0;
		}
		
		float dwm = distanceReference - this.dmwBase;
		
		float speed = (float) Math.sqrt(ply.motionX * ply.motionX + ply.motionZ * ply.motionZ);
		float distance = 0.65f;
		float volume = 0.1f;
		
		if (ply.isOnLadder())
		{
			distance = 0.4f;
		}
		else if (Math.abs(this.yPosition - ply.posY) > 0.4d)
		{
			// Regular stance on staircases (1-1-1-1-)
			distance = 0.01f;
			this.dwmYChange = distanceReference;
			
		}
		else if (speed > 0.13f)
		{
			// Gallop stance (1-1-2--)
			if (this.hoof == 3)
			{
				distance = 0.7f;
			}
			else if (this.hoof == 2)
			{
				distance = 0.1f;
			}
			else
			{
				distance = 0.25f;
			}
		}
		else if (speed > 0.08f)
		{
			// Walking stance (2-2-)
			// Prevent the 2-2 steps from happening on staircases
			if (distanceReference - this.dwmYChange > 1f)
			{
				if (this.hoof % 2 == 0)
				{
					distance = distance / 7f;
				}
			}
			
		}
		else
		{
			// Slow stance (1--1--1--1--)
			distance = 0.75f;
			volume = volume * speed / 0.08f;
		}
		
		if (dwm > distance)
		{
			int xx = MathHelper.floor_double(ply.posX);
			int yy = MathHelper.floor_double(ply.posY - 0.2d - ply.yOffset);
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
				
				if (!ply.isOnLadder())
				{
					this.mod.manager().getMinecraft().theWorld.playSound(
						ply.posX, ply.posY, ply.posZ, "ccb_sounds.hoof_relaxed_one", volume, randomPitch(1f, 0.1f),
						false);
				}
			}
			
			this.dmwBase = distanceReference;
			
			this.hoof = (this.hoof + 1) % 4;
		}
		
		this.yPosition = ply.posY;
		
	}
	
	private float scalex(float number, float min, float range)
	{
		float m = (number - min) / range;
		if (m < 0f)
			return 0f;
		if (m > 1f)
			return 1f;
		
		return m;
		
	}
	
	private float randomPitch(float base, float radius)
	{
		return base + new Random().nextFloat() * radius * 2 - radius;
		
	}
	
	@Override
	public void setVariator(CCBVariator var)
	{
		// TODO Auto-generated method stub
		
	}
	
}