package net.minecraft.src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.minecraft.client.Minecraft;

public class mod_ChatLogToFile extends BaseMod
{
	private File file;
	private boolean canWrite;
	
	public mod_ChatLogToFile()
	{
		file = null;
		canWrite = true;
		
	}
	
	@Override
	public String getVersion()
	{
		return "r0";
	}
	
	@Override
	public void load()
	{
		try
		{
			file = new File(Minecraft.getMinecraftDir(), "chatlog.txt");
			if (!file.exists())
				file.createNewFile();
			
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String dateNow = formatter.format(currentDate.getTime());
			
			PrintWriter writer = new PrintWriter(new FileWriter(file, true));
			writer.println();
			writer.println();
			writer.println("========================");
			writer.println("Starting new session on: "
					+ dateNow + " ("
					+ System.currentTimeMillis() + ")");
			writer.println("========================");
			writer.close();
			
			canWrite = true;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public void receiveChatPacket(String contents)
	{
		PrintWriter writer;
		try
		{
			writer = new PrintWriter(new FileWriter(file, true));
			writer.println(contents);
			writer.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
		}
		
	}
	
}
