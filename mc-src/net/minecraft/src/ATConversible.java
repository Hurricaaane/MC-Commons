package net.minecraft.src;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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

public class ATConversible
{
	private File file;
	private URL url;
	private boolean isFile;
	
	public ATConversible(File file)
	{
		this.file = file;
		this.isFile = true;
	}
	
	public ATConversible(URL url)
	{
		this.url = url;
	}
	
	public File getFile()
	{
		return this.file;
	}
	
	public URL getURL()
	{
		return this.url;
	}
	
	public boolean isFile()
	{
		return this.isFile;
	}
	
	public URL asURL()
	{
		try
		{
			return this.isFile ? this.file.toURI().toURL() : this.url;
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Malformed URL thrown from Audiotori!");
		}
	}
	
}
