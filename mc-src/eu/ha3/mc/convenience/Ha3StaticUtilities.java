package eu.ha3.mc.convenience;

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
			canWork = Class.forName(className, false, context.getClass()
					.getClassLoader()) != null;
			
		}
		catch (ClassNotFoundException e)
		{
			canWork = false;
		}
		
		return canWork;
		
	}
	
}
