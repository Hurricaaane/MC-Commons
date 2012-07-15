package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Manager;
import eu.ha3.mc.haddon.SupportsChatEvents;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsInitialization;
import eu.ha3.mc.haddon.SupportsKeyEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;
import eu.ha3.mc.haddon.Utility;

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

public class HaddonBridgeModLoader extends BaseMod implements Manager
{
	private Haddon haddon;
	private Utility utility;
	
	private boolean supportsTick;
	private boolean supportsFrame;
	private boolean supportsChat;
	private boolean supportsKey;
	
	private boolean tickEnabled;
	private boolean frameEnabled;
	private boolean chatEnabled;
	
	private Map<Object, Object> renderPairs;
	
	private boolean impl_continueTicking;
	
	private int lastTick;
	
	public HaddonBridgeModLoader(Haddon haddon)
	{
		this.haddon = haddon;
		
		this.utility = new HaddonUtilityImpl(this);
		haddon.setManager(this);
		
		this.supportsTick = haddon instanceof SupportsTickEvents;
		this.supportsFrame = haddon instanceof SupportsFrameEvents;
		this.supportsChat = haddon instanceof SupportsChatEvents;
		this.supportsKey = haddon instanceof SupportsKeyEvents;
		
		this.impl_continueTicking = this.supportsTick || this.supportsFrame;
		
		this.lastTick = -1;
		
		if (haddon instanceof SupportsInitialization)
		{
			((SupportsInitialization) haddon).onInitialize();
		}
		
	}
	
	@Override
	public void load()
	{
		this.haddon.onLoad();
		
		if (this.supportsTick && !this.supportsFrame)
		{
			ModLoader.setInGameHook(this, true, true);
		}
		else if (this.supportsFrame)
		{
			ModLoader.setInGameHook(this, true, false);
		}
		
	}
	
	@Override
	public Minecraft getMinecraft()
	{
		return ModLoader.getMinecraftInstance();
		
	}
	
	@Override
	public boolean onTickInGame(float semi, Minecraft minecraft)
	{
		if (this.supportsTick && this.tickEnabled)
		{
			int tick = this.utility.getClientTick();
			if (tick != this.lastTick)
			{
				((SupportsTickEvents) this.haddon).onTick();
				this.lastTick = tick;
				
			}
			
		}
		
		if (this.supportsFrame && this.frameEnabled)
		{
			((SupportsFrameEvents) this.haddon).onFrame(semi);
			
		}
		
		return this.impl_continueTicking;
		
	}
	
	@Override
	public void receiveChatPacket(String contents)
	{
		if (this.supportsChat && this.chatEnabled)
		{
			((SupportsChatEvents) this.haddon).onChat(contents);
		}
		
	}
	
	@Override
	public void hookTickEvents(boolean enable)
	{
		if (!this.supportsTick)
			return;
		
		this.tickEnabled = enable;
		
	}
	
	@Override
	public void hookFrameEvents(boolean enable)
	{
		if (!this.supportsFrame)
			return;
		
		this.frameEnabled = enable;
		
	}
	
	@Override
	public void hookChatEvents(boolean enable)
	{
		if (!this.supportsChat)
			return;
		
		this.chatEnabled = enable;
		
	}
	
	@Override
	public String getVersion()
	{
		return "REMOVE ME FROM IMPL"; //TODO
	}
	
	@Override
	public Utility getUtility()
	{
		return this.utility;
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addRenderable(Class renderable, Object renderer)
	{
		if (this.renderPairs == null)
		{
			this.renderPairs = new HashMap<Object, Object>();
		}
		
		this.renderPairs.put(renderable, renderer);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addRenderer(Map map)
	{
		if (this.renderPairs != null)
		{
			map.putAll(this.renderPairs);
		}
		
	}
	
	@Override
	public void addKeyBinding(KeyBinding keyBindingIn, String localization)
	{
		ModLoader.addLocalization(keyBindingIn.keyDescription, localization);
		ModLoader.registerKey(this, keyBindingIn, true);
		
	}
	
	@Override
	public void keyboardEvent(KeyBinding event)
	{
		if (!this.supportsKey)
			return;
		
		((SupportsKeyEvents) this.haddon).onKey(event);
		
	}
	
}
