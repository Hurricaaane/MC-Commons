package net.minecraft.src;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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

public class DVGuiCapture
{
	private DVRenderItem itemRenderer;
	private Minecraft mc;
	private FontRenderer fontRenderer;
	
	public DVGuiCapture(Minecraft mc)
	{
		this.mc = mc;
		this.itemRenderer = new DVRenderItem();
		this.fontRenderer = this.mc.fontRenderer;
	}
	
	public void render(ItemStack stack)
	{
		int a = 0;
		int b = 0;
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		this.itemRenderer.zLevel = 0F;
		
		GL11.glPushMatrix();
		
		float fscale = 12F;
		GL11.glScalef(fscale, fscale, fscale);
		
		this.itemRenderer.drawItemIntoGui(
			this.fontRenderer, this.mc.renderEngine, stack.itemID, stack.getItemDamage(), stack.getIconIndex(), a, b);
		GL11.glPopMatrix();
		
		//this.itemRenderer.doRenderItem(stack., 0d, 0d, 64d, 64f, 0f);
		//this.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, stack, a, b);
		//this.itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, stack, a, b);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
		
	}
}
