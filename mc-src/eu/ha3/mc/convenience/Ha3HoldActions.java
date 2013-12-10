package eu.ha3.mc.convenience;

/* x-placeholder-wtfplv2 */

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
