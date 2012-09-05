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
		this.path = "";
		
		this.machine = machineIn;
		
		this.volume = 1F;
		this.pitch = 1F;
		this.fadeInTime = 0F;
		this.fadeOutTime = 0F;
		this.delayBeforeFadeIn = 0F;
		this.delayBeforeFadeOut = 0F;
		this.isLooping = true;
		this.isUsingPause = false;
		
		this.isTurnedOn = false;
		this.isPlaying = false;
		
		this.firstCall = true;
		
		this.token = -1;
		
		this.startTime = 0;
		this.stopTime = 0;
		
	}
	
	void setMachine(MAtmosMachine machineIn)
	{
		this.machine = machineIn;
		
	}
	
	public void signalPlayable()
	{
		if (this.isTurnedOn)
			return;
		
		this.startTime = this.machine.knowledge.getTimeMillis() + (long) (this.delayBeforeFadeIn * 1000);
		this.isTurnedOn = true;
		
	}
	
	public void signalStoppable()
	{
		if (!this.isTurnedOn)
			return;
		
		this.stopTime = this.machine.knowledge.getTimeMillis() + (long) (this.delayBeforeFadeOut * 1000);
		this.isTurnedOn = false;
		
	}
	
	public void clearToken()
	{
		if (this.firstCall)
			return;
		
		this.machine.knowledge.soundManager.eraseStreamingToken(this.token);
		this.isPlaying = false;
		
		this.token = -1;
		this.firstCall = true;
		
	}
	
	public void routine()
	{
		if (!this.isLooping && this.isUsingPause)
			return; // FIXME: A non-looping sound cannot use the pause scheme.
			
		if (this.isTurnedOn && !this.isPlaying)
		{
			if (this.machine.knowledge.getTimeMillis() > this.startTime)
			{
				this.isPlaying = true;
				
				if (this.firstCall)
				{
					this.token = this.machine.knowledge.soundManager.getNewStreamingToken();
					
					// FIXME: Blatent crash prevention: Find new implementation
					if (this.machine.knowledge.soundManager.setupStreamingToken(
						this.token, this.path, this.volume, this.pitch))
					{
						this.firstCall = false;
						this.machine.knowledge.soundManager.startStreaming(this.token, this.fadeInTime, this.isLooping
							? 0 : 1);
						
					}
					
				}
				else
				{
					this.machine.knowledge.soundManager.startStreaming(this.token, this.fadeInTime, this.isLooping
						? 0 : 1);
					
				}
				
			}
			
		}
		else if (!this.isTurnedOn && this.isPlaying)
		{
			if (this.machine.knowledge.getTimeMillis() > this.stopTime)
			{
				this.isPlaying = false;
				
				if (!this.isUsingPause)
				{
					this.machine.knowledge.soundManager.stopStreaming(this.token, this.fadeOutTime);
				}
				else
				{
					this.machine.knowledge.soundManager.pauseStreaming(this.token, this.fadeOutTime);
				}
				
			}
			
		}
		
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");
		
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createStartElement("", "", "stream"));
		eventWriter.add(ret);
		createNode(eventWriter, "path", this.path, 2);
		createNode(eventWriter, "volume", "" + this.volume, 2);
		createNode(eventWriter, "pitch", "" + this.pitch, 2);
		createNode(eventWriter, "fadeintime", "" + this.fadeInTime, 2);
		createNode(eventWriter, "fadeouttime", "" + this.fadeOutTime, 2);
		createNode(eventWriter, "delaybeforefadein", "" + this.delayBeforeFadeIn, 2);
		createNode(eventWriter, "delaybeforefadeout", "" + this.delayBeforeFadeOut, 2);
		createNode(eventWriter, "islooping", this.isLooping ? "1" : "0", 2);
		createNode(eventWriter, "isusingpause", this.isUsingPause ? "1" : "0", 2);
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createEndElement("", "", "stream"));
		eventWriter.add(ret);
		
		return "";
	}
	
}
