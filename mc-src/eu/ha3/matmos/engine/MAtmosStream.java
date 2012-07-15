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

public class MAtmosStream extends MAtmosDescriptible
{
	MAtmosMachine machine;
	
	int token;
	
	public String path;
	public float volume;
	public float pitch;
	public float fadeInTime;
	public float fadeOutTime;
	public float delayBeforeFadeIn;
	public float delayBeforeFadeOut;
	public boolean isLooping;
	public boolean isUsingPause;
	
	boolean isTurnedOn;
	boolean isPlaying;
	
	long startTime;
	long stopTime;
	
	boolean firstCall;
	
	MAtmosStream(MAtmosMachine machineIn)
	{
		path = "";
		
		machine = machineIn;
		
		volume = 1F;
		pitch = 1F;
		fadeInTime = 0F;
		fadeOutTime = 0F;
		delayBeforeFadeIn = 0F;
		delayBeforeFadeOut = 0F;
		isLooping = true;
		isUsingPause = false;
		
		isTurnedOn = false;
		isPlaying = false;
		
		firstCall = true;
		
		token = -1;
		
		startTime = 0;
		stopTime = 0;
		
	}
	void setMachine(MAtmosMachine machineIn)
	{
		machine = machineIn;
		
	}
	
	public void signalPlayable()
	{
		if (isTurnedOn)
			return;
		
		startTime = machine.knowledge.getTimeMillis() + (long) (delayBeforeFadeIn * 1000);
		isTurnedOn = true;
		
	}
	public void signalStoppable()
	{
		if (!isTurnedOn)
			return;
		
		stopTime = machine.knowledge.getTimeMillis() + (long) (delayBeforeFadeOut * 1000);
		isTurnedOn = false;
		
	}
	
	public void clearToken()
	{
		if (firstCall) return;
		
		machine.knowledge.soundManager.eraseStreamingToken(token);
		isPlaying = false;
		
		token = -1;
		firstCall = true;
		
	}
	
	public void routine()
	{
		if (!isLooping && isUsingPause)
			return; // FIXME: A non-looping sound cannot use the pause scheme.
		
		if (isTurnedOn && !isPlaying)
		{
			if (machine.knowledge.getTimeMillis() > startTime)
			{
				isPlaying = true;
				
				if (firstCall)
				{
					token = machine.knowledge.soundManager.getNewStreamingToken();
					
					// FIXME: Blatent crash prevention: Find new implementation
					if (machine.knowledge.soundManager.setupStreamingToken(token, path, volume, pitch))
					{
						firstCall = false;
						machine.knowledge.soundManager.startStreaming(token, fadeInTime, isLooping ? 0 : 1);
						
					}
					
				}
				else
				{
					machine.knowledge.soundManager.startStreaming(token, fadeInTime, isLooping ? 0 : 1);
					
				}
				
			}
			
		}
		else if (!isTurnedOn && isPlaying)
		{
			if (machine.knowledge.getTimeMillis() > stopTime)
			{
				isPlaying = false;
				
				if (!isUsingPause)
					machine.knowledge.soundManager.stopStreaming(token, fadeOutTime);
				
				else
					machine.knowledge.soundManager.pauseStreaming(token, fadeOutTime);
				
			}
			
		}
		
	}
	
	
	@Override
	public String serialize(XMLEventWriter eventWriter)
	throws XMLStreamException {
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");
		
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createStartElement("", "", "stream"));
		eventWriter.add(ret);
		createNode(eventWriter, "path", path, 2);
		createNode(eventWriter, "volume", "" + volume, 2);
		createNode(eventWriter, "pitch", "" + pitch, 2);
		createNode(eventWriter, "fadeintime", "" + fadeInTime, 2);
		createNode(eventWriter, "fadeouttime", "" + fadeOutTime, 2);
		createNode(eventWriter, "delaybeforefadein", "" + delayBeforeFadeIn, 2);
		createNode(eventWriter, "delaybeforefadeout", "" + delayBeforeFadeOut, 2);
		createNode(eventWriter, "islooping", isLooping ? "1" : "0", 2);
		createNode(eventWriter, "isusingpause", isUsingPause ? "1" : "0", 2);
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createEndElement("", "", "stream"));
		eventWriter.add(ret);
		
		return "";
	}
	
	
	
}
