package eu.ha3.matmos.engine;

import java.util.ArrayList;
import java.util.Collections;
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

public class MAtmosList extends MAtmosDescriptible
{
	ArrayList<Integer> list;
	
	MAtmosList()
	{
		list = new ArrayList<Integer>();
		
	}
	
	public ArrayList<Integer> getList()
	{
		return list;
		
	}
	
	public boolean contains(int in)
	{
		return list.contains(in);
		
	}
	public void add(int in)
	{
		if (list.contains(in))
			return;
		
		list.add(in);
		Collections.sort( list );
	}
	public void remove(int in)
	{
		list.remove(in);
	}
	public void clear()
	{
		list.clear();
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);
		
		for (Iterator<Integer> iter = list.iterator(); iter.hasNext();)
		{
			createNode(eventWriter, "constant", iter.next().toString());
			
		}
		
		return null;
		
	}
	
}
