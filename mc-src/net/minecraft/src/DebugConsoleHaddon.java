package net.minecraft.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsGuiFrameEvents;
import eu.ha3.mc.haddon.SupportsGuiTickEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;

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

public class DebugConsoleHaddon extends HaddonImpl
	implements SupportsGuiFrameEvents, SupportsFrameEvents, SupportsTickEvents, SupportsGuiTickEvents
{
	private boolean enabled;
	private EdgeTrigger bindTrigger;
	
	private PrintStream soutPrinter;
	private BufferedReader soutReader;
	private InputStream soutInStream;
	private OutputStream soutOutStream;
	
	private PrintStream serrPrinter;
	private BufferedReader serrReader;
	private InputStream serrInStream;
	private OutputStream serrOutStream;
	
	private List<String> lastMessages;
	
	@Override
	public void onLoad()
	{
		this.bindTrigger = new EdgeTrigger(new EdgeModel() {
			@Override
			public void onTrueEdge()
			{
				toggleState();
				
			}
			
			@Override
			public void onFalseEdge()
			{
			}
		});
		
		try
		{
			this.soutInStream = new PipedInputStream();
			this.soutOutStream = new PipedOutputStream((PipedInputStream) this.soutInStream);
			this.soutPrinter = new PrintStream(this.soutOutStream);
			this.soutReader = new BufferedReader(new InputStreamReader(this.soutInStream));
			
			this.serrInStream = new PipedInputStream();
			this.serrOutStream = new PipedOutputStream((PipedInputStream) this.serrInStream);
			this.serrPrinter = new PrintStream(this.serrOutStream);
			this.serrReader = new BufferedReader(new InputStreamReader(this.serrInStream));
			
			this.sb = new StringBuilder();
			this.lastMessages = new ArrayList<String>();
			
		}
		catch (Exception e)
		{
		}
		
		manager().hookTickEvents(true);
		
	}
	
	public void toggleState()
	{
		if (!this.enabled)
		{
			enable();
		}
		else
		{
			disable();
		}
		
	}
	
	private PrintStream orgStream;
	private PrintStream orrStream;
	
	public void enable()
	{
		if (this.enabled)
			return;
		
		manager().hookGuiFrameEvents(true);
		manager().hookFrameEvents(true);
		
		try
		{
			this.orgStream = System.out;
			this.orrStream = System.err;
			System.setOut(this.soutPrinter);
			System.setErr(this.serrPrinter);
		}
		catch (Exception e)
		{
			System.setOut(this.orgStream);
			System.setErr(this.orrStream);
		}
		
		this.enabled = true;
		
	}
	
	public void disable()
	{
		if (!this.enabled)
			return;
		
		manager().hookGuiFrameEvents(true);
		manager().hookFrameEvents(true);
		
		try
		{
			System.setOut(this.orgStream);
			System.setErr(this.orrStream);
		}
		catch (Exception e)
		{
		}
		
		this.enabled = false;
	}
	
	@Override
	public void onTick()
	{
		this.bindTrigger.signalState(util().areKeysDown(29, 42, 46));
		if (this.enabled)
		{
			try
			{
				while (this.soutReader.ready())
				{
					char read = (char) this.soutReader.read();
					if (read == '\n')
					{
						this.lastMessages.add("OUT: " + this.sb.toString());
						this.sb = new StringBuilder();
					}
					else
					{
						this.sb.append(read);
						
					}
					
				}
				
				while (this.serrReader.ready())
				{
					char read = (char) this.serrReader.read();
					if (read == '\n')
					{
						this.lastMessages.add("ERR: " + this.sb.toString());
						this.sb = new StringBuilder();
					}
					else
					{
						this.sb.append(read);
						
					}
					
				}
				
				while (this.lastMessages.size() > 5)
				{
					this.lastMessages.remove(0);
					
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	private StringBuilder sb;
	
	@Override
	public void onFrame(float semi)
	{
		int height = manager().getMinecraft().fontRenderer.FONT_HEIGHT;
		
		util().prepareDrawString();
		
		int i = 0;
		for (String message : this.lastMessages)
		{
			util().drawString(message, 0, 0, 2, 2 + (height + 2) * i, '7', 255, 255, 0, 128, true);
			
			i++;
			
		}
	}
	
	@Override
	public void onGuiFrame(GuiScreen gui, float semi)
	{
	}
	
	@Override
	public void onGuiTick(GuiScreen gui)
	{
		onTick();
		
	}
	
}
