package eu.ha3.mc.haddon;

public interface SupportsInitialization
{
	/**
	 * Triggered after Utility and Manager have been bound and after the addon
	 * interfaces have been evaluated (eu.ha3.mc.haddon.Supports*).
	 * 
	 */
	public void onInitialize();
	
}
