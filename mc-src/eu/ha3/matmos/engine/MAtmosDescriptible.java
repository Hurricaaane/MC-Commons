package eu.ha3.matmos.engine;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

public abstract class MAtmosDescriptible
{
	public String nickname = new String();
	public String description = new String();
	
	public String icon = new String();
	
	public String meta = new String();
	
	public abstract String serialize(XMLEventWriter eventWriter) throws XMLStreamException;
	
	@Override
	public String toString()
	{
		return "[(" + this.getClass().toString() + ") " + nickname + "]";
		
	}

	protected void buildDescriptibleSerialized(XMLEventWriter eventWriter) throws XMLStreamException
	{
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");
		
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createStartElement("", "", "descriptible"));
		eventWriter.add(ret);
		createNode(eventWriter, "nickname", nickname, 2);
		createNode(eventWriter, "description", description, 2);
		createNode(eventWriter, "icon", icon, 2);
		createNode(eventWriter, "meta", meta, 2);
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createEndElement("", "", "descriptible"));
		eventWriter.add(ret);
		
	}
	protected void createNode(XMLEventWriter eventWriter, String name,
			String value, int tabCount) throws XMLStreamException
			{
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent tab = eventFactory.createDTD("\t");
		XMLEvent end = eventFactory.createDTD("\n");
		
		// Create Start node
		/*StartElement sElement = eventFactory.createStartElement("", "", name);
		for (int i=0;i<tabCount;i++)
			eventWriter.add(tab);
		eventWriter.add(sElement);
		// Create Content
		Characters characters = eventFactory.createCharacters(value);
		eventWriter.add(characters);
		// Create End node
		EndElement eElement = eventFactory.createEndElement("", "", name);
		eventWriter.add(eElement);
		eventWriter.add(end);*/
		
		for (int i=0;i<tabCount;i++)
			eventWriter.add(tab);
		
		eventWriter.add(eventFactory.createStartElement("", "", name));
		eventWriter.add(eventFactory.createCharacters(value));
		eventWriter.add(eventFactory.createEndElement("", "", name));
		eventWriter.add(end);
		
			}
	protected void createNode(XMLEventWriter eventWriter, String name,
			String value) throws XMLStreamException
			{
		createNode(eventWriter, name, value, 1);
		
			}
	
}
