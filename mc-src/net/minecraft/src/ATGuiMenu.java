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
	private GuiButton startEnabledButton;
	private GuiButton turnOnOffButton;
	private GuiButton hintButton;
	private GuiButton playButton;
	private GuiButton stopButton;
	private GuiButton musicButton;
	
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
		
		if (this.mod.getConfig().getBoolean("gui.hints.enabled"))
		{
			this.hintButton =
				new GuiButton(
					211, _RIGHT - _TURNOFFWIDTH * 2 - _MIX - _GAP, _HEIGHT - _SEPARATOR - _MIX * 2, _TURNOFFWIDTH,
					_UNIT, this.mod.getConfig().getBoolean("gui.hints.enabled") ? "Hints: ON" : "Hints: OFF");
		}
		else
		{
			this.hintButton =
				new GuiButton(
					211, _RIGHT - _TURNOFFWIDTH - _MIX - _GAP - _UNIT, _HEIGHT - _SEPARATOR - _MIX * 2, _UNIT, _UNIT,
					"?");
		}
		
		this.startEnabledButton =
			new GuiButton(
				210, _RIGHT - _TURNOFFWIDTH - _MIX, _HEIGHT - _SEPARATOR - _MIX * 2, _TURNOFFWIDTH, _UNIT, this.mod
					.getConfig().getBoolean("start.enabled") ? "Start: ON" : "Start: OFF");
		
		this.musicButton =
			new GuiButton(213, _RIGHT - _TURNOFFWIDTH - _MIX - _GAP * 3 - _UNIT * 2 - _TURNOFFWIDTH, _HEIGHT
				- _SEPARATOR - _MIX * 1, _TURNOFFWIDTH, _UNIT, this.mod.getConfig().getBoolean("stash.music")
				? "BGM: PACKS" : "BGM: ALL");
		
		this.turnOnOffButton =
			new GuiButton(
				212, _RIGHT - _TURNOFFWIDTH - _MIX, _HEIGHT - _SEPARATOR - _MIX * 1, _TURNOFFWIDTH, _UNIT, "ON/OFF");
		
		this.controlList.add(this.hintButton);
		this.controlList.add(this.startEnabledButton);
		
		this.controlList.add(new GuiButton(200, _LEFT + _MIX, _HEIGHT - _SEPARATOR - _MIX * 1, _WIDTH
			- _MIX * 2 - _GAP - _TURNOFFWIDTH - _UNIT * 2 - _GAP * 3 - _TURNOFFWIDTH, _UNIT, "Done"));
		
		this.playButton =
			new GuiButton(
				240, _RIGHT - _TURNOFFWIDTH - _MIX - _GAP * 2 - _UNIT * 2, _HEIGHT - _SEPARATOR - _MIX * 1, _UNIT,
				_UNIT, "\u25b6");
		this.stopButton =
			new GuiButton(
				241, _RIGHT - _TURNOFFWIDTH - _MIX - _GAP - _UNIT, _HEIGHT - _SEPARATOR - _MIX * 1, _UNIT, _UNIT,
				"\u25a0");
		
		this.controlList.add(this.playButton);
		this.controlList.add(this.stopButton);
		this.controlList.add(this.musicButton);
		
		this.controlList.add(this.turnOnOffButton);
		
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
		else if (button.id == 211)
		{
			boolean newHintState = !this.mod.getConfig().getBoolean("gui.hints.enabled");
			this.mod.getConfig().setProperty("gui.hints.enabled", newHintState);
			if (!button.displayString.equals("?"))
			{
				button.displayString = newHintState ? "Hints: ON" : "Hints: OFF";
			}
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
		else if (button.id == 213)
		{
			boolean newStashState = !this.mod.getConfig().getBoolean("stash.music");
			this.mod.getConfig().setProperty("stash.music", newStashState);
			button.displayString = newStashState ? "BGM: PACKS" : "BGM: ALL";
			this.mod.saveConfig();
			this.mod.getPackManager().getSystem().setStashForMusic(newStashState);
			this.mod.getPackManager().applyAllPacks(true);
		}
		else if (button.id == 240)
		{
			this.mod.playMusic();
		}
		else if (button.id == 241)
		{
			this.mod.stopMusic();
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
	public void drawScreen(int mouseX, int mouseY, float par3)
	{
		int numberOfPacks = this.mod.getPackManager().getPackCount();
		boolean isActivated = this.mod.getPackManager().isActivated();
		int titleWidth = this.fontRenderer.getStringWidth(this.screenTitle);
		
		this.tip = null;
		this.packSlotContainer.drawScreen(mouseX, mouseY, par3);
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
		
		if (numberOfPacks == 0)
		{
			drawCenteredString(
				this.fontRenderer, "No sound packs installed! (.minecraft/audiotori/<sound pack>/)", this.width / 2,
				20, 0xC00000);
		}
		else
		{
			drawCenteredString(this.fontRenderer, "Top layer (overrides)", this.width / 2, 20, 0xA0A0A0);
			drawCenteredString(
				this.fontRenderer, "Bottom layer (compliants)", this.width / 2, this.height - 60, 0xA0A0A0);
		}
		
		super.drawScreen(mouseX, mouseY, par3);
		
		if (this.mod.getConfig().getBoolean("gui.hints.enabled"))
		{
			if (isMouseHovering(mouseX, mouseY, this.packEnable))
			{
				inputTip("Enable/Disable selected pack (Shortcut: Double-click or press SPACE)");
			}
			else if (isMouseHovering(mouseX, mouseY, this.packUp))
			{
				inputTip("Increase priority (Shortcut: SHIFT + UP arrow)");
			}
			else if (isMouseHovering(mouseX, mouseY, this.packDown))
			{
				inputTip("Decrease priority (Shortcut: SHIFT + DOWN arrow)");
			}
			else if (isMouseHovering(mouseX, mouseY, this.packDown))
			{
				inputTip("Decrease priority (Shortcut: SHIFT + DOWN arrow)");
			}
			else if (isMouseHovering(mouseX, mouseY, this.startEnabledButton))
			{
				inputTip("Start Enabled: Should Audiotori start when Minecraft starts?");
			}
			else if (isMouseHovering(mouseX, mouseY, this.turnOnOffButton))
			{
				inputTip("Turn On or Off / Reload sound packs from disk");
			}
			else if (isMouseHovering(mouseX, mouseY, this.musicButton))
			{
				inputTip("PACKS: Only play custom music (if there are) / ALL: Play all music");
			}
			else if (isMouseHovering(mouseX, mouseY, this.playButton))
			{
				/*if (this.mod.isMusicPlaying())
				{
					inputTip("Stop playing the current music and roll a new one");
				}
				else
				{*/
				inputTip("Play random music");
				//}
			}
			else if (isMouseHovering(mouseX, mouseY, this.stopButton))
			{
				inputTip("Stop music");
			}
		}
		if (isMouseHovering(mouseX, mouseY, this.hintButton))
		{
			inputTip("Should hints display when hovering buttons?");
		}
		
		if (this.tip != null)
		{
			displayFloatingNote(this.tip, mouseX, mouseY);
		}
	}
	
	private boolean isMouseHovering(int mx, int my, GuiButton button)
	{
		int x = button.xPosition;
		int y = button.yPosition;
		int w = button.width;
		int h = button.height;
		
		return mx >= x && my >= y && mx <= x + w && my <= y + h;
	}
	
	private void displayFloatingNote(String tipContents, int mouseX, int mouseY)
	{
		if (tipContents != null)
		{
			int var5 = mouseY - 12 - 6;
			int var6 = this.fontRenderer.getStringWidth(tipContents);
			int var4 = mouseX /*- var6 / 2*/- 4;
			
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
			this.packSlotContainer.func_77208_b(-this.packSlotContainer.slotHeight); // Readjust container if off limits
			updateUpDownButtons();
		}
		else if (!prioritize && this.selectedSlot + 1 < getSize())
		{
			this.mod.getPackManager().swapDuetAt(this.selectedSlot);
			setSelected(this.selectedSlot + 1);
			this.packSlotContainer.func_77208_b(this.packSlotContainer.slotHeight);
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
			this.mod.util().closeCurrentScreen();
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
