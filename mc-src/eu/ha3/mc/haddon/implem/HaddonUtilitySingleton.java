package eu.ha3.mc.haddon.implem;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import eu.ha3.mc.haddon.PrivateAccessException;

/* x-placeholder-wtfplv2 */

public class HaddonUtilitySingleton
{
	final static public Logger LOGGER = Logger.getLogger("HaddonUtilitySingleton");
	private static final HaddonUtilitySingleton instance = new HaddonUtilitySingleton();
	
	private Field fieldMod;
	
	private HaddonUtilitySingleton()
	{
		try
		{
			this.fieldMod = java.lang.reflect.Field.class.getDeclaredField("modifiers");
			this.fieldMod.setAccessible(true);
		}
		catch (SecurityException e)
		{
			throw new RuntimeException("haddonUtility critical failure: Security");
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException("haddonUtility critical failure: NoSuchField");
		}
		
	}
	
	public static HaddonUtilitySingleton getInstance()
	{
		return instance;
		
	}
	
	public Field getFieldModifiers()
	{
		return this.fieldMod;
		
	}
	
	@SuppressWarnings("rawtypes")
	public Object getPrivateValue(Class classToPerformOn, Object instanceToPerformOn, int zeroOffsets)
		throws PrivateAccessException
	{
		try
		{
			Field field = classToPerformOn.getDeclaredFields()[zeroOffsets];
			field.setAccessible(true);
			return field.get(instanceToPerformOn);
			
		}
		catch (IllegalAccessException illegalaccessexception)
		{
			throw new RuntimeException("getPrivateValue critical failure: IllegalAccess");
			
		}
		catch (IllegalArgumentException e)
		{
			throw new PrivateAccessException("getPrivateValue has failed: IllegalArgument");
			
		}
		catch (SecurityException e)
		{
			throw new PrivateAccessException("getPrivateValue has failed: Security");
			
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public void setPrivateValue(Class classToPerformOn, Object instanceToPerformOn, int zeroOffsets, Object newValue)
		throws PrivateAccessException
	{
		try
		{
			Field field = classToPerformOn.getDeclaredFields()[zeroOffsets];
			field.setAccessible(true);
			int j = this.fieldMod.getInt(field);
			
			if ((j & 0x10) != 0)
			{
				this.fieldMod.setInt(field, j & 0xffffffef);
			}
			
			field.set(instanceToPerformOn, newValue);
			
		}
		catch (IllegalAccessException illegalaccessexception)
		{
			throw new RuntimeException("getPrivateValue critical failure: IllegalAccess");
			
		}
		catch (IllegalArgumentException e)
		{
			throw new PrivateAccessException("setPrivateValue has failed: IllegalArgument");
			
		}
		catch (SecurityException e)
		{
			throw new PrivateAccessException("setPrivateValue has failed: Security");
			
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public Object getPrivateValueViaName(Class classToPerformOn, Object instanceToPerformOn, String obf)
		throws PrivateAccessException
	{
		try
		{
			Field field = classToPerformOn.getDeclaredField(obf);
			field.setAccessible(true);
			return field.get(instanceToPerformOn);
			
		}
		catch (IllegalAccessException illegalaccessexception)
		{
			throw new RuntimeException("getPrivateValue critical failure: IllegalAccess");
			
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.fine("getPrivateValue has failed: IllegalArgument on field " + obf);
			throw new PrivateAccessException("getPrivateValue has failed: IllegalArgument");
			
		}
		catch (SecurityException e)
		{
			LOGGER.fine("getPrivateValue has failed: Security on field " + obf);
			throw new PrivateAccessException("getPrivateValue has failed: Security");
			
		}
		catch (NoSuchFieldException e)
		{
			LOGGER.fine("getPrivateValue has failed: NoSuchField on field " + obf);
			throw new PrivateAccessException("getPrivateValue has failed: NoSuchField");
			
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public void setPrivateValueViaName(Class classToPerformOn, Object instanceToPerformOn, String obf, Object newValue)
		throws PrivateAccessException
	{
		try
		{
			Field field = classToPerformOn.getDeclaredField(obf);
			field.setAccessible(true);
			int j = this.fieldMod.getInt(field);
			
			if ((j & 0x10) != 0)
			{
				this.fieldMod.setInt(field, j & 0xffffffef);
			}
			
			field.set(instanceToPerformOn, newValue);
		}
		catch (IllegalAccessException illegalaccessexception)
		{
			throw new RuntimeException("getPrivateValue critical failure: IllegalAccess");
			
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.fine("setPrivateValue has failed: IllegalArgument on field " + obf);
			throw new PrivateAccessException("setPrivateValue has failed: IllegalArgument");
			
		}
		catch (SecurityException e)
		{
			LOGGER.fine("setPrivateValue has failed: Security on field " + obf);
			throw new PrivateAccessException("setPrivateValue has failed: Security");
			
		}
		catch (NoSuchFieldException e)
		{
			LOGGER.fine("setPrivateValue has failed: NoSuchField on field " + obf);
			throw new PrivateAccessException("setPrivateValue has failed: NoSuchField");
			
		}
		
	}
	
}
