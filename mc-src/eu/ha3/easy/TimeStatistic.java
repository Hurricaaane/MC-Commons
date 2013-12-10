package eu.ha3.easy;

import java.util.Locale;

/* x-placeholder-wtfplv2 */

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
