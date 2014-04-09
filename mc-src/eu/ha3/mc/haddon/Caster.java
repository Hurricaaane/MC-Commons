package eu.ha3.mc.haddon;

/*
--filenotes-placeholder
*/

/**
 * This is an interface used by operators that manages fick and frame flow.
 * 
 * @author Hurry
 * 
 */
public interface Caster
{
	/**
	 * Enable the functionality of (SupportsTickEvents)onTick() while
	 * in-game-world. onTick will not run if the Haddon is not an instance of
	 * SupportsTickEvents, however, the number of ticks elapsed should still be
	 * counted as long as the functionnality is enabled.
	 * 
	 * @param enabled
	 */
	public void setTickEnabled(boolean enabled);
	
	/**
	 * Enable the functionality of (SupportsFrameEvents)onFrame() while
	 * in-game-world. onFrame will not run if the Haddon is not an instance of
	 * SupportsFrameEvents.
	 * 
	 * @param enabled
	 */
	public void setFrameEnabled(boolean enabled);
	
	/**
	 * Returns the number of ticks elapsed while ticks are enabled. This works
	 * even if the Haddon is not an instance of SupportsTickEvents.
	 * 
	 * @return
	 */
	public int getTicks();
}
