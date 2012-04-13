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
	
}
