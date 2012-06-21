package net.minecraft.src;

public class LBVisRenderHooks extends Render
{
	private LBVisHaddon haddon;
	
	public LBVisRenderHooks(LBVisHaddon haddon)
	{
		this.haddon = haddon;
	}
	
	@Override
	public void doRender(Entity entity, double d, double d1, double d2,
			float f, float f1)
	{
		haddon.render(d, d1, d2, f, f1);
		
	}
	
}
