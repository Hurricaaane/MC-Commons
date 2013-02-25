package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

public class ATPack
{
	private File container;
	
	private String sysName;
	
	private String locationPrintName;
	private String prettyName;
	private String author;
	private String url;
	private String description;
	private String madeForVersion;
	
	private boolean isActivated;
	
	public ATPack(File container)
	{
		this.container = container;
		this.sysName = container.getName();
		this.locationPrintName = container.getName() + (this.container.isDirectory() ? "/" : "");
		
		this.prettyName = this.sysName;
		this.author = "";
		this.url = "";
		this.description = "";
		this.madeForVersion = "";
	}
	
	public String getSysName()
	{
		return this.sysName;
	}
	
	public String getLocationPrintName()
	{
		return this.locationPrintName;
	}
	
	public File getDirectory()
	{
		return this.container;
	}
	
	public void fetchInfo()
	{
		InputStream is = null;
		ZipFile zip = null;
		
		try
		{
			if (this.container.isDirectory())
			{
				File metadata = new File(this.container, "info.cfg");
				if (!metadata.exists())
					return;
				is = new FileInputStream(metadata);
			}
			else
			{
				zip = new ZipFile(this.container);
				ZipEntry entry = zip.getEntry("info.cfg");
				if (entry == null)
					return;
				
				is = zip.getInputStream(entry);
			}
			if (is == null)
				return;
			
			ATConfigFromStream info = new ATConfigFromStream();
			info.setProperty("pack.prettyname", this.sysName);
			info.setProperty("pack.author", "");
			info.setProperty("pack.url", "");
			info.setProperty("pack.description", "");
			info.setProperty("pack.madeforversion", "???");
			if (info.loadFromStream(is))
			{
				this.prettyName = info.getString("pack.prettyname");
				this.author = info.getString("pack.author");
				this.url = info.getString("pack.url");
				this.description = info.getString("pack.description");
				this.madeForVersion = info.getString("pack.madeforversion");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (zip != null)
			{
				try
				{
					zip.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public boolean isActive()
	{
		return this.isActivated;
	}
	
	public void setActive(boolean active)
	{
		if (this.isActivated == active)
			return;
		
		this.isActivated = active;
	}
	
	public String getPrettyName()
	{
		return this.prettyName;
	}
	
	public String getAuthor()
	{
		return this.author;
	}
	
	public String getUrl()
	{
		return this.url;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public String getMadeForVersion()
	{
		return this.madeForVersion;
	}
}
