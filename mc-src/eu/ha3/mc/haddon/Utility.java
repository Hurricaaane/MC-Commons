package eu.ha3.mc.haddon;

import java.io.File;

/* x-placeholder-wtfplv2 */

@SuppressWarnings("rawtypes")
public interface Utility
{
	/**
	 * Register a Private Access getter on a certain name, that operates on a
	 * certain Class in a certain Object instance. The last two arguments are
	 * increasingly of priority: The rightmost argument is evaluated first.<br>
	 * When used by getPrivate(...):<br>
	 * The rightmost fieldname (if not null) is used first. If it doesn't work,
	 * the arguments left to it are used. If everything fails, it uses the
	 * zeroOffsets, that is the nth field (0th being the first field).
	 * zeroOffsets is ignored if it is a negative number. If none worked, this
	 * throws a PrivateAccessException containing the name of that getter.
	 * 
	 * @param name
	 * @param classToPerformOn
	 * @param zeroOffsets
	 * @param lessToMoreImportantFieldName
	 */
	public void registerPrivateGetter(
		String name, Class classToPerformOn, int zeroOffsets, String... lessToMoreImportantFieldName);
	
	/**
	 * Register a Private Access setter on a certain name, that operates on a
	 * certain Class in a certain Object instance. The last two arguments are
	 * increasingly of priority: The rightmost argument is evaluated first.<br>
	 * When used by getPrivate(...):<br>
	 * The rightmost fieldname (if not null) is used first. If it doesn't work,
	 * the arguments left to it are used. If everything fails, it uses the
	 * zeroOffsets, that is the nth field (0th being the first field).
	 * zeroOffsets is ignored if it is a negative number. If none worked, this
	 * throws a PrivateAccessException containing the name of that setter.
	 * 
	 * @param name
	 * @param classToPerformOn
	 * @param zeroOffsets
	 * @param lessToMoreImportantFieldName
	 */
	public void registerPrivateSetter(
		String name, Class classToPerformOn, int zeroOffsets, String... lessToMoreImportantFieldName);
	
	/**
	 * Gets a registered Private field
	 * 
	 * @param instance
	 * @param name
	 * @return
	 * @throws PrivateAccessException
	 */
	public Object getPrivate(Object instance, String name) throws PrivateAccessException;
	
	/**
	 * Sets a registered Private field
	 * 
	 * @param instance
	 * @param name
	 * @param value
	 * @throws PrivateAccessException
	 */
	public void setPrivate(Object instance, String name, Object value) throws PrivateAccessException;
	
	/**
	 * Forces a private value to be read, using the Zero Offset method.
	 * 
	 * @param classToPerformOn Class of the object being manipulated
	 * @param instanceToPerformOn Object being manipulated
	 * @param zeroOffsets Offsets from zero
	 * @return Object to be read
	 * @throws PrivateAccessException When the method fails
	 */
	@Deprecated
	public Object getPrivateValue(Class classToPerformOn, Object instanceToPerformOn, int zeroOffsets)
		throws PrivateAccessException;
	
	/**
	 * Forces a private value to be set, using the Zero Offset method.
	 * 
	 * @param classToPerformOn Class of the object being manipulated
	 * @param instanceToPerformOn Object being manipulated
	 * @param zeroOffsets Offsets from zero
	 * @param newValue New value to override
	 * @throws PrivateAccessException When the method fails
	 */
	@Deprecated
	public void setPrivateValue(Class classToPerformOn, Object instanceToPerformOn, int zeroOffsets, Object newValue)
		throws PrivateAccessException;
	
	/**
	 * Forces a private value to be read, first using the literal string of the
	 * field, and if it fails, the Zero Offset method.
	 * 
	 * @param classToPerformOn Class of the object being manipulated
	 * @param instanceToPerformOn Object being manipulated
	 * @param obfPriority Literal string of the field, when obfuscated
	 * @param zeroOffsetsDebug Offsets from zero as a fallback
	 * @return Object to be read.
	 * @throws PrivateAccessException When the method fails twice
	 */
	@Deprecated
	public Object getPrivateValueLiteral(
		Class classToPerformOn, Object instanceToPerformOn, String obfPriority, int zeroOffsetsDebug)
		throws PrivateAccessException;
	
	/**
	 * Forces a private value to be set, first using the literal string of the
	 * field, and if it fails, the Zero Offset method.
	 * 
	 * @param classToPerformOn Class of the object being manipulated
	 * @param instanceToPerformOn Object being manipulated
	 * @param obfPriority Literal string of the field, when obfuscated
	 * @param zeroOffsetsDebug Offsets from zero as a fallback
	 * @param newValue New value to override
	 * @throws PrivateAccessException When the method fails twice
	 */
	@Deprecated
	public void setPrivateValueLiteral(
		Class classToPerformOn, Object instanceToPerformOn, String obfPriority, int zeroOffsetsDebug, Object newValue)
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
	
	/**
	 * Returns the Mods directory
	 * 
	 * @return Mods directory
	 */
	public File getModsFolder();
	
	public Object getCurrentScreen();
	
	public boolean isCurrentScreen(final Class classtype);
	
	public void closeCurrentScreen();
	
	public long getClientTick();
	
	public void printChat(Object... args);
	
	public boolean areKeysDown(int... args);
	
	/**
	 * Prepares a drawString sequence.
	 */
	public void prepareDrawString();
	
	/**
	 * Draws a string on-screen using viewport size percentages.<br>
	 * Alignment is a number between 1 and 9. It corresponds to the key position
	 * of a classic layout keyboard numpad. For instance, 7 means "top left",
	 * because the key "7" is at the top left.
	 * 
	 * @param text
	 * @param px
	 * @param py
	 * @param offx
	 * @param offy
	 * @param alignment Number from 1 to 9 corresponding to numpad position on a
	 *            keyboard (not a phone).
	 * @param cr Red color 0-255
	 * @param cg
	 * @param cb
	 * @param ca
	 * @param hasShadow
	 */
	public void drawString(
		String text, float px, float py, int offx, int offy, char alignment, int cr, int cg, int cb, int ca,
		boolean hasShadow);
	
}
