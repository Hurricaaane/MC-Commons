package eu.ha3.matmos.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

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

public class MAtmosConditionSet extends MAtmosSwitchable
{
	HashMap<String, Boolean> conditions;
	private boolean isTrueEvaluated;
	
	MAtmosConditionSet(MAtmosKnowledge knowledgeIn)
	{
		super(knowledgeIn);
		isTrueEvaluated = false;
		
		conditions = new HashMap<String, Boolean>();
		
		//setSet(args);
		
	}
	
	@Override
	protected boolean testIfValid()
	{
		if (conditions.size() == 0) return false;
		
		Iterator<String> iterConditions = conditions.keySet().iterator();
		while (iterConditions.hasNext())
		{
			String condition = iterConditions.next();
			
			if (!knowledge.conditions.containsKey(condition))
			{
				return false;
				
			}
			
		}
		
		return true;
		
	}
	
	public void replaceConditionName(String name, String newName)
	{
		flagNeedsTesting();
		
		if (conditions.containsKey(name))
		{
			conditions.put(newName, conditions.get(name));
			conditions.remove(name);
			
		}
		
	}
	
	/**
	 * Sets the set.
	 */
	public void setSet(Object... args) throws IllegalArgumentException
	{
		flagNeedsTesting();
		
		if ((args.length % 2) == 0)
		{
			conditions.clear();
			for (int i = 0; i < (args.length/2); i++)
			{
				conditions.put((String)args[i], (Boolean)args[i+1]);
				
			}
			
		}
		else
		{
			conditions.clear();
			throw new IllegalArgumentException();
			
		}
		
	}
	public void addCondition(String name, boolean truth) throws IllegalArgumentException
	{
		flagNeedsTesting();
		
		conditions.put(name, truth);
		
	}
	public void removeCondition(String name)
	{
		flagNeedsTesting();
		
		conditions.remove(name);
		
	}
	public HashMap<String, Boolean> getSet()
	{
		return conditions;
		
	}
	
	public boolean evaluate()
	{
		if (!isValid())
			return false;
		
		boolean pre = isTrueEvaluated;
		isTrueEvaluated = testIfTrue();
		
		if (pre != isTrueEvaluated)
		{
			//MAtmosEngine.logger; //TODO Logger
			MAtmosLogger.LOGGER.finer(new StringBuilder("S:").append(
					nickname)
					.append(isTrueEvaluated ? " now On." : " now Off.")
					.toString());
			
		}
		
		return isTrueEvaluated;
		
	}
	
	@Override
	public boolean isActive()
	{
		return isTrue();
		
	}
	
	public boolean isTrue()
	{
		return isTrueEvaluated;
		
	}
	public boolean testIfTrue()
	{
		if (!isValid())
			return false;
		
		boolean isTrue = true;
		
		Iterator<Entry<String, Boolean>> iterConditions = conditions.entrySet().iterator();
		while (isTrue && iterConditions.hasNext())
		{
			Entry<String, Boolean> condition = iterConditions.next();
			
			if (condition.getValue() != knowledge.conditions.get(condition.getKey()).isTrue() )
			{
				isTrue = false;
				
			}
			
		}
		return isTrue;
		
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);
		
		for (Iterator<Entry<String, Boolean>> iter = conditions.entrySet().iterator(); iter.hasNext();)
		{
			Entry<String, Boolean> struct = iter.next();
			
			if (struct.getValue() == true)
				createNode(eventWriter, "truepart", struct.getKey());
			
			else
				createNode(eventWriter, "falsepart", struct.getKey());
			
		}
		
		return "";
	}
	
	
}
