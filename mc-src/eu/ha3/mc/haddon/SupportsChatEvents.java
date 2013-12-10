package eu.ha3.mc.haddon;

/* x-placeholder-wtfplv2 */

public interface SupportsChatEvents
{
	/**
	 * Triggered when a chat is intercepted while the chat events are hooked
	 * onto the manager. The contents should normally be a single line,
	 * independently on whether the content is line-wrapped on screen display.
	 * 
	 * @param contents
	 *            Contents
	 */
	public void onChat(String contents);
	
}
