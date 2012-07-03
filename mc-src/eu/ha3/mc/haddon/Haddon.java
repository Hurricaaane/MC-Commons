package eu.ha3.mc.haddon;

public interface Haddon
{
	// This has been moved to a SupportsInitialization
	/*
	 * Triggered after Utility and Manager have been bound and after the addon
	 * interfaces have been evaluated (eu.ha3.mc.haddon.Supports*).
	 * 
	 */
	//public void onInitialize();
	
	/**
	 * Triggered depending on the Manager implementation during the addon
	 * loading process.
	 * 
	 */
	public void onLoad();
	
	/**
	 * Get the manager object dedicated to this addon.
	 * 
	 * @return
	 */
	public Manager getManager();
	
	/**
	 * Sets the manager object dedicated to this addon.
	 * 
	 * @return
	 */
	public void setManager(Manager manager);
	
}
