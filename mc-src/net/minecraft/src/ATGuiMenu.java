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

public class ATGuiMenu extends GuiScreen
{
	/**
	 * A reference to the screen object that created this. Used for navigating
	 * between screens.
	 */
	private GuiScreen parentScreen;
	private ATGuiSlotPack packSlotContainer;
	
	/** The title string that is displayed in the top-center of the screen. */
	protected String screenTitle;
	
	private ATHaddon mod;
	
	/** The ID of the button that has been pressed. */
	private int buttonId;
	
	private int selectedSlot;
	private String tip;
	private GuiButton packEnable;
	private GuiButton packUp;
	private GuiButton packDown;
	
	public ATGuiMenu(GuiScreen par1GuiScreen, ATHaddon haddon)
	{
		this.selectedSlot = -1;
		
		this.screenTitle = "Audiotori";
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		this.mod = haddon;
		
		// Ensure the Pack Manager is cached for things to display on
		if (!this.mod.getPackManager().isCached())
		{
			this.mod.getPackManager().cacheAllPacks();
		}
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		this.packSlotContainer = new ATGuiSlotPack(this);
		
		final int _GAP = 2;
		final int _UNIT = 20;
		final int _WIDTH = 155 * 2;
		
		final int _MIX = _GAP + _UNIT;
		
		final int _LEFT = this.width / 2 - _WIDTH / 2;
		final int _RIGHT = this.width / 2 + _WIDTH / 2;
		final int _ADD = 0;
		
		final int _SEPARATOR = 4;
		final int _HEIGHT = this.height;
		
		final int _TURNOFFWIDTH = _WIDTH / 5;
		final int _UPDOWNWIDTH = _WIDTH / 10;
		
		this.packEnable =
			new GuiButton(220, _LEFT + _MIX + _ADD, _HEIGHT - _SEPARATOR - _MIX * 2, _TURNOFFWIDTH, _UNIT, "Enable");
		this.packUp =
			new GuiButton(
				221, _LEFT + _ADD + _MIX + _TURNOFFWIDTH + _GAP, _HEIGHT - _SEPARATOR - _MIX * 2, _UPDOWNWIDTH, _UNIT,
				"Up");
		this.packDown =
			new GuiButton(222, _LEFT + _ADD + _MIX + _TURNOFFWIDTH + _GAP + (_GAP + _UPDOWNWIDTH) * 1, _HEIGHT
				- _SEPARATOR - _MIX * 2, _UPDOWNWIDTH, _UNIT, "Down");
		
		this.controlList.add(this.packEnable);
		this.controlList.add(this.packUp);
		this.controlList.add(this.packDown);
		
		this.packEnable.enabled = false;
		this.packUp.enabled = false;
		this.packDown.enabled = false;
		
		this.controlList.add(new GuiButton(
			210, _RIGHT - _TURNOFFWIDTH - _MIX, _HEIGHT - _SEPARATOR - _MIX * 2, _TURNOFFWIDTH, _UNIT, this.mod
				.getConfig().getBoolean("start.enabled") ? "Start: ON" : "Start: OFF"));
		
		this.controlList.add(new GuiButton(200, _LEFT + _MIX, _HEIGHT - _SEPARATOR - _MIX * 1, _WIDTH
			- _MIX * 2 - _GAP - _TURNOFFWIDTH, _UNIT, "Done"));
		
		this.controlList.add(new GuiButton(
			212, _RIGHT - _TURNOFFWIDTH - _MIX, _HEIGHT - _SEPARATOR - _MIX * 1, _TURNOFFWIDTH, _UNIT, "ON/OFF"));
		
		//this.screenTitle = stringtranslate.translateKey("controls.title");
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.id == 200)
		{
			// This triggers onGuiClosed
			this.mc.displayGuiScreen(this.parentScreen);
		}
		else if (button.id == 210)
		{
			boolean newEnabledState = !this.mod.getConfig().getBoolean("start.enabled");
			this.mod.getConfig().setProperty("start.enabled", newEnabledState);
			button.displayString = newEnabledState ? "Start: ON" : "Start: OFF";
			this.mod.saveConfig();
		}
		else if (button.id == 220)
		{
			toggleSelectedPack();
		}
		else if (button.id == 221)
		{
			moveSelectedPack(true);
		}
		else if (button.id == 222)
		{
			moveSelectedPack(false);
		}
		else if (button.id == 212)
		{
			boolean isActivated = this.mod.getPackManager().isActivated();
			if (!isActivated)
			{
				this.mod.getPackManager().cacheAndActivate(false);
			}
			else
			{
				this.mod.getPackManager().deactivate(false);
			}
		}
		else
		{
			this.packSlotContainer.actionPerformed(button);
		}
	}
	
	@Override
	public void onGuiClosed()
	{
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
		boolean isActivated = this.mod.getPackManager().isActivated();
		int titleWidth = this.fontRenderer.getStringWidth(this.screenTitle);
		
		this.tip = null;
		this.packSlotContainer.drawScreen(par1, par2, par3);
		drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2 - titleWidth / 4, 8, isActivated
			? 0xffffff : 0xD0D0D0);
		
		if (this.mod.canFunction())
		{
			this.fontRenderer.drawStringWithShadow(
				isActivated ? "ON" : "OFF", this.width / 2 + titleWidth / 4 + 10, 8, isActivated ? 0x0080FF : 0xC00000);
		}
		else
		{
			this.fontRenderer.drawStringWithShadow(
				"Minecraft sound is OFF", this.width / 2 + titleWidth / 4 + 10, 8, 0xFFFF00);
		}
		
		drawCenteredString(this.fontRenderer, "Top layer (overrides)", this.width / 2, 20, 0xA0A0A0);
		drawCenteredString(this.fontRenderer, "Bottom layer (compliants)", this.width / 2, this.height - 60, 0xA0A0A0);
		
		super.drawScreen(par1, par2, par3);
		
		if (this.tip != null)
		{
			displayFloatingNote(this.tip, par1, par2);
		}
	}
	
	private void displayFloatingNote(String tipContents, int par2, int par3)
	{
		if (tipContents != null)
		{
			int var5 = par3 - 12;
			int var6 = this.fontRenderer.getStringWidth(tipContents);
			int var4 = par2 /*- var6 / 2*/- 4;
			
			int computedX = var4;
			int computedWidth = var6 + 3;
			int computedXmost = computedX + computedWidth;
			if (computedX < 0)
			{
				computedX = 3;
			}
			else if (computedXmost > this.width)
			{
				computedX = computedX - computedXmost + this.width;
			}
			drawGradientRect(computedX - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
			this.fontRenderer.drawStringWithShadow(tipContents, computedX, var5, -1);
		}
	}
	
	public int getSelectedSlot()
	{
		return this.selectedSlot;
	}
	
	public void setSelected(int elementId)
	{
		this.selectedSlot = elementId;
		updateUpDownButtons();
	}
	
	public void moveSelectedPack(boolean prioritize)
	{
		if (this.selectedSlot < 0 || this.selectedSlot >= getSize())
			return;
		
		if (prioritize && this.selectedSlot >= 1)
		{
			this.mod.getPackManager().swapDuetAt(this.selectedSlot - 1);
			setSelected(this.selectedSlot - 1);
			updateUpDownButtons();
		}
		else if (!prioritize && this.selectedSlot + 1 < getSize())
		{
			this.mod.getPackManager().swapDuetAt(this.selectedSlot);
			setSelected(this.selectedSlot + 1);
			updateUpDownButtons();
		}
		
	}
	
	public void toggleSelectedPack()
	{
		if (this.selectedSlot < 0 || this.selectedSlot >= getSize())
			return;
		
		ATPack pack = getPack(this.selectedSlot);
		this.mod.getPackManager().changePackStateAndSave(pack.getSysName(), !pack.isActive());
		updateUpDownButtons();
		
	}
	
	public void updateUpDownButtons()
	{
		if (this.selectedSlot < 0 || this.selectedSlot >= getSize())
		{
			this.packEnable.enabled = false;
			this.packUp.enabled = false;
			this.packDown.enabled = false;
			this.packEnable.displayString = "Enable";
		}
		else
		{
			this.packEnable.enabled = true;
			this.packUp.enabled = true;
			this.packDown.enabled = true;
			
			this.packEnable.displayString = getPack(this.selectedSlot).isActive() ? "Disable" : "Enable";
			
			if (this.selectedSlot == 0)
			{
				this.packUp.enabled = false;
			}
			if (this.selectedSlot == getSize() - 1)
			{
				this.packDown.enabled = false;
			}
		}
		
	}
	
	@Override
	protected void keyTyped(char par1, int par2)
	{
		if (isShiftKeyDown() && par2 == 200)
		{
			moveSelectedPack(true);
		}
		else if (isShiftKeyDown() && par2 == 208)
		{
			moveSelectedPack(false);
		}
		else if (par1 == ' ')
		{
			toggleSelectedPack();
		}
		else if (par2 == 1)
		{
			this.mc.displayGuiScreen(this.parentScreen);
		}
	}
	
	public int getSize()
	{
		return this.mod.getPackManager().getPackCount();
	}
	
	public ATPack getPack(int id)
	{
		return this.mod.getPackManager().getPack(id);
	}
	
	public String inputTip(String tip)
	{
		return this.tip = tip;
	}
	
}
