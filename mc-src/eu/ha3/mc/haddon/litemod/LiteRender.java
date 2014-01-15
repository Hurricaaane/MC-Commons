package eu.ha3.mc.haddon.litemod;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.Render;

import com.mumfrey.liteloader.RenderListener;
import com.mumfrey.liteloader.util.ModUtilities;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.OperatorRenderer;
import eu.ha3.mc.haddon.supporting.SupportsRenderEvents;

/*
--filenotes-placeholder
*/

public class LiteRender extends LiteBase implements OperatorRenderer, RenderListener
{
	public LiteRender(Haddon haddon)
	{
		super(haddon);
	}
	
	@Override
	public void onRender()
	{
		((SupportsRenderEvents) this.haddon).onRender();
	}
	
	@Override
	public void onRenderGui(GuiScreen currentScreen)
	{
		((SupportsRenderEvents) this.haddon).onRenderGui(currentScreen);
	}
	
	@Override
	public void onRenderWorld()
	{
		((SupportsRenderEvents) this.haddon).onRenderWorld();
	}
	
	@Override
	public void onSetupCameraTransform()
	{
		((SupportsRenderEvents) this.haddon).onSetupCameraTransform();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addRenderable(@SuppressWarnings("rawtypes") Class renderClass, Object renderable)
	{
		ModUtilities.addRenderer(renderClass, (Render) renderable);
	}
	
}
