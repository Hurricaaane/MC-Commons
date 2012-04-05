package eu.ha3.matmos.engine;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

/**
 * 
 * @author Hurry
 * A Machine is an indexed entity in a Knowledge.
 * 
 * 
 * The purpose of a Machine is to generate noises by the storage of EventTimed.
 * 
 * A Machine can be powered on/off and turned on/off.
 * 
 * Powering a machine on allows its routine to execute events that
 * happen while the Machine is turned off.
 * 
 * A Machine is turned on whenever it is powered and its routine executes,
 * and all of the Restricts are false while any of the Allows is true.
 * 
 * 
 * For a Machine to be valid, it needs to have at least one Allow.
 * 
 */

public class MAtmosMachine extends MAtmosSwitchable
{
	ArrayList<String> anyallows;
	ArrayList<String> anyrestricts;
	
	ArrayList<MAtmosEventTimed> etimes;
	ArrayList<MAtmosStream> streams;
	
	private boolean powered;
	private boolean switchedOn;
	
	MAtmosMachine(MAtmosKnowledge knowledgeIn)
	{
		super(knowledgeIn);
		
		etimes = new ArrayList<MAtmosEventTimed>();
		streams = new ArrayList<MAtmosStream>();
		
		anyallows = new ArrayList<String>();
		anyrestricts = new ArrayList<String>();
		
		powered = false;
		switchedOn = false;
		
		
	}
	
	public void routine()
	{
		if (switchedOn)
		{
			for (Iterator<MAtmosEventTimed> iter = etimes.iterator(); iter.hasNext();)
			{
				MAtmosEventTimed etime = iter.next();
				etime.routine();
				
			}
			
		}
		if (powered && !streams.isEmpty())
		{
			for (Iterator<MAtmosStream> iter = streams.iterator(); iter.hasNext();)
				iter.next().routine();
			
		}
		
	}
	
	/**
	 * Turns the machine on.
	 */
	public void turnOn()
	{
		if (!powered)
			return;
		
		if (switchedOn)
			return;
		
		switchedOn = true;
		for (Iterator<MAtmosEventTimed> iter = etimes.iterator(); iter.hasNext();)
			iter.next().restart();
		
		for (Iterator<MAtmosStream> iter = streams.iterator(); iter.hasNext();)
			iter.next().signalPlayable();
		
	}
	
	/**
	 * Turns the machine off.
	 */
	public void turnOff()
	{
		if (!powered)
			return;
		
		if (!switchedOn)
			return;
		
		switchedOn = false;
		
		for (Iterator<MAtmosStream> iter = streams.iterator(); iter.hasNext();)
			iter.next().signalStoppable();
		
	}
	
	/**
	 * Allows the machine to be turned on.
	 */
	public void powerOn()
	{
		powered = true;
		
	}
	
	/**
	 * Disallows the machine to be turned on, and turns it off.
	 */
	public void powerOff()
	{
		for (Iterator<MAtmosStream> iter = streams.iterator(); iter.hasNext();)
			iter.next().clearToken();
		
		turnOff();
		powered = false;
		
	}
	
	public boolean isPowered()
	{
		return powered;
		
	}
	public boolean isOn()
	{
		return switchedOn;
		
	}
	
	
	public ArrayList<String> getAllows()
	{
		return anyallows;
	}
	public ArrayList<String> getRestricts()
	{
		return anyrestricts;
	}
	public void addAllow(String name)
	{
		/*if (anyrestricts.contains(name))
			return;

		if (anyallows.contains(name))
			return;
		 */
		
		anyallows.add(name);
		flagNeedsTesting();
		
		return;
	}
	public void addRestrict(String name)
	{
		/*if (anyallows.contains(name))
			return;
		
		if (anyrestricts.contains(name))
			return;
		 */
		
		anyrestricts.add(name);
		flagNeedsTesting();
		
		return;
	}
	public void removeSet(String name)
	{
		anyallows.remove(name);
		anyrestricts.remove(name);
		flagNeedsTesting();
		
		return;
		
	}
	public void replaceSetName(String name, String newName)
	{
		if (anyallows.contains(name))
		{
			anyallows.add(newName);
			anyallows.remove(name);
		}
		if (anyrestricts.contains(name))
		{
			anyrestricts.add(newName);
			anyrestricts.remove(name);
		}
		flagNeedsTesting();
		
	}
	
	public ArrayList<MAtmosEventTimed> getEventsTimed()
	{
		return etimes;
		
	}
	public int addEventTimed()
	{
		etimes.add(new MAtmosEventTimed(this));
		
		return etimes.size();
		
	}
	public int removeEventTimed(int index)
	{
		etimes.remove(index);
		
		return etimes.size();
		
	}
	public MAtmosEventTimed getEventTimed(int index)
	{
		return etimes.get(index);
		
	}
	
	public ArrayList<MAtmosStream> getStreams()
	{
		return streams;
		
	}
	public int addStream()
	{
		streams.add(new MAtmosStream(this));
		
		return streams.size();
		
	}
	public int removeStream(int index)
	{
		streams.remove(index);
		
		return streams.size();
		
	}
	public MAtmosStream getStream(int index)
	{
		return streams.get(index);
		
	}
	
	@Override
	protected boolean testIfValid()
	{
		if (anyallows.size() == 0)
			return false;
		
		Iterator<String> iterAnyallows = anyallows.iterator();
		while (iterAnyallows.hasNext())
		{
			String cset = iterAnyallows.next();
			
			if (!knowledge.csets.containsKey(cset))
			{
				return false;
				
			}
			
		}
		
		Iterator<String> iterAnyrestricts = anyrestricts.iterator();
		while (iterAnyrestricts.hasNext())
		{
			String cset = iterAnyrestricts.next();
			
			if (!knowledge.csets.containsKey(cset))
			{
				return false;
				
			}
			
		}
		
		return true;
		
	}
	
	public boolean evaluate()
	{
		if (!isValid())
			return false;
		
		if (!powered)
			return false;
		
		boolean pre = switchedOn;
		boolean shallBeOn = testIfTrue();
		
		if (pre != shallBeOn)
		{
			if (shallBeOn)
				turnOn();
			
			else
				turnOff();
			
			//MAtmosEngine.logger; //TODO Logger
			MAtmosLogger.LOGGER.fine(new StringBuilder("M:").append(
					nickname).append(switchedOn ? " now On." : " now Off.")
					.toString());
			
			
		}
		
		return switchedOn;
		
	}
	
	@Override
	public boolean isActive()
	{
		return isTrue();
		
	}
	public boolean isTrue()
	{
		return switchedOn;
		
	}
	public boolean testIfTrue()
	{
		if (!isValid())
			return false;
		
		boolean isTrue = false;
		
		Iterator<String> iterAnyallows = anyallows.iterator();
		while (!isTrue && iterAnyallows.hasNext())
		{
			String cset = iterAnyallows.next();
			
			if (knowledge.csets.get(cset).isTrue())
			{
				isTrue = true; // If any Allows is true, it's true (exit the loop)
				
			}
			
		}
		
		/// Unless...
		
		Iterator<String> iterAnyrestricts = anyrestricts.iterator();
		while (isTrue && iterAnyrestricts.hasNext())
		{
			String cset = iterAnyrestricts.next();
			
			if (knowledge.csets.get(cset).isTrue())
			{
				isTrue = false; // If any Restrict is true, it's false
				
			}
			
		}
		
		return isTrue;
		
	}
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);
		
		for (Iterator<String> iter = anyallows.iterator(); iter.hasNext();)
			createNode(eventWriter, "allow", iter.next());
		
		for (Iterator<String> iter = anyrestricts.iterator(); iter.hasNext();)
			createNode(eventWriter, "restrict", iter.next());
		
		for (Iterator<MAtmosEventTimed> iter = etimes.iterator(); iter.hasNext();)
			iter.next().serialize(eventWriter);
		
		for (Iterator<MAtmosStream> iter = streams.iterator(); iter.hasNext();)
			iter.next().serialize(eventWriter);
		
		return "";
		
	}
	
}
