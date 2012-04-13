package eu.ha3.mc.mod;

import net.minecraft.src.KeyBinding;
import net.minecraft.src.Packet250CustomPayload;

public abstract class Ha3ModCore
{
	public abstract void setMod(Ha3Mod modIn);
	
	public abstract void load();
	
	//public void unload();
	
	/**
	 * If uses frame, triggers when a frame event occurs.
	 * 
	 * @param fspan
	 */
	public void doFrame(float fspan)
	{
	};
	
	/**
	 * When a KeyBinding event occurs.
	 * 
	 * @param fspan
	 */
	public void doKeyBindingEvent(KeyBinding event)
	{
	};
	
	/**
	 * Trigger when the Mod Manager turns into a "ready" state (i.e. when all
	 * mods are loaded).
	 * 
	 * @param fspan
	 */
	public void doManagerReady()
	{
	};
	
	/**
	 * Triggers when a packet subscribed to arrives.
	 * 
	 * @param fspan
	 */
	public void doPluginChannelPacket(Packet250CustomPayload packet)
	{
	};
	
	/**
	 * If uses chat, triggers when a chat occurs.
	 * 
	 * @param fspan
	 */
	public void doChat(String contents)
	{
	};
	
}
