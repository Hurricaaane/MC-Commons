package net.minecraft.src;


import org.lwjgl.input.Keyboard;

import eu.ha3.mc.convenience.Ha3KeyManager;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
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
		mod = mAtmosHaddon;
		
	}
	
	public void load()
	{
		// Prepare key bindings
		keyBindingMain = new KeyBinding("key.matmos", 65);
		keyManager = new Ha3KeyManager();
		
		mod.manager().addKeyBinding(keyBindingMain, "MAtmos");
		keyManager.addKeyBinding(keyBindingMain, new MAtKeyMain(this));
		
		// Prepare scroller
		scroller = new MAtScroller(this, mod);
		
	}
	
	public String getKeyBindingMainFriendlyName()
	{
		if (keyBindingMain == null)
			return "undefined";
		
		return Keyboard.getKeyName(keyBindingMain.keyCode);
	}
	
	public void tickRoutine()
	{
		keyManager.handleRuntime();
		
		scroller.routine();
		if (scroller.isRunning())
		{
			if (!scrollModeIsMusic)
				mod.soundManager().setCustomSoundVolume(
						scroller.getValue());
			else
				mod.soundManager().setCustomMusicVolume(
						scroller.getValue());
			
		}
		
	}
	
	public void frameRoutine(float fspan)
	{
		scroller.draw(fspan);
		
	}
	
	public void communicateKeyBindingEvent(KeyBinding event)
	{
		keyManager.handleKeyDown(event);
		
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
		if (mod.isRunning() && mod.util().isCurrentScreen(null))
		{
			scrollModeIsMusic = false;
			scroller.start(scrollModeIsMusic);
			
		}
		else if (mod.isRunning()
				&& (mod.util().isCurrentScreen(
						net.minecraft.src.GuiInventory.class) || mod
						.util().isCurrentScreen(
								net.minecraft.src.GuiContainerCreative.class)))
		{
			mod.util().closeCurrentScreen();
			
			scrollModeIsMusic = true;
			scroller.start(scrollModeIsMusic);
			
		}
		
	}
	
	public void printUnusualMessages()
	{
		if (!mod.isReady())
		{
			MAtModPhase phase = mod.getPhase();
			if (!mod.isFatalError())
			{
				switch (phase)
				{
				case CONSTRUCTING:
					mod.printChat(Ha3Utility.COLOR_GOLD,
							"Still loading... ", Ha3Utility.COLOR_GRAY,
							"(Waiting for the sound engine to be ready)");
					break;
				case RESOURCE_LOADER:
					mod.printChat(Ha3Utility.COLOR_GOLD,
							"Still loading... ", Ha3Utility.COLOR_GRAY,
							"(Minecraft is downloading sounds)");
					mod
					.printChatShort(
							Ha3Utility.COLOR_WHITE,
							"This can take from seconds to 5 minutes in average, depending on your network speed.");
					mod
					.printChatShort(Ha3Utility.COLOR_GRAY,
							"If you're offline, it will unlock after 20 seconds.");
					mod
					.printChatShort(
							Ha3Utility.COLOR_WHITE,
							"This usually happens after reinstalling Minecraft.");
					mod.printChatShort(Ha3Utility.COLOR_WHITE,
							"(Remember to install MAtmos sounds!)");
					break;
				case FINAL_PHASE:
					mod.printChat(Ha3Utility.COLOR_GOLD,
							"Still loading... ",
							Ha3Utility.COLOR_GRAY,
							"(MAtmos is preparing the ambience generator)");
					break;
				}
				
			}
			else
			{
				switch (phase)
				{
				case NOT_INITIALIZED:
					mod.printChat(Ha3Utility.COLOR_GOLD,
							"MAtmos will not load due to a fatal error. ",
							Ha3Utility.COLOR_GRAY,
							"(Some MAtmos modules are not initialized)");
					break;
				case SOUNDCOMMUNICATOR_FAILURE:
					mod.printChat(Ha3Utility.COLOR_GOLD,
							"Still loading... ", Ha3Utility.COLOR_GRAY,
							"(Could not retreive Minecraft sound engine)");
					break;
				}
				
			}
		}
		
	}
	
	public void signalShortPress()
	{
		if (mod.isRunning())
		{
			if (!hasFirstHit
 && mod.expansionLoader().getLoadingCount() > 0)
			{
				int glc = mod.expansionLoader().getLoadingCount();
				mod.printChat(
						Ha3Utility.COLOR_GOLD,
						"Warning: " + glc + " expansion"
								+ (glc > 1 ? "s are" : " is")
								+ " still loading.");
				mod.printChatShort(Ha3Utility.COLOR_GOLD, "Press ",
						Ha3Utility.COLOR_WHITE,
						this.getKeyBindingMainFriendlyName(),
						Ha3Utility.COLOR_GOLD, " to stop MAtmos.");
				
			}
			else
			{
				mod.stopRunning();
				mod.printChat(Ha3Utility.COLOR_YELLOW,
						"Stopped. Press ", Ha3Utility.COLOR_WHITE,
						this.getKeyBindingMainFriendlyName(),
						Ha3Utility.COLOR_YELLOW, " to re-enable.");
			}
			hasFirstHit = true;
			
		}
		
		else if (mod.isReady())
		{
			mod.printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Loading...");
			mod.startRunning();
			
		}
		
		printUnusualMessages();
		
	}
	
	public void endHold()
	{
		if (scroller.isRunning())
		{
			scroller.stop();
			mod.options().saveOptions();
			
		}
		
		if (!mod.isRunning() && mod.isReady())
		{
			mod.printChat(Ha3Utility.COLOR_BRIGHTGREEN,
					"Reloading expansions...");
			mod.reloadAndStart();
			
		}
		
		printUnusualMessages();
		
	}
	
}
