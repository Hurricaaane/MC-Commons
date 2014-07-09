package eu.ha3.mc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

/* x-placeholder-wtfplv2 */

public class HGuiSliderControl extends GuiButton implements HDisplayStringHolder
{
	protected float value = 1.0F;
	protected boolean isBeingDragged = false;
	
	protected HSliderListener listener;
	private HDisplayStringProvider dsProvider;
	
	public HGuiSliderControl(int id, int xPos, int yPos, String label, float value)
	{
		this(id, xPos, yPos, 150, 20, label, value);
	}
	
	public HGuiSliderControl(int id, int xPos, int yPos, int width, int height, String label, float value)
	{
		super(id, xPos, yPos, width, height, label);
		this.value = value;
	}
	
	public void setListener(HSliderListener listener)
	{
		this.listener = listener;
	}
	
	@Override
	public int getHoverState(boolean p_146114_1_)
	{
		return 0;
	}
	
	@Override
	protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3)
	{
		if (this.visible) // drawButton
		{
			int x = this.xPosition;
			int y = this.yPosition;
			int w = this.width;
			int h = this.height;
			
			if (this.isBeingDragged)
			{
				float value = (float) (par2 - (x + 4)) / (w - 8);
				
				if (value < 0.0F)
				{
					value = 0.0F;
				}
				
				if (value > 1.0F)
				{
					value = 1.0F;
				}
				
				if (this.value != value)
				{
					this.value = value;
					this.listener.sliderValueChanged(this, value);
				}
				
			}
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(x + (int) (this.value * (w - 8)), y, 0, 66, 4, h);
			drawTexturedModalRect(x + (int) (this.value * (w - 8)) + 4, y, 196, 66, 4, h);
		}
	}
	
	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
	{
		if (super.mousePressed(par1Minecraft, par2, par3))
		{
			int x = this.xPosition;
			int w = this.width;
			float value = (float) (par2 - (x + 4)) / (w - 8);
			
			if (value < 0.0F)
			{
				value = 0.0F;
			}
			
			if (value > 1.0F)
			{
				value = 1.0F;
			}
			
			if (this.value != value)
			{
				this.value = value;
				this.listener.sliderValueChanged(this, value);
			}
			
			this.isBeingDragged = true;
			this.listener.sliderPressed(this);
			
			return true;
		}
		else
			return false;
	}
	
	@Override
	public void mouseReleased(int p_146118_1_, int p_146118_2_)
	{
		this.isBeingDragged = false;
		
		this.listener.sliderReleased(this);
	}
	
	@Override
	public void updateDisplayString()
	{
		if (this.dsProvider == null)
			return;
		
		this.displayString = this.dsProvider.provideDisplayString();
		
	}
	
	@Override
	public void setDisplayStringProvider(HDisplayStringProvider provider)
	{
		this.dsProvider = provider;
		
	}
}
