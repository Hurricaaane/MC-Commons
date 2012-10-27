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

public class ATGuiSlotPack extends GuiSlot
{
	private ATGuiMenu menu;
	
	public ATGuiSlotPack(ATGuiMenu menu)
	{
		super(menu.mc, menu.width, menu.height, 32, menu.height - 64, 22);
		this.menu = menu;
	}
	
	@Override
	protected int getSize()
	{
		return this.menu.getSize();
	}
	
	@Override
	protected void elementClicked(int elementId, boolean unknown)
	{
		if (elementId < getSize())
		{
			this.menu.setSelected(elementId);
		}
	}
	
	@Override
	protected boolean isSelected(int id)
	{
		return id == this.menu.getSelectedSlot();
	}
	
	@Override
	protected void drawBackground()
	{
		this.menu.drawDefaultBackground();
	}
	
	@Override
	protected void drawSlot(int id, int x, int y, int ddd, Tessellator tesselator)
	{
		try
		{
			ATPack pack = this.menu.getPack(id);
			
			this.menu.drawString(this.menu.fontRenderer, (pack.getPrettyName() == pack.getSysName() ? "\u00A7o" : "")
				+ pack.getPrettyName(), x + 1, y, 0xFFFFFF);
			this.menu.drawString(this.menu.fontRenderer, pack.getSysName() + "/", x + 1, y + 10, 0x404040);
			
			String status = pack.isActive() ? "ON" : "OFF";
			int statusColor = pack.isActive() ? 0x0080FF : 0xC00000;
			int statusWidth = this.menu.fontRenderer.getStringWidth(status);
			this.menu.drawString(this.menu.fontRenderer, status, x + 215 - statusWidth, y, statusColor);
			
			/*if (getSize() > 1)
			{
				if (this.mouseX >= x && this.mouseY >= y && this.mouseX <= x + 215 && this.mouseY <= y + ddd)
				{
					if (id == 0)
					{
						this.menu.inputTip("Top layer: May override sounds from other packs.");
					}
					else if (id == getSize() - 1)
					{
						this.menu.inputTip("Bottom layer: Layers above may override this sound pack.");
					}
				}
			}*/
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
	}
}
