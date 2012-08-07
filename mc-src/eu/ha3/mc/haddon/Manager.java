package eu.ha3.mc.haddon;

import net.minecraft.client.Minecraft;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Packet250CustomPayload;

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

public interface Manager
{
	/**
	 * Gets the Minecraft instance.
	 * 
	 * @return
	 */
	public Minecraft getMinecraft();
	
	/**
	 * Gets the utility object dedicated to this manager.
	 * 
	 * @return
	 */
	public Utility getUtility();
	
	/**
	 * Enable or disable the Tick events.
	 * 
	 * If the addon doesn't implement SupportsTickEvents, the method will throw
	 * a UnsupportedInterfaceException.
	 * 
	 * @param enable
	 */
	public void hookTickEvents(boolean enable);
	
	/**
	 * Enable or disable the Frame events.
	 * 
	 * If the addon doesn't implement SupportsTickEvents, the method will throw
	 * a UnsupportedInterfaceException.
	 * 
	 * @param enable
	 */
	public void hookFrameEvents(boolean enable);
	
	/**
	 * Enable or disable the Gui Tick events.
	 * 
	 * If the addon doesn't implement SupportsGuiTickEvents, the method will
	 * throw a UnsupportedInterfaceException.
	 * 
	 * @param enable
	 */
	public void hookGuiTickEvents(boolean enable);
	
	/**
	 * Enable or disable the Gui Frame events.
	 * 
	 * If the addon doesn't implement SupportsGuiFrameEvents, the method will
	 * throw a UnsupportedInterfaceException.
	 * 
	 * @param enable
	 */
	public void hookGuiFrameEvents(boolean enable);
	
	/**
	 * Enable or disable the Chat events.
	 * 
	 * If the addon doesn't implement SupportsTickEvents, the method will throw
	 * a UnsupportedInterfaceException.
	 * 
	 * @param enable
	 */
	public void hookChatEvents(boolean enable);
	
	/**
	 * Add a renderable class coupled to a renderer object.<br/>
	 * <br/>
	 * The renderer class should normally extend net.minecraft.src.Render.
	 * 
	 * @param renderable
	 * @param renderer
	 */
	@SuppressWarnings("rawtypes")
	public void addRenderable(Class renderable, Object renderer);
	
	/**
	 * Adds a key binding with a certain localization.
	 * 
	 * @param keyBindingIn
	 * @param localization
	 */
	public void addKeyBinding(KeyBinding keyBindingIn, String localization);
	
	/**
	 * Enlist for incoming messages on some specific channel by name.<br>
	 * If channel is null, then allow any messages to be received if possible.<br>
	 * <br>
	 * If the addon doesn't implement SupportsIncomingMessages, the method will
	 * throw a UnsupportedInterfaceException.
	 * 
	 * @param channel
	 */
	public void enlistIncomingMessages(String channel);
	
	/**
	 * Declare existence of outgoing messages on some channel.
	 * 
	 * @param channel
	 */
	public void enlistOutgoingMessages(String channel);
	
	/**
	 * Delist for incoming messages on some channel.<br>
	 * If channel is null, then if it was previously enlisted for any messages,
	 * disallow any messages to be received except those specified by name.<br>
	 * <br>
	 * If the addon doesn't implement SupportsIncomingMessages, the method will
	 * throw a UnsupportedInterfaceException.
	 * 
	 * @param channel
	 */
	public void delistIncomingMessages(String channel);
	
	/**
	 * Declare no more messages are going to be outgoing anymore to this
	 * channel.
	 * 
	 * @param channel
	 */
	public void delistOutgoingMessages(String channel);
	
	/**
	 * Sends a message with a prepared packet. This doesn't require enlisting.
	 * 
	 * @param message
	 */
	public void sendOutgoingMessage(Packet250CustomPayload message);
	
}
