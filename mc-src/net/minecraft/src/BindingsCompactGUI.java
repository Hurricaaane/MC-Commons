package net.minecraft.src;

public class BindingsCompactGUI extends GuiScreen
{
	/**
	 * A reference to the screen object that created this. Used for navigating
	 * between screens.
	 */
	private GuiScreen parentScreen;
	
	/** The title string that is displayed in the top-center of the screen. */
	protected String screenTitle;
	
	/** Reference to the GameSettings object. */
	private GameSettings options;
	
	/** The ID of the button that has been pressed. */
	private int buttonId;
	
	public BindingsCompactGUI(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
	{
		this.screenTitle = "Controls";
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		this.options = par2GameSettings;
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
		
		for (int j = 0; j < this.options.keyBindings.length; j++)
		{
			this.buttonList.add(new GuiSmallButton(
				j, i + j % 2 * 160, this.height / 12 + 16 * (j >> 1), 70, 20, this.options.getOptionDisplayString(j)));
		}
		
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, stringtranslate
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
		for (int i = 0; i < this.options.keyBindings.length; i++)
		{
			((GuiButton) this.buttonList.get(i)).displayString = this.options.getOptionDisplayString(i);
		}
		
		if (par1GuiButton.id == 200)
		{
			this.mc.displayGuiScreen(this.parentScreen);
		}
		else
		{
			this.buttonId = par1GuiButton.id;
			par1GuiButton.displayString =
				new StringBuilder()
					.append("> ").append(this.options.getOptionDisplayString(par1GuiButton.id)).append(" <").toString();
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
			this.options.setKeyBinding(this.buttonId, -100 + par3);
			((GuiButton) this.buttonList.get(this.buttonId)).displayString =
				this.options.getOptionDisplayString(this.buttonId);
			this.buttonId = -1;
			KeyBinding.resetKeyBindingArrayAndHash();
		}
		else
		{
			super.mouseClicked(par1, par2, par3);
		}
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		if (this.buttonId >= 0)
		{
			this.options.setKeyBinding(this.buttonId, par2);
			((GuiButton) this.buttonList.get(this.buttonId)).displayString =
				this.options.getOptionDisplayString(this.buttonId);
			this.buttonId = -1;
			KeyBinding.resetKeyBindingArrayAndHash();
		}
		else
		{
			super.keyTyped(par1, par2);
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
		int i = func_20080_j();
		
		for (int j = 0; j < this.options.keyBindings.length; j++)
		{
			boolean flag = false;
			int k = 0;
			
			do
			{
				if (k >= this.options.keyBindings.length)
				{
					break;
				}
				
				if (k != j && this.options.keyBindings[j].keyCode == this.options.keyBindings[k].keyCode)
				{
					flag = true;
					break;
				}
				
				k++;
			} while (true);
			
			k = j;
			
			if (this.buttonId == j)
			{
				((GuiButton) this.buttonList.get(k)).displayString = "\247f> \247e??? \247f<";
			}
			else if (flag)
			{
				((GuiButton) this.buttonList.get(k)).displayString =
					new StringBuilder().append("\247c").append(this.options.getOptionDisplayString(k)).toString();
			}
			else
			{
				((GuiButton) this.buttonList.get(k)).displayString = this.options.getOptionDisplayString(k);
			}
			
			drawString(
				this.fontRenderer, this.options.getKeyBindingDescription(j), i + j % 2 * 160 + 70 + 6, this.height
					/ 12 + 16 * (j >> 1) + 7, -1);
		}
		
		super.drawScreen(par1, par2, par3);
	}
}
