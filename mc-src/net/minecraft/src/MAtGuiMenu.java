package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

public class MAtGuiMenu extends GuiScreen
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
	
	private int pageFromZero;
	private final int IDS_PER_PAGE = 6;
	
	private static int in_memory_page = 0;
	
	public MAtGuiMenu(GuiScreen par1GuiScreen, MAtMod matmos)
	{
		this(par1GuiScreen, matmos, in_memory_page);
	}
	
	public MAtGuiMenu(GuiScreen par1GuiScreen, MAtMod matmos, int pageFromZero)
	{
		this.screenTitle = "Expansions";
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		this.matmos = matmos;
		this.pageFromZero = pageFromZero;
		
		in_memory_page = this.pageFromZero;
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		StringTranslate stringtranslate = StringTranslate.getInstance();
		int leftHinge = this.width / 2 - 155;
		
		Map<String, MAtExpansion> expansions = this.matmos.getExpansionLoader().getExpansions();
		int id = 0;
		
		{
			final MAtSoundManagerMaster central = this.matmos.getSoundManagerMaster();
			String display = "Global Volume Control: " + (int) Math.floor(central.getVolume() * 100) + "%";
			
			HGuiSliderControl sliderControl =
				new HGuiSliderControl(id, leftHinge, 22 + 22 * id, 310, 20, display, central.getVolume() * 0.5f);
			sliderControl.setListener(new HSliderListener() {
				@Override
				public void sliderValueChanged(HGuiSliderControl slider, float value)
				{
					central.setVolume(value * 2);
					slider.displayString = "Global Volume Control: " + (int) Math.floor(value * 200) + "%";
					MAtGuiMenu.this.matmos.getConfiguration().setProperty("globalvolume.scale", central.getVolume());
				}
			});
			this.controlList.add(sliderControl);
			id++;
			
		}
		
		List<String> identifiers = new ArrayList<String>(expansions.keySet());
		Collections.sort(identifiers);
		
		for (int indexedIdentifier = this.pageFromZero * this.IDS_PER_PAGE; indexedIdentifier < this.pageFromZero
			* this.IDS_PER_PAGE + this.IDS_PER_PAGE
			&& indexedIdentifier < identifiers.size(); indexedIdentifier++)
		{
			final String uniqueIdentifier = identifiers.get(indexedIdentifier);
			final MAtExpansion expansion = expansions.get(uniqueIdentifier);
			
			String display = expansion.getFriendlyName() + ": " + (int) Math.floor(expansion.getVolume() * 100) + "%";
			if (expansion.getVolume() == 0f)
			{
				display = expansion.getFriendlyName() + " (Disabled)";
			}
			
			HGuiSliderControl sliderControl =
				new HGuiSliderControl(
					id, leftHinge + 22, 22 + 22 * id, 310 - 44, 20, display, expansion.getVolume() * 0.5f);
			sliderControl.setListener(new HSliderListener() {
				@Override
				public void sliderValueChanged(HGuiSliderControl slider, float value)
				{
					expansion.setVolume(value * 2);
					if (value != 0f && !expansion.isRunning())
					{
						expansion.turnOn();
					}
					
					String display =
						expansion.getFriendlyName() + ": " + (int) Math.floor(expansion.getVolume() * 100) + "%";
					if (value == 0f)
					{
						display = display + " (Will be disabled)";
					}
					slider.displayString = display;
					
				}
			});
			this.controlList.add(sliderControl);
			id++;
			
		}
		
		this.controlList.add(new GuiButton(200, leftHinge + 22 + 120 + 5, this.height / 6 + 168, 140, 20, "Save"));
		this.controlList.add(new GuiButton(210, leftHinge + 22, this.height / 6 + 168, 120, 20, this.matmos
			.isStartEnabled() ? "Start Enabled: ON" : "Start Enabled: OFF"));
		if (this.pageFromZero != 0)
		{
			this.controlList.add(new GuiButton(201, leftHinge, this.height / 6 + 168 - 22, 150, 20, stringtranslate
				.translateKey("Previous")));
		}
		if (this.pageFromZero * this.IDS_PER_PAGE + this.IDS_PER_PAGE < identifiers.size())
		{
			this.controlList.add(new GuiButton(
				202, leftHinge + 160, this.height / 6 + 168 - 22, 150, 20, stringtranslate.translateKey("Next")));
		}
		
		//this.screenTitle = stringtranslate.translateKey("controls.title");
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
			Map<String, MAtExpansion> expansions = this.matmos.getExpansionLoader().getExpansions();
			for (MAtExpansion expansion : expansions.values())
			{
				if (expansion.getVolume() == 0f && expansion.isRunning())
				{
					expansion.turnOff();
					
				}
				
			}
			this.mc.displayGuiScreen(this.parentScreen);
			
			this.matmos.saveConfig();
			for (MAtExpansion expansion : expansions.values())
			{
				expansion.saveConfig();
				
			}
		}
		else if (par1GuiButton.id == 201)
		{
			this.mc.displayGuiScreen(new MAtGuiMenu(this.parentScreen, this.matmos, this.pageFromZero - 1));
		}
		else if (par1GuiButton.id == 202)
		{
			this.mc.displayGuiScreen(new MAtGuiMenu(this.parentScreen, this.matmos, this.pageFromZero + 1));
		}
		else if (par1GuiButton.id == 210)
		{
			this.matmos.setStartEnabled(!this.matmos.isStartEnabled());
			par1GuiButton.displayString = this.matmos.isStartEnabled() ? "Start Enabled: ON" : "Start Enabled: OFF";
			this.matmos.saveConfig();
		}
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
	
}
