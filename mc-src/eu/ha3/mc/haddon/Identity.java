package eu.ha3.mc.haddon;

/*
--filenotes-placeholder
*/

public interface Identity
{
	/**
	 * Returns the name of the mod. This can include spaces. Do not include
	 * version number.
	 * 
	 * @return
	 */
	public String getHaddonName();
	
	/**
	 * Returns the version number. Format must be usable for automaton (to
	 * compare numbers).
	 * 
	 * @return
	 */
	public int getHaddonVersionNumber();
	
	/**
	 * Returns Minecraft version this is made for. Format is arbitrary, and
	 * should not be used for automaton.
	 * 
	 * @return
	 */
	public String getHaddonMinecraftVersion();
	
	/**
	 * Returns a web URL for the website of this haddon.
	 * 
	 * @return
	 */
	public String getHaddonAddress();
	
	/**
	 * Returns a human-readable version. Format is arbitrary, and should not be
	 * used for automaton.
	 * 
	 * @return
	 */
	public String getHaddonHumanVersion();
}
