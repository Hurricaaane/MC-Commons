package eu.ha3.mc.convenience;

/* x-placeholder-wtfplv2 */

public class Ha3StaticUtilities
{
	/**
	 * Checks if a certain class name exists in a certain object context's class
	 * loader.
	 * 
	 * @param className
	 * @param context
	 * @return
	 */
	public static boolean classExists(String className, Object context)
	{
		boolean canWork = false;
		try
		{
			canWork = Class.forName(className, false, context.getClass().getClassLoader()) != null;
			
		}
		//catch (ClassNotFoundException e)
		//{
		//}
		catch (Exception e)
		{
			// Normally throws checked ClassNotFoundException
			// This also throws unckecked security exceptions
		}
		
		return canWork;
		
	}
	
}
