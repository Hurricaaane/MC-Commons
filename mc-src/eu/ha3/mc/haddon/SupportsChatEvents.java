package eu.ha3.mc.haddon;

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
