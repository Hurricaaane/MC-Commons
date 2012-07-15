package net.minecraft.src;

import org.lwjgl.input.Keyboard;

import eu.ha3.mc.convenience.Ha3KeyManager;

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

public class MAtUserControl
{
	private MAtMod mod;
	
	private KeyBinding keyBindingMain;
	private Ha3KeyManager keyManager;
	
	private MAtScroller scroller;
	private boolean scrollModeIsMusic;
	
	private boolean hasFirstHit;
	
	public MAtUserControl(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		
	}
	
	public void load()
	{
		// Prepare key bindings
		this.keyBindingMain = new KeyBinding("key.matmos", 65);
		this.keyManager = new Ha3KeyManager();
		
		this.mod.manager().addKeyBinding(this.keyBindingMain, "MAtmos");
		this.keyManager.addKeyBinding(this.keyBindingMain, new MAtKeyMain(this));
		
		// Prepare scroller
		this.scroller = new MAtScroller(this, this.mod);
		
	}
	
	public String getKeyBindingMainFriendlyName()
	{
		if (this.keyBindingMain == null)
			return "undefined";
		
		return Keyboard.getKeyName(this.keyBindingMain.keyCode);
	}
	
	public void tickRoutine()
	{
		this.keyManager.handleRuntime();
		
		this.scroller.routine();
		if (this.scroller.isRunning())
		{
			if (!this.scrollModeIsMusic)
			{
				this.mod.soundManager().setCustomSoundVolume(this.scroller.getValue());
			}
			else
			{
				this.mod.soundManager().setCustomMusicVolume(this.scroller.getValue());
			}
			
		}
		
	}
	
	public void frameRoutine(float fspan)
	{
		this.scroller.draw(fspan);
		
	}
	
	public void communicateKeyBindingEvent(KeyBinding event)
	{
		this.keyManager.handleKeyDown(event);
		
	}
	
	public void signalPress()
	{
		// Do nothing.
		// XXX DEBUG
		/*mod.manager().getMinecraft().displayGuiScreen(
				new MAtGuiExpansions(null));*/
		
	}
	
	public void beginHold()
	{
		if (this.mod.isRunning() && this.mod.util().isCurrentScreen(null))
		{
			this.scrollModeIsMusic = false;
			this.scroller.start(this.scrollModeIsMusic);
			
		}
		else if (this.mod.isRunning()
			&& (this.mod.util().isCurrentScreen(net.minecraft.src.GuiInventory.class) || this.mod
				.util().isCurrentScreen(net.minecraft.src.GuiContainerCreative.class)))
		{
			this.mod.util().closeCurrentScreen();
			
			this.scrollModeIsMusic = true;
			this.scroller.start(this.scrollModeIsMusic);
			
		}
		
	}
	
	public void printUnusualMessages()
	{
		if (!this.mod.isReady())
		{
			MAtModPhase phase = this.mod.getPhase();
			if (!this.mod.isFatalError())
			{
				switch (phase)
				{
				case CONSTRUCTING:
					this.mod.printChat(
						Ha3Utility.COLOR_GOLD, "Still loading... ", Ha3Utility.COLOR_GRAY,
						"(Waiting for the sound engine to be ready)");
					break;
				case RESOURCE_LOADER:
					this.mod.printChat(
						Ha3Utility.COLOR_GOLD, "Still loading... ", Ha3Utility.COLOR_GRAY,
						"(Minecraft is downloading sounds)");
					this.mod.printChatShort(
						Ha3Utility.COLOR_WHITE,
						"This can take from seconds to 5 minutes in average, depending on your network speed.");
					this.mod.printChatShort(
						Ha3Utility.COLOR_GRAY, "If you're offline, it will unlock after 20 seconds.");
					this.mod.printChatShort(
						Ha3Utility.COLOR_WHITE, "This usually happens after reinstalling Minecraft.");
					this.mod.printChatShort(Ha3Utility.COLOR_WHITE, "(Remember to install MAtmos sounds!)");
					break;
				case FINAL_PHASE:
					this.mod.printChat(
						Ha3Utility.COLOR_GOLD, "Still loading... ", Ha3Utility.COLOR_GRAY,
						"(MAtmos is preparing the ambience generator)");
					break;
				}
				
			}
			else
			{
				switch (phase)
				{
				case NOT_INITIALIZED:
					this.mod.printChat(
						Ha3Utility.COLOR_GOLD, "MAtmos will not load due to a fatal error. ", Ha3Utility.COLOR_GRAY,
						"(Some MAtmos modules are not initialized)");
					break;
				case SOUNDCOMMUNICATOR_FAILURE:
					this.mod.printChat(
						Ha3Utility.COLOR_GOLD, "Still loading... ", Ha3Utility.COLOR_GRAY,
						"(Could not retreive Minecraft sound engine)");
					break;
				}
				
			}
		}
		
	}
	
	public void signalShortPress()
	{
		if (this.mod.isRunning())
		{
			if (!this.hasFirstHit && this.mod.expansionLoader().getLoadingCount() > 0)
			{
				int glc = this.mod.expansionLoader().getLoadingCount();
				this.mod.printChat(Ha3Utility.COLOR_GOLD, "Warning: "
					+ glc + " expansion" + (glc > 1 ? "s are" : " is") + " still loading.");
				this.mod.printChatShort(
					Ha3Utility.COLOR_GOLD, "Press ", Ha3Utility.COLOR_WHITE, getKeyBindingMainFriendlyName(),
					Ha3Utility.COLOR_GOLD, " to stop MAtmos.");
				
			}
			else
			{
				this.mod.stopRunning();
				this.mod.printChat(
					Ha3Utility.COLOR_YELLOW, "Stopped. Press ", Ha3Utility.COLOR_WHITE,
					getKeyBindingMainFriendlyName(), Ha3Utility.COLOR_YELLOW, " to re-enable.");
			}
			this.hasFirstHit = true;
			
		}
		
		else if (this.mod.isReady())
		{
			this.mod.printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Loading...");
			this.mod.startRunning();
			
		}
		
		printUnusualMessages();
		
	}
	
	public void endHold()
	{
		if (this.scroller.isRunning())
		{
			this.scroller.stop();
			this.mod.options().saveOptions();
			
		}
		
		if (!this.mod.isRunning() && this.mod.isReady())
		{
			this.mod.printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Reloading expansions...");
			this.mod.reloadAndStart();
			
		}
		
		printUnusualMessages();
		
	}
	
}
