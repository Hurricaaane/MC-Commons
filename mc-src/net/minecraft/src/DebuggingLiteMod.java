package net.minecraft.src;

import com.mumfrey.liteloader.RenderListener;

public class DebuggingLiteMod implements RenderListener
{
	@Override
	public String getName()
	{
		return "fff";
	}
	
	@Override
	public String getVersion()
	{
		return "1.4.6";
	}
	
	@Override
	public void init()
	{
		System.out.println("lololo");
		
	}
	
	@Override
	public void onRender()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onRenderGui(GuiScreen currentScreen)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onRenderWorld()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSetupCameraTransform()
	{
		// TODO Auto-generated method stub
		
	}
	
}
