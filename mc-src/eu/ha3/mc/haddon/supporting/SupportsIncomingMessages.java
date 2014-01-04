package eu.ha3.mc.haddon.supporting;

import net.minecraft.network.play.client.C17PacketCustomPayload;

/* x-placeholder-wtfplv2 */

public interface SupportsIncomingMessages
{
	/**
	 * Receives a message that has been enlisted for.
	 * 
	 * @param message
	 */
	public void onIncomingMessage(C17PacketCustomPayload message);
	
}
