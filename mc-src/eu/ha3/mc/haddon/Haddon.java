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
