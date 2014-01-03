package eu.ha3.mc.haddon;

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
