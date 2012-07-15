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
		
		supportsTick = haddon instanceof SupportsTickEvents;
		supportsFrame = haddon instanceof SupportsFrameEvents;
		supportsChat = haddon instanceof SupportsChatEvents;
		supportsKey = haddon instanceof SupportsKeyEvents;
		
		impl_continueTicking = supportsTick || supportsFrame;
		
		lastTick = -1;
		
		if (haddon instanceof SupportsInitialization)
			((SupportsInitialization) haddon).onInitialize();
		
	}
	
	@Override
	public void load()
	{
		haddon.onLoad();
		
		if (supportsTick && !supportsFrame)
			ModLoader.setInGameHook(this, true, true);
		
		else if (supportsFrame)
			ModLoader.setInGameHook(this, true, false);
		
	}
	
	@Override
	public Minecraft getMinecraft()
	{
		return ModLoader.getMinecraftInstance();
		
	}
	
	@Override
	public boolean onTickInGame(float semi, Minecraft minecraft)
	{
		if (supportsTick && tickEnabled)
		{
			int tick = utility.getClientTick();
			if (tick != lastTick)
			{
				((SupportsTickEvents) haddon).onTick();
				lastTick = tick;
				
			}
			
		}
		
		if (supportsFrame && frameEnabled)
		{
			((SupportsFrameEvents) haddon).onFrame(semi);
			
		}
		
		return impl_continueTicking;
		
	}
	
	@Override
	public void receiveChatPacket(String contents)
	{
		if (supportsChat && chatEnabled)
			((SupportsChatEvents) haddon).onChat(contents);
		
	}
	
	@Override
	public void hookTickEvents(boolean enable)
	{
		if (!supportsTick)
			return;
		
		tickEnabled = enable;
		
	}
	
	@Override
	public void hookFrameEvents(boolean enable)
	{
		if (!supportsFrame)
			return;
		
		frameEnabled = enable;
		
	}
	
	@Override
	public void hookChatEvents(boolean enable)
	{
		if (!supportsChat)
			return;
		
		chatEnabled = enable;
		
	}
	
	@Override
	public String getVersion()
	{
		return "REMOVE ME FROM IMPL"; //TODO
	}
	
	@Override
	public Utility getUtility()
	{
		return utility;
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addRenderable(Class renderable, Object renderer)
	{
		if (renderPairs == null)
			renderPairs = new HashMap<Object, Object>();
		
		renderPairs.put(renderable, renderer);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addRenderer(Map map)
	{
		if (renderPairs != null)
			map.putAll(renderPairs);
		
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
		if (!supportsKey)
			return;
		
		((SupportsKeyEvents) haddon).onKey(event);
		
	}
	
}
