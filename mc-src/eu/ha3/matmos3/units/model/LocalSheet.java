package eu.ha3.matmos3.units.model;

public interface LocalSheet extends Unit
{
	/**
	 * Core name of sheet.
	 * 
	 * @return
	 */
	public String getSheetCoreName();
	
	/**
	 * Time span required for this unit to reevaluate itself.<br>
	 * 0 means every frame.
	 * 
	 * @return
	 */
	public int getTimeSpan();
	
	/**
	 * Returns the value associated with the key of the sheet.<br>
	 * <br>
	 * If the sheet or the key is invalid, the method would throw a
	 * ExpansionRuntimeException to be caught by the engine, which would shut
	 * itself down.
	 * 
	 * @param key
	 * @return
	 */
	public Object valueOf(String key);
	
	/**
	 * Returns the delta value associated with the key of the sheet, using the
	 * previously gathered value as the reference.<br>
	 * If the value has no delta yet, the value returned should be either 0 if
	 * the value is of a numeric type, or the current value for any other type.<br>
	 * The delta value is not effective when the key is gathered beforehand. For
	 * instance, if a Machine unit uses a Variable unit delta as a parameter,
	 * the Variable unit will only be gathered when the Machine is turned on.
	 * Therefore, the delta is inaccurate on the first frame (holds the
	 * previously stored delta value), unless the Variable unit is always on.<br>
	 * <br>
	 * If the sheet or the key is invalid, the method would throw a
	 * ExpansionRuntimeException to be caught by the engine, which would shut
	 * itself down.
	 * 
	 * @param key
	 * @return
	 */
	public Object deltaOf(String key);
	
}
