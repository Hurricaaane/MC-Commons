package eu.ha3.easy;

import java.util.Locale;

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

public class TimeStatistic
{
	private long startTime;
	private Locale locale;
	
	public TimeStatistic(Locale locale)
	{
		this.locale = locale;
		this.startTime = System.currentTimeMillis();
	}
	
	public TimeStatistic()
	{
		this(null);
	}
	
	public long getMilliseconds()
	{
		return System.currentTimeMillis() - this.startTime;
	}
	
	public String getSecondsAsString(int precision)
	{
		if (this.locale == null)
			return String.format("%." + precision + "f", getMilliseconds() / 1000f);
		
		return String.format(this.locale, "%." + precision + "f", getMilliseconds() / 1000f);
	}
	
}
