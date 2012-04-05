package eu.ha3.matmos.engine;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

public abstract class MAtmosSwitchable extends MAtmosDescriptible
{
	MAtmosKnowledge knowledge;
	boolean needsTesting;
	boolean isValid;
	
	MAtmosSwitchable(MAtmosKnowledge knowledgeIn)
	{
		knowledge = knowledgeIn;
		isValid = false;
		needsTesting = true;
		
	}
	
	public abstract boolean isActive();
	
	/**
	 * Flags the fact that this Switchable might
	 * start/stop working due to an update on the internal data.
	 * 
	 */
	public void flagNeedsTesting()
	{
		//System.out.println("TESTING");
		needsTesting = true;
	}
	
	/**
	 * Changes the Knowledge this Switchable belongs to.
	 */
	public void setKnowledge(MAtmosKnowledge knowledgeIn)
	{
		knowledge = knowledgeIn;
		flagNeedsTesting();
		
	}

	/**
	 * Tests if this Switchable is ought to work. A Switchable that
	 * references existing elements that are not valid doesn't mean
	 * this Switchable won't be valid. An non-existing reference usually
	 * causes the Switchable to stop being valid.
	 */
	public boolean isValid()
	{
		validateUsability();
		return isValid;
		
	}
	
	/**
	 * Rests if tjis Switchable is actually useable.
	 * Called by isValid. Don't call this.
	 */
	private void validateUsability()
	{
		if (!needsTesting) return;
		
		isValid = testIfValid();
		needsTesting = false;
		
	}
	
	/**
	 * Returns if the Switchable is valid. Usually, if some references
	 * lead to non-existing elements, this Switchable should be marked
	 * as invalid.
	 */
	protected abstract boolean testIfValid();
	
}
