package eu.ha3.mc.haddon;

import net.minecraft.client.Minecraft;
import net.minecraft.src.KeyBinding;

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
	
	public void addKeyBinding(KeyBinding keyBindingIn, String localization);
	
}
