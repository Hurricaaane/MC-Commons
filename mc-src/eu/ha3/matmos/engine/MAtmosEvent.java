package eu.ha3.matmos.engine;

import java.util.ArrayList;
import java.util.Iterator;

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

public class MAtmosEvent extends MAtmosDescriptible
{
	MAtmosKnowledge knowledge;
	
	public ArrayList<String> paths;
	 
	public float volMin;
	public float volMax;
	public float pitchMin;
	public float pitchMax;
	
	public int metaSound;
	
	MAtmosEvent(MAtmosKnowledge knowledgeIn)
	{
		paths = new ArrayList<String>();
		knowledge = knowledgeIn;
		
		volMin = 1F;
		volMax = 1F;
		pitchMin = 1F;
		pitchMax = 1F;
		
		metaSound = 0;
		
	}
	
	void setKnowledge(MAtmosKnowledge knowledgeIn)
	{
		knowledge = knowledgeIn;
	
	}
	
	public void cacheSounds()
	{
		for (Iterator<String> iter = paths.iterator(); iter.hasNext();)
		{
			knowledge.soundManager.cacheSound(iter.next());
			
		}
		
	}
	
	public void playSound(float volMod, float pitchMod)
	{
		if (paths.isEmpty()) return;
		
		//float volume = volMin + knowledge.random.nextFloat() * (volMax - volMin);
		//float pitch = pitchMin + knowledge.random.nextFloat() * (pitchMax - pitchMin);
		
		float volume = volMax - volMin;
		float pitch = pitchMax - pitchMin;
		volume = volMin + (volume > 0 ? knowledge.random.nextFloat() * volume : 0);
		pitch = pitchMin + (pitch > 0 ? knowledge.random.nextFloat() * pitch : 0);
		
		String path = paths.get( knowledge.random.nextInt(paths.size()) );
		
		volume = volume * volMod;
		pitch = pitch * pitchMod;
		
		knowledge.soundManager.playSound(path, volume, pitch, metaSound);
		
	}

	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);
		
		for (Iterator<String> iter = paths.iterator(); iter.hasNext();)
		{
			createNode(eventWriter, "path", iter.next());
			
		}
		
		createNode(eventWriter, "volmin", volMin + "");
		createNode(eventWriter, "volmax", volMax + "");
		createNode(eventWriter, "pitchmin", pitchMin + "");
		createNode(eventWriter, "pitchmax", pitchMax + "");
		createNode(eventWriter, "metasound", metaSound + "");
		
		return "";
	}
	
}
