package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.minecraft.client.Minecraft;

public class mod_FootstepsShortCircuit extends BaseMod
{
	private final int SPAN = 300;
	
	private boolean activated;
	private int roller;
	
	public mod_FootstepsShortCircuit()
	{
		roller = 0;
		
	}
	
	@Override
	public String getVersion()
	{
		return "r0";
	}
	
	@Override
	public void load()
	{
		activated = true;
		
		try
		{
			File file = new File(Minecraft.getMinecraftDir(),
					"footstepsshortcircuit.txt");
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
					activated = false;
					System.out.println("FootstepsShortCurcuit is off");
					
				}
				else
				{
					activated = true;
					
				}
				
			}
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if (activated)
		{
			ModLoader.setInGameHook(this, true, false);
			
		}
		
	}
	
	@Override
	public boolean onTickInGame(float fspan, Minecraft game)
	{
		if (roller > 0)
		{
			roller = roller - 1;
			return true;
			
		}
		roller = SPAN;
		
		if (ModLoader.getMinecraftInstance().thePlayer != null)
		{
			try
			{
				//nextStepDistance
				ModLoader.setPrivateValue(Entity.class, ModLoader
						.getMinecraftInstance().thePlayer, 35,
						Integer.MAX_VALUE);
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
				
			}
			
		}
		
		return true;
		
	}
	
}
