package eu.ha3.matmos.engine.logic;

import java.util.List;

public interface Knowledge
{
	/**
	 * A generic Sheet. Different implementations will allow the sheet to behave
	 * differently when asked with the isTrue() method.
	 * 
	 * @param name
	 *            Name of the sheet.
	 * @param sheetFormat
	 *            The sheet.
	 */
	public void addSheet(String name, Sheet sheetFormat);
	
	/**
	 * A condition.<br>
	 * <br>
	 * If the condition leads to an invalid situation, assume the result will
	 * always return false.
	 * 
	 * @param name
	 *            Name of the condition.
	 * @param sheet
	 *            Name of the sheet.
	 * @param index
	 *            Index in the sheet.
	 * @param compareFunction
	 *            Comparison function.
	 * @param value
	 *            Value to compare against.
	 */
	public void addCondition(String name, String sheet, String index,
			String compareFunction,
			String value);
	
	/**
	 * A set (of conditions).<br>
	 * <br>
	 * If one of the lists is empty, assume that list is true. If both lists are
	 * empty assume the result will always return false.
	 * 
	 * @param name
	 *            Name of the set.
	 * @param isTrue
	 *            List of conditions that have to be true.
	 * @param isFalse
	 *            List of conditions that have to be false.
	 */
	public void addSet(String name, List<String> isTrue,
			List<String> isFalse);
	
	/**
	 * A machine. It needs at least a working battery (condition set) to be on,
	 * but all the brakes must be off for it to run.<br>
	 * <br>
	 * If the machine has no batteries.
	 * 
	 * @param name
	 * @param fuels
	 *            List of sets that allow the machine to run if at least one of
	 *            them is on and the brakes are all off.
	 * @param brakes
	 *            List of sets that disallow the machine to run if at least one
	 *            of them is on.
	 */
	public void addMachine(String name, List<String> fuels, List<String> brakes);
	
}
