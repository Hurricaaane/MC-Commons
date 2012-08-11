package eu.ha3.mc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;

import org.lwjgl.opengl.GL11;

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

public class HGuiSliderControl extends GuiButton
{
	protected float value = 1.0F;
	protected boolean isBeingDragged = false;
	
	protected HSliderListener listener;
	
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
	
	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over
	 * this button and 2 if it IS hovering over this button.
	 */
	@Override
	protected int getHoverState(boolean par1)
	{
		return 0;
	}
	
	/**
	 * Fired when the mouse button is dragged. Equivalent of
	 * MouseListener.mouseDragged(MouseEvent e).
	 */
	@Override
	protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3)
	{
		if (this.drawButton)
		{
			if (this.isBeingDragged)
			{
				float value = (float) (par2 - (this.xPosition + 4)) / (this.width - 8);
				
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
					this.listener.sliderValueChanged(this.id, value);
				}
				
			}
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(
				this.xPosition + (int) (this.value * (this.width - 8)), this.yPosition, 0, 66, 4, this.height);
			drawTexturedModalRect(
				this.xPosition + (int) (this.value * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, this.height);
		}
	}
	
	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of
	 * MouseListener.mousePressed(MouseEvent e).
	 */
	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
	{
		if (super.mousePressed(par1Minecraft, par2, par3))
		{
			float value = (float) (par2 - (this.xPosition + 4)) / (this.width - 8);
			
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
				this.listener.sliderValueChanged(this.id, value);
			}
			
			this.isBeingDragged = true;
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Fired when the mouse button is released. Equivalent of
	 * MouseListener.mouseReleased(MouseEvent e).
	 */
	@Override
	public void mouseReleased(int par1, int par2)
	{
		this.isBeingDragged = false;
	}
}
