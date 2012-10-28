package net.minecraft.src;

import java.io.File;

import eu.ha3.util.property.simple.ConfigProperty;

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
	private File directory;
	
	private String sysName;
	private String prettyName;
	
	private boolean isActivated;
	
	public ATPack(File directory)
	{
		this.directory = directory;
		
		this.sysName = directory.getName();
		this.prettyName = this.sysName;
	}
	
	public String getSysName()
	{
		return this.sysName;
	}
	
	public String getPrettyName()
	{
		return this.prettyName;
	}
	
	public File getDirectory()
	{
		return this.directory;
	}
	
	public void fetchInfo()
	{
		File metadata = new File(this.directory, "info.cfg");
		if (!metadata.exists())
			return;
		
		ConfigProperty info = new ConfigProperty();
		info.setSource(metadata.getAbsolutePath());
		if (info.load())
		{
			this.prettyName = info.getString("pack.prettyname");
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
}
