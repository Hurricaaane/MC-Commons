package net.minecraft.src;

import java.lang.reflect.Field;

import eu.ha3.mc.haddon.PrivateAccessException;

public class HaddonUtilitySingleton
{
	private static final HaddonUtilitySingleton instance = new HaddonUtilitySingleton();
	
	private Field fieldMod;
	
	private HaddonUtilitySingleton()
	{
		try
		{
			fieldMod = (java.lang.reflect.Field.class)
					.getDeclaredField("modifiers");
			fieldMod.setAccessible(true);
		}
		catch (SecurityException e)
		{
			throw new RuntimeException(
					"haddonUtility critical failure: Security");
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException(
					"haddonUtility critical failure: NoSuchField");
		}
		
	}
	
	public static HaddonUtilitySingleton getInstance()
	{
		return instance;
		
	}
	
	public Field getFieldModifiers()
	{
		return fieldMod;
		
	}
	
	@SuppressWarnings("rawtypes")
	public Object getPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, int zeroOffsets)
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
			throw new RuntimeException(
					"getPrivateValue critical failure: IllegalAccess");
			
		}
		catch (IllegalArgumentException e)
		{
			throw new PrivateAccessException(
					"getPrivateValue has failed: IllegalArgument");
			
		}
		catch (SecurityException e)
		{
			throw new PrivateAccessException(
					"getPrivateValue has failed: Security");
			
		}
		
					}
	
	@SuppressWarnings("rawtypes")
	public void setPrivateValue(Class classToPerformOn,
			Object instanceToPerformOn, int zeroOffsets, Object newValue)
					throws PrivateAccessException
					{
		try
		{
			Field field = classToPerformOn.getDeclaredFields()[zeroOffsets];
			field.setAccessible(true);
			int j = fieldMod.getInt(field);
			
			if ((j & 0x10) != 0)
			{
				fieldMod.setInt(field, j & 0xffffffef);
			}
			
			field.set(instanceToPerformOn, newValue);
			
		}
		catch (IllegalAccessException illegalaccessexception)
		{
			throw new RuntimeException(
					"getPrivateValue critical failure: IllegalAccess");
			
		}
		catch (IllegalArgumentException e)
		{
			throw new PrivateAccessException(
					"setPrivateValue has failed: IllegalArgument");
			
		}
		catch (SecurityException e)
		{
			throw new PrivateAccessException(
					"setPrivateValue has failed: Security");
			
		}
		
					}
	
	@SuppressWarnings("rawtypes")
	public Object getPrivateValueViaName(Class classToPerformOn,
			Object instanceToPerformOn, String obf)
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
			throw new RuntimeException(
					"getPrivateValue critical failure: IllegalAccess");
			
		}
		catch (IllegalArgumentException e)
		{
			throw new PrivateAccessException(
					"getPrivateValue has failed: IllegalArgument");
			
		}
		catch (SecurityException e)
		{
			throw new PrivateAccessException(
					"getPrivateValue has failed: Security");
			
		}
		catch (NoSuchFieldException e)
		{
			throw new PrivateAccessException(
					"getPrivateValue has failed: NoSuchField");
			
		}
		
					}
	
	@SuppressWarnings("rawtypes")
	public void setPrivateValueViaName(Class classToPerformOn,
			Object instanceToPerformOn, String obf, Object newValue)
					throws PrivateAccessException
					{
		try
		{
			Field field = classToPerformOn.getDeclaredField(obf);
			field.setAccessible(true);
			int j = fieldMod.getInt(field);
			
			if ((j & 0x10) != 0)
			{
				fieldMod.setInt(field, j & 0xffffffef);
			}
			
			field.set(instanceToPerformOn, newValue);
		}
		catch (IllegalAccessException illegalaccessexception)
		{
			throw new RuntimeException(
					"getPrivateValue critical failure: IllegalAccess");
			
		}
		catch (IllegalArgumentException e)
		{
			throw new PrivateAccessException(
					"setPrivateValue has failed: IllegalArgument");
			
		}
		catch (SecurityException e)
		{
			throw new PrivateAccessException(
					"setPrivateValue has failed: Security");
			
		}
		catch (NoSuchFieldException e)
		{
			throw new PrivateAccessException(
					"setPrivateValue has failed: NoSuchField");
			
		}
		
					}
	
}
