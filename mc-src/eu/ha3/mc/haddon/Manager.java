package eu.ha3.mc.haddon;

import net.minecraft.client.Minecraft;
import net.minecraft.src.KeyBinding;

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
	 * If the addon doesn't implement SupportsTickEvents, the method will fail
	 * silently.
	 * 
	 * @param enable
	 */
	public void hookTickEvents(boolean enable);
	
	/**
	 * Enable or disable the Frame events.
	 * 
	 * If the addon doesn't implement SupportsTickEvents, the method will fail
	 * silently.
	 * 
	 * @param enable
	 */
	public void hookFrameEvents(boolean enable);
	
	/**
	 * Enable or disable the Chat events.
	 * 
	 * If the addon doesn't implement SupportsTickEvents, the method will fail
	 * silently.
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
