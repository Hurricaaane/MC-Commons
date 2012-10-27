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
		super(menu.mc, menu.width, menu.height, 32, menu.height - 64, 36);
		this.menu = menu;
	}
	
	@Override
	protected int getSize()
	{
		return 1;
	}
	
	@Override
	protected void elementClicked(int elementId, boolean var2)
	{
		if (elementId < getSize())
		{
			this.menu.setSelected(elementId);
		}
	}
	
	@Override
	protected boolean isSelected(int var1)
	{
		return var1 == this.menu.getSelectedSlot();
	}
	
	@Override
	protected void drawBackground()
	{
		this.menu.drawDefaultBackground();
	}
	
	@Override
	protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5)
	{
	}
}
