package eu.ha3.matmos3.engine.model;

public interface SheetManager
{
	/**
	 * Adds a sheet model, and returns true in case of success.<br>
	 * If the name already exists, the sheet will not be added, and the method
	 * returns false.
	 * 
	 * @param coreName
	 * @param sheet
	 * @return
	 */
	public boolean addSheet(String coreName, Sheet sheet);
	
	/**
	 * Sheet model associated with this name.<br>
	 * If the name does not exist, the method would throw a
	 * ExpansionRuntimeException to be caught by whatever engine was the cause
	 * of the failed attempt, which would shut itself down.
	 * 
	 * @param coreName
	 * @param sheet
	 * @return
	 */
	public Sheet getSheet(String coreName);

}
