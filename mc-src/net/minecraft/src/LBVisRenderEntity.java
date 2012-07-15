package net.minecraft.src;

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

public class LBVisRenderEntity extends Entity
{
	private LBVisHaddon haddon;
	
	public LBVisRenderEntity(LBVisHaddon haddon, World par1World)
	{
		super(par1World);
		
		this.haddon = haddon;
		
		this.ignoreFrustumCheck = true;
		
	}
	
	@Override
	public void onEntityUpdate()
	{
		EntityPlayer ply = this.haddon.getManager().getMinecraft().thePlayer;
		
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
