package net.minecraft.src;

public class LBVisRenderEntity extends Entity
{
	private LBVisHaddon haddon;
	
	public LBVisRenderEntity(LBVisHaddon haddon, World par1World)
	{
		super(par1World);
		
		this.haddon = haddon;

		ignoreFrustumCheck = true;
		
	}
	
	@Override
	public void onEntityUpdate()
	{
		EntityPlayer ply = haddon.getManager().getMinecraft().thePlayer;
		
		this.setPosition(ply.posX, ply.posY, ply.posZ);
		
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
	}}
