package net.minecraft.src;

import net.minecraft.client.Minecraft;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.RenderListener;

import eu.ha3.mc.haddon.Bridge;
import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Manager;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.UnsupportedInterfaceException;
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

public class LiteMod_CCB implements LiteMod, RenderListener, Manager, Bridge
{
	private CCBHaddon haddon;
	private Utility utility;
	
	private Minecraft mc;
	
	private boolean supportsFrame;
	
	private boolean frameEnabled;
	
	public LiteMod_CCB()
	{
		this.haddon = new CCBHaddon();
		
		this.utility = new HaddonUtilityModLoader(this);
		this.haddon.setManager(this);
		
		this.supportsFrame = this.haddon instanceof SupportsFrameEvents;
		
		this.mc = Minecraft.getMinecraft();
	}
	
	@Override
	public Haddon getHaddon()
	{
		return this.haddon;
		
	}
	
	@Override
	public void init()
	{
		this.haddon.onLoad();
		
	}
	
	@Override
	public Minecraft getMinecraft()
	{
		return this.mc;
		
	}
	
	@Override
	public void hookTickEvents(boolean enable)
	{
		throw new UnsupportedInterfaceException();
		
	}
	
	@Override
	public void hookGuiTickEvents(boolean enable)
	{
		throw new UnsupportedInterfaceException();
		
	}
	
	@Override
	public void hookGuiFrameEvents(boolean enable)
	{
		throw new UnsupportedInterfaceException();
		
	}
	
	@Override
	public void hookFrameEvents(boolean enable)
	{
		this.frameEnabled = enable;
		
	}
	
	@Override
	public void hookChatEvents(boolean enable)
	{
		throw new UnsupportedInterfaceException();
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
		throw new UnsupportedInterfaceException();
	}
	
	@Override
	public void addKeyBinding(KeyBinding keyBindingIn, String localization)
	{
		throw new UnsupportedInterfaceException();
		
	}
	
	@Override
	public void enlistIncomingMessages(String channel)
	{
		throw new UnsupportedInterfaceException();
	}
	
	@Override
	public void enlistOutgoingMessages(String channel)
	{
		throw new UnsupportedInterfaceException();
	}
	
	@Override
	public void delistIncomingMessages(String channel)
	{
		throw new UnsupportedInterfaceException();
	}
	
	@Override
	public void delistOutgoingMessages(String channel)
	{
		throw new UnsupportedInterfaceException();
	}
	
	@Override
	public void sendOutgoingMessage(Packet250CustomPayload message)
	{
		throw new UnsupportedInterfaceException();
	}
	
	@Override
	public void onRender()
	{
		if (this.supportsFrame && this.frameEnabled)
		{
			((SupportsFrameEvents) this.haddon).onFrame(0f);
		}
	}
	
	@Override
	public void onRenderGui(GuiScreen currentScreen)
	{
	}
	
	@Override
	public void onRenderWorld()
	{
	}
	
	@Override
	public void onSetupCameraTransform()
	{
	}
	
	@Override
	public String getName()
	{
		return this.haddon.getClass().getName();
	}
	
	@Override
	public String getVersion()
	{
		return "" + CCBHaddon.VERSION;
	}
	
}
