package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.minecraft.client.Minecraft;

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

public class mod_FootstepsShortCircuit extends BaseMod
{
	private final int SPAN = 300;
	
	private boolean activated;
	private int roller;
	
	public mod_FootstepsShortCircuit()
	{
		this.roller = 0;
		
	}
	
	@Override
	public String getVersion()
	{
		return "r0";
	}
	
	@Override
	public void load()
	{
		this.activated = true;
		
		try
		{
			File file = new File(Minecraft.getMinecraftDir(), "footstepsshortcircuit.txt");
			if (!file.exists())
			{
				boolean yes = file.createNewFile();
				if (yes)
				{
					PrintWriter fw = new PrintWriter(new FileWriter(file));
					fw.print("1");
					fw.close();
					
				}
				
			}
			else
			{
				BufferedReader fr = new BufferedReader(new FileReader(file));
				String line = fr.readLine();
				if (line.equals("0"))
				{
					this.activated = false;
					System.out.println("FootstepsShortCurcuit is off");
					
				}
				else
				{
					this.activated = true;
					
				}
				fr.close();
				
			}
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if (this.activated)
		{
			ModLoader.setInGameHook(this, true, false);
			
		}
		
	}
	
	@Override
	public boolean onTickInGame(float fspan, Minecraft game)
	{
		if (this.roller > 0)
		{
			this.roller = this.roller - 1;
			return true;
			
		}
		this.roller = this.SPAN;
		
		if (ModLoader.getMinecraftInstance().thePlayer != null)
		{
			try
			{
				//nextStepDistance
				ModLoader.setPrivateValue(
					Entity.class, ModLoader.getMinecraftInstance().thePlayer, 35, Integer.MAX_VALUE);
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
				
			}
			
		}
		
		return true;
		
	}
	
}
