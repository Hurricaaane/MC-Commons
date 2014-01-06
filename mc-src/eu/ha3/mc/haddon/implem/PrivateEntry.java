package eu.ha3.mc.haddon.implem;

import eu.ha3.mc.haddon.PrivateAccessException;

/*
--filenotes-placeholder
*/

public interface PrivateEntry
{
	public String getName();
	
	@SuppressWarnings("rawtypes")
	public Class getTarget();
	
	public int getZero();
	
	/**
	 * Gets the field names from less to more important
	 * 
	 * @return
	 */
	public String[] getFieldNames();
	
	public Object get(Object instance) throws PrivateAccessException;
	
	public void set(Object instance, Object value) throws PrivateAccessException;
}
