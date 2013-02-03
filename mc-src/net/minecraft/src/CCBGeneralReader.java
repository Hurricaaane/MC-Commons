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
	
	protected CCBVariator VAR;
	
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
		this.VAR = new CCBVariator();
	}
	
	@Override
	public void setVariator(CCBVariator variator)
	{
		this.VAR = variator;
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
				this.airborneTime = System.currentTimeMillis() + this.VAR.WING_JUMPING_REST_TIME;
			}
			
			boolean hugeLanding = !this.isFlying && this.fallDistance > this.VAR.HUGEFALL_LANDING_DISTANCE_MIN;
			boolean speedingJumpStateChange = speed > this.VAR.GROUND_AIR_STATE_SPEED;
			
			if (hugeLanding || speedingJumpStateChange)
			{
				float volume = this.VAR.GROUND_AIR_STATE_CHANGE_VOLUME;
				
				// If the pegasus has landed 
				if (hugeLanding)
				{
					volume =
						this.VAR.HUGEFALL_LANDING_VOLUME_MIN
							+ (this.VAR.HUGEFALL_LANDING_VOLUME_MAX - this.VAR.HUGEFALL_LANDING_VOLUME_MIN)
							* scalex(
								this.fallDistance, this.VAR.HUGEFALL_LANDING_DISTANCE_MIN,
								this.VAR.HUGEFALL_LANDING_DISTANCE_MAX - this.VAR.HUGEFALL_LANDING_DISTANCE_MIN);
					if (speedingJumpStateChange && volume < this.VAR.GROUND_AIR_STATE_CHANGE_VOLUME)
					{
						volume = this.VAR.GROUND_AIR_STATE_CHANGE_VOLUME;
					}
				}
				
				if (!this.isFlying)
				{
					this.mod.manager().getMinecraft().theWorld.playSound(
						ply.posX, ply.posY, ply.posZ, "ccb_sounds.land", volume,
						randomPitch(1f, this.VAR.LANDING_PITCH_RADIUS), false);
				}
				else
				{
					this.mod.manager().getMinecraft().theWorld.playSound(
						ply.posX, ply.posY, ply.posZ, "ccb_sounds.dash", volume,
						randomPitch(1f, this.VAR.DASHING_PITCH_RADIUS), false);
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
			int period = this.VAR.WING_SLOW;
			if (volumetricSpeed > this.VAR.WING_SPEED_MIN)
			{
				period =
					(int) (period - (this.VAR.WING_SLOW - this.VAR.WING_FAST)
						* scalex(volumetricSpeed, this.VAR.WING_SPEED_MIN, this.VAR.WING_SPEED_MAX));
			}
			
			this.airborneTime = System.currentTimeMillis() + period;
			
			float volume = this.VAR.WING_VOLUME;
			long diffImmobile = System.currentTimeMillis() - this.immobileTime;
			if (System.currentTimeMillis() - this.immobileTime > this.VAR.WING_IMMOBILE_FADE_START)
			{
				volume =
					volume
						* (1f - scalex(
							diffImmobile, this.VAR.WING_IMMOBILE_FADE_START, this.VAR.WING_IMMOBILE_FADE_DURATION));
			}
			
			if (volume > 0f)
			{
				this.mod.manager().getMinecraft().theWorld.playSound(
					ply.posX, ply.posY, ply.posZ, "ccb_sounds.wing", volume,
					randomPitch(1f, this.VAR.WING_PITCH_RADIUS), false);
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
		float distance = this.VAR.WALK_DISTANCE;
		float volume = this.VAR.WALK_VOLUME;
		
		if (ply.isOnLadder())
		{
			volume = this.VAR.LADDER_VOLUME;
			distance = this.VAR.LADDER_DISTANCE;
		}
		else if (Math.abs(this.yPosition - ply.posY) > 0.4d)
		{
			// Regular stance on staircases (1-1-1-1-)
			volume = this.VAR.STAIRCASE_VOLUME;
			distance = this.VAR.STAIRCASE_DISTANCE;
			this.dwmYChange = distanceReference;
			
		}
		else if (speed > this.VAR.SPEED_TO_GALLOP)
		{
			volume = this.VAR.GALLOP_VOLUME;
			// Gallop stance (1-1-2--)
			if (this.hoof == 3)
			{
				distance = this.VAR.GALLOP_DISTANCE_4;
			}
			else if (this.hoof == 2)
			{
				distance = this.VAR.GALLOP_DISTANCE_3;
			}
			else if (this.hoof == 1)
			{
				distance = this.VAR.GALLOP_DISTANCE_2;
			}
			else
			{
				distance = this.VAR.GALLOP_DISTANCE_1;
			}
		}
		else if (speed > this.VAR.SPEED_TO_WALK)
		{
			// Walking stance (2-2-)
			// Prevent the 2-2 steps from happening on staircases
			if (distanceReference - this.dwmYChange > this.VAR.STAIRCASE_ANTICHASE_DIFFERENCE)
			{
				if (this.hoof % 2 == 0)
				{
					//distance = distance / 7f;
					distance = distance * this.VAR.WALK_CHASING_FACTOR;
				}
			}
			
		}
		else
		{
			// Slow stance (1--1--1--1--)
			distance = this.VAR.SLOW_DISTANCE;
			volume = this.VAR.SLOW_VOLUME * speed / this.VAR.SPEED_TO_WALK;
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
				if (this.VAR.PLAY_BLOCKSTEPS)
				{
					ply.playStepSound(xx, yy, zz, block);
				}
				
				volume = volume * this.VAR.HOOF_VOLUME_MULTIPLICATOR;
				if (this.VAR.PLAY_HOOFSTEPS && volume > 0)
				{
					this.mod.manager().getMinecraft().theWorld.playSound(
						ply.posX, ply.posY, ply.posZ, "ccb_sounds.hoofstep", volume,
						randomPitch(1f, this.VAR.HOOF_PITCH_RADIUS), false);
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
	
}
