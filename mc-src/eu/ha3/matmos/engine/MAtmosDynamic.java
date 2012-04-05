package eu.ha3.matmos.engine;

import java.util.ArrayList;
import java.util.Iterator;

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

public class MAtmosDynamic extends MAtmosSwitchable
{
	public ArrayList<String> sheets;
	public ArrayList<Integer> keys;
	
	public int value;
	
	MAtmosDynamic(MAtmosKnowledge knowledgeIn)
	{
		super(knowledgeIn);
		
		sheets = new ArrayList<String>();
		keys = new ArrayList<Integer>();
		
		value = 0;
		
	}
	
	public void addCouple(String sheet, int key)
	{
		sheets.add(sheet);
		keys.add(key);
		flagNeedsTesting();
		
	}
	public void removeCouple(int id)
	{
		sheets.remove(id);
		keys.remove(id);
		flagNeedsTesting();
		
	}

	public void setSheet(int id, String sheet)
	{
		sheets.set(id, sheet);
		flagNeedsTesting();
		
	}
	public void setKey(int id, int key)
	{
		keys.set(id, key);
		flagNeedsTesting();
		
	}
	
	
	public ArrayList<String> getSheets()
	{
		return sheets;
		
	}
	public ArrayList<Integer> getKeys()
	{
		return keys;
		
	}
	public String getSheet(int id)
	{
		return sheets.get(id);
		
	}
	public int getKey(int id)
	{
		return keys.get(id);
		
	}

	public boolean isActive()
	{
		return false;
		
	}
	public void evaluate()
	{
		value = 0;
		
		if (!isValid())
			return;

		Iterator<String> iterSheets = sheets.iterator();
		Iterator<Integer> iterKeys = keys.iterator();
		
		while (iterSheets.hasNext())
		{
			String sheet = iterSheets.next();
			Integer key = iterKeys.next();
			
			value = value + knowledge.data.sheets.get(sheet).get(key);
			
		}
		
	}

	protected boolean testIfValid()
	{
		Iterator<String> iterSheets = sheets.iterator();
		Iterator<Integer> iterKeys = keys.iterator();
		
		while (iterSheets.hasNext())
		{
			String sheet = iterSheets.next();
			Integer key = iterKeys.next();
			
			if (knowledge.data.sheets.containsKey(sheet))
			{
				if ( !((key >= 0) && (key < knowledge.data.sheets.get(sheet).size())) )
				{
					return false;
					
				}
				//else continue;
				
			}
			else
			{
				return false;
				
			}
			
		}
		
		return true;
	}
    
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);

		XMLEventFactory eventFactory = XMLEventFactory.newInstance();

		XMLEvent tab = eventFactory.createDTD("\t");
		XMLEvent ret = eventFactory.createDTD("\n");
		
		for (int i = 0; i < sheets.size(); i++)
		{
			eventWriter.add(tab);
			eventWriter.add(eventFactory.createStartElement("", "", "entry"));
			eventWriter.add(eventFactory.createAttribute("sheet", sheets.get(i)));
			eventWriter.add(eventFactory.createCharacters(keys.get(i) + ""));
			eventWriter.add(eventFactory.createEndElement("", "", "entry"));
			eventWriter.add(ret);
			
		}
		
		return null;
	}

}
