package eu.ha3.matmos.engine;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamResult;

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

/**
 * Stores a Knowledge.
 */
public class MAtmosKnowledge
{
	HashMap<String, MAtmosDynamic> dynamics;
	HashMap<String, MAtmosList> lists;
	
	HashMap<String, MAtmosCondition> conditions;
	HashMap<String, MAtmosConditionSet> csets;
	HashMap<String, MAtmosMachine> machines;
	
	HashMap<String, MAtmosEvent> events;
	
	MAtmosData data;
	MAtmosSoundManager soundManager;
	MAtmosClock clock;
	
	private boolean isRunning;
	int dataLastVersion;
	
	Random random;
	
	public MAtmosKnowledge()
	{
		data = new MAtmosData();
		soundManager = null;
		
		dataLastVersion = 0;
		isRunning = false;
		
		random = new Random(System.currentTimeMillis());
		
		clock = new MAtmosClock();
		
		patchKnowledge();
		
	}
	
	/**
	 * Closes the Knowledge, annihilates all references to libraries of objects
	 * from the current knowledge, and instantiates new ones.<br>
	 * <br>
	 * This does not clear the previously stored libraries, in order to preserve
	 * the integrity of keyrings.<br>
	 * This renews the library by creating a new object, so any referenced
	 * library from another object will keep its integrity.
	 * 
	 */
	public void patchKnowledge()
	{
		turnOff();
		
		dynamics = new HashMap<String, MAtmosDynamic>();
		lists = new HashMap<String, MAtmosList>();
		
		conditions = new HashMap<String, MAtmosCondition>();
		csets = new HashMap<String, MAtmosConditionSet>();
		machines = new HashMap<String, MAtmosMachine>();
		
		events = new HashMap<String, MAtmosEvent>();
		
	}
	
	public void turnOn()
	{
		if (soundManager == null)
			return;
		
		if (isRunning)
			return;
		
		reclaimKeyring();
		isRunning = true;
		
		// FIXME Why do i have to do that -> look at the UML sheet
		// Machines have to be powered on for their routines to run even if the machines are turned off
		for (MAtmosMachine machine : machines.values())
		{
			machine.powerOn();
			
		}
		
	}
	
	public void turnOff()
	{
		if (!isRunning)
			return;
		
		isRunning = false;
		
		// FIXME Why do i have to do that -> look at the UML sheet
		// Machines have to be powered on for their routines to run even if the machines are turned off
		for (MAtmosMachine machine : machines.values())
		{
			machine.powerOff();
			
		}
		
	}
	
	public boolean isTurnedOn()
	{
		return isRunning;
		
	}
	
	public Set<String> getDynamicsKeySet()
	{
		return dynamics.keySet();
		
	}
	
	public Set<String> getListsKeySet()
	{
		return lists.keySet();
		
	}
	
	public Set<String> getConditionsKeySet()
	{
		return conditions.keySet();
		
	}
	
	public Set<String> getConditionSetsKeySet()
	{
		return csets.keySet();
		
	}
	
	public Set<String> getMachinesKeySet()
	{
		return machines.keySet();
		
	}
	
	public Set<String> getEventsKeySet()
	{
		return events.keySet();
		
	}
	
	/**
	 * Makes sure referenced database uses this knowledge.
	 */
	public void reclaimKeyring()
	{
		turnOff();
		
		for (MAtmosDynamic dynamic : dynamics.values())
			dynamic.setKnowledge(this);
		
		// Lists don't have to be tied with the knowledge
		
		for (MAtmosCondition condition : conditions.values())
			condition.setKnowledge(this);
		
		for (MAtmosConditionSet cset : csets.values())
			cset.setKnowledge(this);
		
		for (MAtmosMachine machine : machines.values())
			machine.setKnowledge(this);
		
		for (MAtmosEvent event : events.values())
			event.setKnowledge(this);
		
	}
	
	/**
	 * Gets from originalKnowledge a keyring of the database referencing the
	 * original database objects.
	 */
	@SuppressWarnings("unchecked")
	public void retreiveKeyring(MAtmosKnowledge originalKnowledge)
	{
		if (originalKnowledge.isRunning)
			return;
		
		dynamics = (HashMap<String, MAtmosDynamic>) originalKnowledge.dynamics.clone();
		lists = (HashMap<String, MAtmosList>) originalKnowledge.lists.clone();
		conditions = (HashMap<String, MAtmosCondition>) originalKnowledge.conditions.clone();
		csets = (HashMap<String, MAtmosConditionSet>) originalKnowledge.csets.clone();
		machines = (HashMap<String, MAtmosMachine>) originalKnowledge.machines.clone();
		events = (HashMap<String, MAtmosEvent>) originalKnowledge.events.clone();
		reclaimKeyring();
		
	}
	
	public void setSoundManager(MAtmosSoundManager soundManagerIn)
	{
		soundManager = soundManagerIn;
		
	}
	
	public void cacheSounds()
	{
		for (MAtmosEvent event : events.values())
			event.cacheSounds();
		
	}
	
	public void setClock(MAtmosClock clockIn)
	{
		clock = clockIn;
	}
	
	public void setData(MAtmosData dataIn)
	{
		data = dataIn;
		applySheetFlagNeedsTesting();
	}
	
	public long getTimeMillis()
	{
		return clock.getTimeMillis();
		
	}
	
	void applySheetFlagNeedsTesting()
	{
		for (MAtmosCondition condition : conditions.values())
			condition.flagNeedsTesting();
		
		for (MAtmosDynamic dynamic : dynamics.values())
			dynamic.flagNeedsTesting();
		
	}
	
	public MAtmosEvent getEvent(String name)
	{
		return events.get(name);
		
	}
	
	public boolean addEvent(String name)
	{
		if (events.containsKey(name))
			return false;
		
		events.put(name, new MAtmosEvent(this));
		events.get(name).nickname = name;
		
		return true;
		
	}
	
	public boolean removeEvent(String name)
	{
		if (!events.containsKey(name))
			return false;
		
		events.remove(name);
		
		return true;
		
	}
	
	public boolean renameEvent(String name, String newName)
	{
		if (!events.containsKey(name))
			return false; // Error?
		
		if (events.containsKey(newName))
			return false;
		
		events.put(newName, events.get(name));
		events.remove(name);
		events.get(newName).nickname = newName;
		
		for (MAtmosMachine machine : machines.values())
		{
			// TODO Make this a method of MAtmosMachine
			for (MAtmosEventTimed etime : machine.etimes)
			{
				if (etime.event.equals(name))
					etime.event = newName;
				
			}
			
		}
		
		return true;
		
	}
	
	void applyDynamicFlagNeedsTesting()
	{
		for (MAtmosCondition condition : conditions.values())
		{
			condition.flagNeedsTesting();
			
		}
		
	}
	
	public MAtmosDynamic getDynamic(String name)
	{
		return dynamics.get(name);
		
	}
	
	public boolean addDynamic(String name)
	{
		if (dynamics.containsKey(name))
			return false;
		
		dynamics.put(name, new MAtmosDynamic(this));
		dynamics.get(name).nickname = name;
		
		applyDynamicFlagNeedsTesting();
		
		return true;
		
	}
	
	public boolean removeDynamic(String name)
	{
		if (!dynamics.containsKey(name))
			return false;
		
		dynamics.remove(name);
		
		applyDynamicFlagNeedsTesting();
		
		return true;
		
	}
	
	public boolean renameDynamic(String name, String newName)
	{
		if (!dynamics.containsKey(name))
			return false; // Error?
		
		if (dynamics.containsKey(newName))
			return false;
		
		dynamics.put(newName, dynamics.get(name));
		dynamics.remove(name);
		dynamics.get(newName).nickname = newName;
		
		for (MAtmosCondition condition : conditions.values())
			condition.replaceDynamicName(name, newName);
		
		return true;
		
	}
	
	void applyListFlagNeedsTesting()
	{
		for (MAtmosCondition condition : conditions.values())
		{
			condition.flagNeedsTesting();
			
		}
		
	}
	
	public MAtmosList getList(String name)
	{
		return lists.get(name);
		
	}
	
	public boolean addList(String name)
	{
		if (lists.containsKey(name))
			return false;
		
		lists.put(name, new MAtmosList());
		lists.get(name).nickname = name;
		
		applyDynamicFlagNeedsTesting();
		
		return true;
		
	}
	
	public boolean removeList(String name)
	{
		if (!lists.containsKey(name))
			return false;
		
		lists.remove(name);
		
		applyDynamicFlagNeedsTesting();
		
		return true;
		
	}
	
	public boolean renameList(String name, String newName)
	{
		if (!lists.containsKey(name))
			return false; // Error?
		
		if (lists.containsKey(newName))
			return false;
		
		lists.put(newName, lists.get(name));
		lists.remove(name);
		lists.get(newName).nickname = newName;
		
		for (MAtmosCondition condition : conditions.values())
			condition.replaceListName(name, newName);
		
		return true;
		
	}
	
	void applyDataConditionNeedsTesting()
	{
		for (MAtmosConditionSet cset : csets.values())
			cset.flagNeedsTesting();
		
	}
	
	public MAtmosCondition getDataCondition(String name)
	{
		return conditions.get(name);
		
	}
	
	public boolean addDataCondition(String name)
	{
		if (conditions.containsKey(name))
			return false;
		
		conditions.put(name, new MAtmosCondition(this));
		conditions.get(name).nickname = name;
		
		applyDataConditionNeedsTesting();
		
		return true;
		
	}
	
	public boolean renameDataCondition(String name, String newName)
	{
		if (!conditions.containsKey(name))
			return false;
		
		if (conditions.containsKey(newName))
			return false;
		
		conditions.put(newName, conditions.get(name));
		conditions.remove(name);
		conditions.get(newName).nickname = newName;
		
		for (MAtmosConditionSet cset : csets.values())
			cset.replaceConditionName(name, newName);
		
		applyDataConditionNeedsTesting();
		
		return true;
		
	}
	
	public boolean removeDataCondition(String name)
	{
		if (!conditions.containsKey(name))
			return false;
		
		conditions.remove(name);
		
		applyDataConditionNeedsTesting();
		
		return true;
		
	}
	
	void applyConditionSetNeedsTesting()
	{
		for (MAtmosMachine machine : machines.values())
			machine.flagNeedsTesting();
		
	}
	
	public MAtmosConditionSet getConditionSet(String name)
	{
		return csets.get(name);
	}
	
	public boolean addConditionSet(String name)
	{
		if (csets.containsKey(name))
			return false;
		
		csets.put(name, new MAtmosConditionSet(this));
		csets.get(name).nickname = name;
		
		applyConditionSetNeedsTesting();
		
		return true;
		
	}
	
	public boolean renameConditionSet(String name, String newName)
	{
		if (!csets.containsKey(name))
			return false;
		
		if (csets.containsKey(newName))
			return false;
		
		csets.put(newName, csets.get(name));
		csets.remove(name);
		csets.get(newName).nickname = newName;
		
		for (MAtmosMachine machine : machines.values())
		{
			machine.replaceSetName(name, newName);
			
		}
		
		applyConditionSetNeedsTesting();
		
		return true;
		
	}
	
	public boolean removeConditionSet(String name)
	{
		if (!csets.containsKey(name))
		{
			// MAtmosEngine.logger;
			// Not an exception!
			return false;
			
		}
		
		csets.remove(name);
		
		applyConditionSetNeedsTesting();
		
		return true;
		
	}
	
	void applyMachineNeedsTesting()
	{
		// Do nothing
	}
	
	public MAtmosMachine getMachine(String name)
	{
		return machines.get(name);
	}
	
	public boolean addMachine(String name)
	{
		if (machines.containsKey(name))
			return false;
		
		machines.put(name, new MAtmosMachine(this));
		machines.get(name).nickname = name;
		
		applyMachineNeedsTesting();
		
		return true;
		
	}
	
	public boolean removeMachine(String name)
	{
		if (!machines.containsKey(name))
			return false;
		
		machines.remove(name);
		
		applyMachineNeedsTesting();
		
		return true;
		
	}
	
	public boolean renameMachine(String name, String newName)
	{
		if (!machines.containsKey(name))
			return false;
		
		if (machines.containsKey(newName))
			return false;
		
		machines.put(newName, machines.get(name));
		machines.remove(name);
		machines.get(newName).nickname = newName;
		
		// Nothing to do!
		
		applyConditionSetNeedsTesting();
		
		return true;
		
	}
	
	public void routine()
	{
		if (!isRunning)
			return;
		
		if (dataLastVersion != data.updateVersion)
		{
			evaluate();
			dataLastVersion = data.updateVersion;
			
		}
		
		soundManager.routine();
		for (Iterator<MAtmosMachine> iter = machines.values().iterator(); iter.hasNext();)
		{
			iter.next().routine();
			
		}
		
	}
	
	public void soundRoutine()
	{
		if (!isRunning)
			return;
		
		soundManager.routine();
		for (Iterator<MAtmosMachine> iter = machines.values().iterator(); iter
				.hasNext();)
		{
			iter.next().routine();
			
		}
		
	}
	
	public void dataRoutine()
	{
		if (!isRunning)
			return;
		
		if (dataLastVersion != data.updateVersion)
		{
			evaluate();
			dataLastVersion = data.updateVersion;
			
		}
		
	}
	
	void evaluate()
	{
		if (!isRunning) // The keyring may not be reclaimed: If running then it must have been reclaimed. Do not perform if not running.
			return;
		
		for (MAtmosDynamic dynamic : dynamics.values())
		{
			dynamic.evaluate();
			
		}
		// Lists don't have to be tied with the knowledge
		for (MAtmosCondition condition : conditions.values())
		{
			condition.evaluate();
			
		}
		for (MAtmosConditionSet cset : csets.values())
		{
			cset.evaluate();
			
		}
		for (MAtmosMachine machine : machines.values())
		{
			machine.evaluate();
			
		}
		
	}
	
	public String createXML() throws XMLStreamException
	{
		StreamResult serialized = new StreamResult(new StringWriter());
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(serialized);
		
		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent end = eventFactory.createDTD("\n");
		
		Object[] keysArray;
		
		eventWriter.add(eventFactory.createStartDocument());
		eventWriter.add(ret);
		eventWriter.add(eventFactory.createStartElement("", "", "contents"));
		
		keysArray = dynamics.keySet().toArray();
		Arrays.sort(keysArray);
		for (int i = 0; i < keysArray.length; i++)
		{
			String name = keysArray[i].toString();
			
			eventWriter.add(ret);
			eventWriter.add(eventFactory.createStartElement("", "", "dynamic"));
			eventWriter.add(eventFactory.createAttribute("name", name));
			eventWriter.add(ret);
			dynamics.get(name).serialize(eventWriter);
			eventWriter.add(eventFactory.createEndElement("", "", "dynamic"));
			
		}
		keysArray = lists.keySet().toArray();
		Arrays.sort(keysArray);
		for (int i = 0; i < keysArray.length; i++)
		{
			String name = keysArray[i].toString();
			
			eventWriter.add(ret);
			eventWriter.add(eventFactory.createStartElement("", "", "list"));
			eventWriter.add(eventFactory.createAttribute("name", name));
			eventWriter.add(ret);
			lists.get(name).serialize(eventWriter);
			eventWriter.add(eventFactory.createEndElement("", "", "list"));
			
		}
		keysArray = conditions.keySet().toArray();
		Arrays.sort(keysArray);
		for (int i = 0; i < keysArray.length; i++)
		{
			String name = keysArray[i].toString();
			
			eventWriter.add(ret);
			eventWriter.add(eventFactory.createStartElement("", "", "condition"));
			eventWriter.add(eventFactory.createAttribute("name", name));
			eventWriter.add(ret);
			conditions.get(name).serialize(eventWriter);
			eventWriter.add(eventFactory.createEndElement("", "", "condition"));
			
		}
		keysArray = csets.keySet().toArray();
		Arrays.sort(keysArray);
		for (int i = 0; i < keysArray.length; i++)
		{
			String name = keysArray[i].toString();
			
			eventWriter.add(ret);
			eventWriter.add(eventFactory.createStartElement("", "", "set"));
			eventWriter.add(eventFactory.createAttribute("name", name));
			eventWriter.add(ret);
			csets.get(name).serialize(eventWriter);
			eventWriter.add(eventFactory.createEndElement("", "", "set"));
			
		}
		keysArray = events.keySet().toArray();
		Arrays.sort(keysArray);
		for (int i = 0; i < keysArray.length; i++)
		{
			String name = keysArray[i].toString();
			
			eventWriter.add(ret);
			eventWriter.add(eventFactory.createStartElement("", "", "event"));
			eventWriter.add(eventFactory.createAttribute("name", name));
			eventWriter.add(ret);
			events.get(name).serialize(eventWriter);
			eventWriter.add(eventFactory.createEndElement("", "", "event"));
			
		}
		keysArray = machines.keySet().toArray();
		Arrays.sort(keysArray);
		for (int i = 0; i < keysArray.length; i++)
		{
			String name = keysArray[i].toString();
			
			eventWriter.add(ret);
			eventWriter.add(eventFactory.createStartElement("", "", "machine"));
			eventWriter.add(eventFactory.createAttribute("name", name));
			eventWriter.add(ret);
			machines.get(name).serialize(eventWriter);
			eventWriter.add(eventFactory.createEndElement("", "", "machine"));
			
		}
		
		eventWriter.add(ret);
		eventWriter.add(eventFactory.createEndElement("", "", "contents"));
		
		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
		
		return serialized.getWriter().toString();
		
	}
	/*
	public String diffXML(MAtmosKnowledge base) throws XMLStreamException
	{
		StreamResult serialized = new StreamResult(new StringWriter());
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(serialized);

		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent end = eventFactory.createDTD("\n");
		
		eventWriter.add(eventFactory.createStartDocument());
		eventWriter.add(ret);
		eventWriter.add(eventFactory.createStartElement("", "", "contents"));
		
		
		
		for (Iterator<Entry<String,MAtmosDynamic>> iter = dynamics.entrySet().iterator(); iter.hasNext();)
		{
			Entry<String,MAtmosDynamic> entry = iter.next();
			boolean addMe = false;
			
			if (base.getDynamic( entry.getKey() ) == null)
			{
				addMe = true;
				
			}
			else
			
		}
		
		
		return "";
		
	}
	 */
	
}
