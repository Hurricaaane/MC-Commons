package eu.ha3.matmos.engine;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamResult;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

public class MAtmosData
{
	public HashMap<String, ArrayList<Integer>> sheets;
	public int updateVersion;
	
	public MAtmosData()
	{
		sheets = new HashMap<String, ArrayList<Integer>>();
		updateVersion = 0;
		
	}
	public void flagUpdate()
	{
		updateVersion = updateVersion + 1;
		
	}
	
	public String createXML() throws XMLStreamException
	{
		StreamResult serialized = new StreamResult(new StringWriter()); 
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(serialized);

		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");
		XMLEvent end = eventFactory.createDTD("\n");

		eventWriter.add(eventFactory.createStartDocument());
		eventWriter.add(ret);
		eventWriter.add(eventFactory.createStartElement("", "", "contents"));
		for (Iterator<Entry<String, ArrayList<Integer>>> iter = sheets.entrySet().iterator(); iter.hasNext();)
		{
			Entry<String, ArrayList<Integer>> entry = iter.next();
			
			eventWriter.add(ret);
			eventWriter.add(eventFactory.createStartElement("", "", "sheet"));
			eventWriter.add(eventFactory.createAttribute("name", entry.getKey()));
			eventWriter.add(eventFactory.createAttribute("size", entry.getValue().size() + ""));
			eventWriter.add(ret);

			int i = 0;
			for (Iterator<Integer> idter = entry.getValue().iterator(); idter.hasNext();)
			{
				eventWriter.add(tab);
				eventWriter.add(eventFactory.createStartElement("", "", "key"));
				eventWriter.add(eventFactory.createAttribute("id", i + ""));
				eventWriter.add(eventFactory.createCharacters(idter.next().toString()));
				eventWriter.add(eventFactory.createEndElement("", "", "key"));
				eventWriter.add(ret);
				
				i++;
				
			}
			
			eventWriter.add(eventFactory.createEndElement("", "", "sheet"));
		}

		eventWriter.add(ret);
		eventWriter.add(eventFactory.createEndElement("", "", "contents"));
		
		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
		
		
		return serialized.getWriter().toString();
	}
	
}
