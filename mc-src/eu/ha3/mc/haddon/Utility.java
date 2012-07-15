package eu.ha3.mc.haddon;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

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
	public Object getPrivateValueLiteral(Class classToPerformOn,
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
	public void setPrivateValueLiteral(Class classToPerformOn,
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
	
	public boolean areKeysDown(int... args);
	
}
