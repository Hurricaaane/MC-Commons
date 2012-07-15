package eu.ha3.matmos.engine;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

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

public class MAtmosEventTimed extends MAtmosDescriptible
{
	MAtmosMachine machine;
	
	public String event;
	
	public float volMod;
	public float pitchMod;
	
	public float delayMin;
	public float delayMax;
	
	public float delayStart;
	
	public long nextPlayTime;
	
	MAtmosEventTimed(MAtmosMachine machineIn)
	{
		//event = eventIn;
		
		event = "";
		
		machine = machineIn;
		volMod = 1F;
		pitchMod = 1F;
		
		delayMin = 10F;
		delayMax = 10F;
		
		delayStart = 0F;
		
	}
	void setMachine(MAtmosMachine machineIn)
	{
		machine = machineIn;
		
	}
	public void routine()
	{
		if (machine.knowledge.getTimeMillis() < nextPlayTime)
			return;
		
		if (machine.knowledge.events.containsKey(event))
			machine.knowledge.events.get(event).playSound(volMod, pitchMod);
		
		if ((delayMin == delayMax) && (delayMin > 0))
		{
			while (nextPlayTime < machine.knowledge.getTimeMillis())
			{
				nextPlayTime = nextPlayTime + (long) (delayMin * 1000);
				
			}
			
		}
		else
			nextPlayTime = machine.knowledge.getTimeMillis()
			+ (long) ((delayMin + machine.knowledge.random.nextFloat()
					* (delayMax - delayMin)) * 1000);
		
	}
	public void restart()
	{
		if (delayStart == 0)
			nextPlayTime = machine.knowledge.getTimeMillis() + (long)(machine.knowledge.random.nextFloat() * delayMax * 1000);
		
		else
			nextPlayTime = machine.knowledge.getTimeMillis() + (long)(delayStart * 1000);
		
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");
		
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createStartElement("", "", "eventtimed"));
		eventWriter.add(ret);
		createNode(eventWriter, "eventname", event, 2);
		createNode(eventWriter, "delaymin", "" + delayMin, 2);
		createNode(eventWriter, "delaymax", "" + delayMax, 2);
		createNode(eventWriter, "delaystart", "" + delayStart, 2);
		createNode(eventWriter, "volmod", "" + volMod, 2);
		createNode(eventWriter, "pitchmod", "" + pitchMod, 2);
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createEndElement("", "", "eventtimed"));
		eventWriter.add(ret);
		
		return "";
	}
	
}
