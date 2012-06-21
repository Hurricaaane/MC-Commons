package eu.ha3.mc.haddon;

@SuppressWarnings("rawtypes")
public interface Utility
{
	/**
	 * Forces a private value to be read, using the Zero Offset method.
	 * 
	 * @param classToPerformOn
	 *            Class of the object being manipulated
	 * @param instanceToPerformOn
	 *            Object being manipulated
	 * @param zeroOffsets
	 *            Offsets from zero
	 * @return Object to be read
	 * @throws PrivateAccessException
	 *             When the method fails
	 */
	public Object getPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, int zeroOffsets)
					throws PrivateAccessException;
	
	/**
	 * Forces a private value to be set, using the Zero Offset method.
	 * 
	 * @param classToPerformOn
	 *            Class of the object being manipulated
	 * @param instanceToPerformOn
	 *            Object being manipulated
	 * @param zeroOffsets
	 *            Offsets from zero
	 * @param newValue
	 *            New value to override
	 * @return
	 * @throws PrivateAccessException
	 *             When the method fails
	 */
	public void setPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn,
			int zeroOffsets, Object newValue)
					throws PrivateAccessException;
	
	/**
	 * Forces a private value to be read, first using the literal string of the
	 * field, and if it fails, the Zero Offset method.
	 * 
	 * @param classToPerformOn
	 *            Class of the object being manipulated
	 * @param instanceToPerformOn
	 *            Object being manipulated
	 * @param obfPriority
	 *            Literal string of the field, when obfuscated
	 * @param zeroOffsetsDebug
	 *            Offsets from zero as a fallback
	 * @return Object to be read.
	 * @throws PrivateAccessException
	 *             When the method fails twice
	 */
	public Object getPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, String obfPriority,
			int zeroOffsetsDebug) throws PrivateAccessException;
	
	/**
	 * Forces a private value to be set, first using the literal string of the
	 * field, and if it fails, the Zero Offset method.
	 * 
	 * @param classToPerformOn
	 *            Class of the object being manipulated
	 * @param instanceToPerformOn
	 *            Object being manipulated
	 * @param obfPriority
	 *            Literal string of the field, when obfuscated
	 * @param zeroOffsetsDebug
	 *            Offsets from zero as a fallback
	 * @param newValue
	 *            New value to override
	 * @return
	 * @throws PrivateAccessException
	 *             When the method fails twice
	 */
	public void setPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, String obfPriority,
			int zeroOffsetsDebug, Object newValue)
					throws PrivateAccessException;
	
	/**
	 * Returns the world height.<br/>
	 * <br/>
	 * There is no guarantee this method will work when no world is loaded. The
	 * implementation will attempt to make this value vary depending on the
	 * currently loaded world.
	 * 
	 * @return World height
	 */
	public int getWorldHeight();
	
	public Object getCurrentScreen();
	
	public boolean isCurrentScreen(final Class classtype);
	
	public void closeCurrentScreen();
	
	public int getClientTick();
	
	public void printChat(Object... args);
	
}
