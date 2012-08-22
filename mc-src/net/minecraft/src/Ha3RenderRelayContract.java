package net.minecraft.src;

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

public abstract class Ha3RenderRelayContract
{
	private Minecraft mc;
	
	private WorldClient lastWorld;
	private EntityClientPlayerMP lastPlayer;
	
	private Entity renderEntity;
	private Render renderHook;
	
	public Ha3RenderRelayContract(Minecraft mc)
	{
		this.mc = mc;
		this.renderHook = new HRenderHook();
		
	}
	
	public boolean ensureExists()
	{
		if (this.mc.theWorld == null || this.mc.thePlayer == null)
			return false;
		
		if (this.mc.theWorld != this.lastWorld || this.mc.thePlayer != this.lastPlayer)
		{
			this.renderEntity = newRenderEntity();
			this.renderEntity.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ);
			this.mc.theWorld.spawnEntityInWorld(this.renderEntity);
			this.renderEntity.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ);
			
			this.lastWorld = this.mc.theWorld;
			this.lastPlayer = this.mc.thePlayer;
			
			return true;
		}
		
		return false;
	}
	
	public Render getRenderHook()
	{
		return this.renderHook;
	}
	
	private class HRenderHook extends Render
	{
		@Override
		public void doRender(Entity entity, double qx, double qy, double qz, float f, float semi)
		{
			double dx = entity.posX * semi + entity.lastTickPosX * (1 - semi);
			double dy = entity.posY * semi + entity.lastTickPosY * (1 - semi);
			double dz = entity.posZ * semi + entity.lastTickPosZ * (1 - semi);
			
			Ha3RenderRelayContract.this.doRender(entity, dx, dy, dz, f, semi);
			
		}
		
	}
	
	protected class HRenderEntity extends Entity
	{
		public HRenderEntity()
		{
			super(Ha3RenderRelayContract.this.mc.theWorld);
			
			this.ignoreFrustumCheck = true;
		}
		
		@Override
		public void onEntityUpdate()
		{
			EntityPlayer ply = Ha3RenderRelayContract.this.mc.thePlayer;
			setPosition(ply.posX, ply.posY, ply.posZ);
			
		}
		
		@Override
		protected void entityInit()
		{
		}
		
		@Override
		protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
		{
		}
		
		@Override
		protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
		{
		}
	}
	
	@SuppressWarnings("rawtypes")
	public abstract Class getRenderEntityClass();
	
	public abstract Entity newRenderEntity();
	
	public abstract void doRender(Entity entity, double dx, double dy, double dz, float f, float semi);
	
}
