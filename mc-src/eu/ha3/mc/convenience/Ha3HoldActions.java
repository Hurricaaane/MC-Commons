package eu.ha3.mc.convenience;

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

public interface Ha3HoldActions
{
	/**
	 * Called when the key begins to be pressed.
	 */
	public void beginPress();
	
	/**
	 * Called when the key is released. This is called after shortPress and
	 * endHold.
	 */
	public void endPress();
	
	/**
	 * Called on short press, when the key is released.
	 */
	public void shortPress();
	
	/**
	 * Called when holding is detected.
	 */
	public void beginHold();
	
	/**
	 * Called when holding is finished.
	 */
	public void endHold();
}
