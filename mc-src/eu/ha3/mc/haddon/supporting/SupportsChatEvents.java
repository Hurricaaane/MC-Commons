package eu.ha3.mc.haddon.supporting;

import net.minecraft.util.IChatComponent;

/* x-placeholder-wtfplv2 */

public interface SupportsChatEvents
{
	/**
	 * Triggered when the OperatorChatter receives chat while it's enabled.
	 * 
	 * @param chat
	 * @param message
	 */
	public void onChat(IChatComponent chat, String message);
	
}
