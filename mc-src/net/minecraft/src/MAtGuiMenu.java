package net.minecraft.src;

import java.util.Map;
import java.util.Map.Entry;

import eu.ha3.mc.gui.HGuiSliderControl;
import eu.ha3.mc.gui.HSliderListener;

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

public class MAtGuiMenu extends GuiScreen implements HSliderListener
{
	/**
	 * A reference to the screen object that created this. Used for navigating
	 * between screens.
	 */
	private GuiScreen parentScreen;
	
	/** The title string that is displayed in the top-center of the screen. */
	protected String screenTitle;
	
	private MAtMod matmos;
	
	/** The ID of the button that has been pressed. */
	private int buttonId;
	
	public MAtGuiMenu(GuiScreen par1GuiScreen, MAtMod matmos)
	{
		this.screenTitle = "Expansions";
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		this.matmos = matmos;
	}
	
	private int func_20080_j()
	{
		return this.width / 2 - 155;
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		StringTranslate stringtranslate = StringTranslate.getInstance();
		int i = func_20080_j();
		
		Map<String, MAtExpansion> expansions = this.matmos.getExpansionLoader().getExpansions();
		int j = 0;
		
		for (Entry<String, MAtExpansion> expansion : expansions.entrySet())
		{
			HGuiSliderControl gsc =
				new HGuiSliderControl(
					j, i + j % 2 * 160, this.height / 12 + 16 * (j >> 1), expansion.getKey(), expansion
						.getValue().getVolume());
			gsc.setListener(this);
			this.controlList.add(gsc);
			j++;
			
		}
		
		this.controlList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, stringtranslate
			.translateKey("gui.done")));
		this.screenTitle = stringtranslate.translateKey("controls.title");
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.id == 200)
		{
			this.mc.displayGuiScreen(this.parentScreen);
		}
		/*else
		{
			this.buttonId = par1GuiButton.id;
			
			GuiSlider slider = (GuiSlider) par1GuiButton;
			this.matmos.expansionLoader().getExpansions().get(slider.displayString).setVolume(slider.value);
		}*/
	}
	
	/**
	 * Called when the mouse is clicked.
	 */
	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		if (this.buttonId >= 0)
		{
		}
		else
		{
			super.mouseClicked(par1, par2, par3);
		}
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 5, 0xffffff);
		
		super.drawScreen(par1, par2, par3);
		
	}
	
	@Override
	public void sliderValueChanged(int id, float value)
	{
		System.out.println("value = " + value);
		this.matmos
			.getExpansionLoader().getExpansions().get(((HGuiSliderControl) this.controlList.get(id)).displayString)
			.setVolume(value);
		System.out.println(this.matmos
			.getExpansionLoader().getExpansions().get(((HGuiSliderControl) this.controlList.get(id)).displayString)
			.getVolume());
		
	}
	
}
