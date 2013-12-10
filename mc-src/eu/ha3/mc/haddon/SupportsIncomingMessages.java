package eu.ha3.mc.haddon;

import net.minecraft.src.Packet250CustomPayload;

/* x-placeholder-wtfplv2 */

public interface SupportsIncomingMessages
{
	/**
	 * Receives a message that has been enlisted for.
	 * 
	 * @param message
	 */
	public void onIncomingMessage(Packet250CustomPayload message);
	
}
