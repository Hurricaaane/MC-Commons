package eu.ha3.matmos3.units.model;

import eu.ha3.matmos3.engine.model.Engine;

public interface Unit
{
	/**
	 * Classname of the unit.
	 * 
	 * @return
	 */
	public String getClassname();
	
	/**
	 * ID of the instanced unit.
	 * 
	 * @return
	 */
	public int getID();
	
	/**
	 * Name of the instanced unit.
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Engine associated with the instanced unit.
	 * 
	 * @return
	 */
	public Engine getEngine();
	
	/**
	 * Priority level of the unit on the evaluation process.
	 * 
	 * @return
	 */
	public Priority getPriority();
	
	/**
	 * Evaluate the unit.
	 */
	public void evaluate();
	
}
