package eu.ha3.mc.haddon.implem;

import java.io.File;

import net.minecraft.src.Minecraft;
import eu.ha3.mc.haddon.Manager;
import eu.ha3.mc.haddon.litemod.LiteBase;

/* x-placeholder-wtfplv2 */

public class HaddonUtilityModLoader extends HaddonUtilityImpl
{
	protected long ticksRan;
	protected File modsFolder;
	
	public HaddonUtilityModLoader(Manager manager)
	{
		super(manager);
	}
	
	@Override
	public long getClientTick()
	{
		return ((LiteBase) this.manager).bridgeTicksRan();
	}
	
	@Override
	public File getModsFolder()
	{
		if (this.modsFolder != null)
			return this.modsFolder;
		
		// else use this 
		this.modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
		return this.modsFolder;
	}
	
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
