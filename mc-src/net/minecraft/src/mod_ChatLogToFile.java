package net.minecraft.src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

public class mod_ChatLogToFile extends BaseMod
{
	private File file;
	
	public mod_ChatLogToFile()
	{
		this.file = null;
		
	}
	
	@Override
	public String getVersion()
	{
		return "r1 for 1.4.2";
		
	}
	
	@Override
	public void load()
	{
		try
		{
			this.file = new File(Minecraft.getMinecraftDir(), "chatlog.txt");
			if (!this.file.exists())
			{
				this.file.createNewFile();
			}
			
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateNow = formatter.format(currentDate.getTime());
			
			PrintWriter writer = new PrintWriter(new FileWriter(this.file, true));
			writer.println();
			writer.println();
			writer.println("========================");
			writer.println("Starting new session on: " + dateNow + " (" + System.currentTimeMillis() + ")");
			writer.println("========================");
			writer.close();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public void clientChat(String contents)
	{
		PrintWriter writer;
		try
		{
			writer = new PrintWriter(new FileWriter(this.file, true));
			writer.println(contents);
			writer.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
		}
		
	}
	
}
