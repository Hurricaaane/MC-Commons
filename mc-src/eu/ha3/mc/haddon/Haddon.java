package eu.ha3.mc.haddon;

/* x-placeholder-wtfplv2 */

public interface Haddon
{
	/**
	 * Triggered depending on the Manager implementation during the addon
	 * loading process.
	 * 
	 */
	public void onLoad();
	
	/**
	 * Get the utility object dedicated to this addon.
	 * 
	 * @return
	 */
	public Utility getUtility();
	
	/**
	 * Sets the utility object dedicated to this addon.
	 * 
	 * @return
	 */
	public void setUtility(Utility utility);
	
}
