package eu.ha3.mc.haddon.implem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.ha3.mc.haddon.PrivateAccessException;

/*
--filenotes-placeholder
*/

public class HaddonPrivateEntry implements PrivateEntry
{
	private final String name;
	@SuppressWarnings("rawtypes")
	private final Class target;
	private final int zero;
	private final String[] fieldNames;
	private final List<String> fieldNamesMoreToLess_depleting;
	
	@SuppressWarnings("rawtypes")
	public HaddonPrivateEntry(String name, Class target, int zero, String... lessToMoreImportantFieldName)
	{
		this.name = name;
		this.target = target;
		this.zero = zero;
		this.fieldNames = lessToMoreImportantFieldName.clone();
		this.fieldNamesMoreToLess_depleting = new ArrayList<String>(Arrays.asList(this.fieldNames));
		Collections.reverse(this.fieldNamesMoreToLess_depleting);
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Class getTarget()
	{
		return this.target;
	}
	
	@Override
	public int getZero()
	{
		return this.zero;
	}
	
	@Override
	public String[] getFieldNames()
	{
		return this.fieldNames;
	}
	
	@Override
	public Object get(Object instance) throws PrivateAccessException
	{
		while (!this.fieldNamesMoreToLess_depleting.isEmpty())
		{
			try
			{
				return HaddonUtilitySingleton.getInstance().getPrivateValueViaName(
					this.target, instance, this.fieldNamesMoreToLess_depleting.get(0));
			}
			catch (PrivateAccessException e)
			{
				HaddonUtilitySingleton.LOGGER.info("(Haddon) PrivateEntry "
					+ this.name + " cannot resolve " + this.fieldNamesMoreToLess_depleting.get(0));
				this.fieldNamesMoreToLess_depleting.remove(0);
			}
		}
		if (this.zero >= 0)
		{
			try
			{
				return HaddonUtilitySingleton.getInstance().getPrivateValue(this.target, instance, this.zero);
			}
			catch (PrivateAccessException e)
			{
				HaddonUtilitySingleton.LOGGER.info("(Haddon) PrivateEntry "
					+ this.name + " cannot resolve zero-index " + this.zero);
			}
		}
		
		generateError(); // will throw an exception
		return null;
	}
	
	@Override
	public void set(Object instance, Object value) throws PrivateAccessException
	{
		int i = this.fieldNames.length - 1;
		while (i >= 0)
		{
			try
			{
				HaddonUtilitySingleton.getInstance().setPrivateValueViaName(
					this.target, instance, this.fieldNames[i], value);
				return;
			}
			catch (PrivateAccessException e)
			{
			}
			i = i - 1;
		}
		if (this.zero >= 0)
		{
			try
			{
				HaddonUtilitySingleton.getInstance().setPrivateValue(this.target, instance, this.zero, value);
				return;
			}
			catch (PrivateAccessException e)
			{
			}
		}
		
		generateError(); // will throw an exception
	}
	
	private void generateError() throws PrivateAccessException
	{
		StringBuilder sb = new StringBuilder();
		for (int j = this.fieldNames.length - 1; j >= 0; j--)
		{
			sb.append(this.fieldNames[j]);
			sb.append(",");
		}
		sb.append("[").append(this.zero).append("]");
		throw new PrivateAccessException(this.name + "(" + sb + ") could not be resolved");
	}
}
