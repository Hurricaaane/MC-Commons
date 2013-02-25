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
	protected void elementClicked(int elementId, boolean doubleClicked)
	{
		if (elementId < getSize())
		{
			this.menu.setSelected(elementId);
		}
		
		if (doubleClicked)
		{
			this.menu.setSelected(elementId);
			this.menu.toggleSelectedPack();
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
				+ pack.getPrettyName(), x + 20, y, 0xFFFFFF);
			this.menu.drawString(this.menu.fontRenderer, pack.getLocationPrintName(), x + 20, y + 10, 0x404040);
			
			this.menu.drawString(
				this.menu.fontRenderer, pack.getMadeForVersion(),
				x + 205 - this.menu.fontRenderer.getStringWidth(pack.getMadeForVersion()), y + 10, 0x404040);
			
			String status = pack.isActive() ? "ON" : "OFF";
			int statusColor = pack.isActive() ? 0x0080FF : 0xC00000;
			int statusWidth = this.menu.fontRenderer.getStringWidth(status);
			this.menu.drawString(this.menu.fontRenderer, status, x + 205 - statusWidth, y, statusColor);
			
			String priority = id + 1 + ".";
			int priorityColor = pack.isActive() ? 0xE0A040 : 0xC0C0C0;
			int priorityWidth = this.menu.fontRenderer.getStringWidth(priority);
			this.menu.drawString(this.menu.fontRenderer, priority, x + 16 - priorityWidth, y, priorityColor);
			
			if (this.mouseX >= x && this.mouseY >= y && this.mouseX <= x + 215 && this.mouseY <= y + ddd)
			{
				if (!pack.getDescription().equals("") || !pack.getDescription().equals(""))
				{
					String tip = pack.getDescription();
					
					if (!pack.getAuthor().equals(""))
					{
						tip = tip + " (" + pack.getAuthor() + ")";
					}
					
					tip = tip.trim();
					
					this.menu.inputTip(tip);
					
				}
			}
			
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
	}
}
