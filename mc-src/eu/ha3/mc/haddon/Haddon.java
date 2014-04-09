package eu.ha3.mc.haddon;

/* x-placeholder-wtfplv2 */

public interface Haddon
{
	/**
	 * Triggered during the addon loading process.
	 */
	public void onLoad();
	
	/**
	 * Returns the utility object dedicated to this haddon.
	 * 
	 * @return
	 */
	public Utility getUtility();
	
	/**
	 * Sets the utility object dedicated to this haddon.
	 * 
	 * @param utility
	 */
	public void setUtility(Utility utility);
	
	/**
	 * Returns the caster object dedicated to this haddon.
	 * 
	 * @return
	 */
	public Operator getOperator();
	
	/**
	 * Sets the caster object dedicated to this haddon.
	 * 
	 * @param operator
	 */
	public void setOperator(Operator operator);
	
	//
	
	/**
	 * Returns the identity of this Haddon.
	 * 
	 * @return
	 */
	public Identity getIdentity();
	
}
