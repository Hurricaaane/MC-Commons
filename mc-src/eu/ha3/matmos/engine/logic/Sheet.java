package eu.ha3.matmos.engine.logic;

public interface Sheet
{
	/**
	 * Tests if the question is true. <br>
	 * <br>
	 * If the question leads to an invalid situation, assume the result will
	 * always return false.
	 * 
	 * @param sheet
	 * @param index
	 * @param compareFunction
	 * @param value
	 * @return
	 */
	public boolean isTrue(String sheet, String index, String compareFunction,
			String value);
	
	/**
	 * Implementations need to trigger this when the Sheet is fully updated.
	 * 
	 */
	public void signalUpdate();
	
	/**
	 * Adds an update listener.
	 * 
	 * @param listener
	 */
	public void addUpdateListener(UpdateListener listener);
	
	/**
	 * Removes an update listener.
	 * 
	 * @param listener
	 */
	public void removeUpdateListener(UpdateListener listener);
	
}
