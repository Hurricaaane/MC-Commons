package eu.ha3.matmos3.engine.model;

public interface Sheet
{
	/**
	 * Returns the value associated with the key of the sheet.<br>
	 * <br>
	 * If the sheet or the key is invalid, the method would throw a
	 * ExpansionRuntimeException to be caught by whatever engine was the cause
	 * of the failed attempt, which would shut itself down.
	 * 
	 * @param key
	 * @return
	 */
	public Object valueOf(String key);
}
