package eu.ha3.easy;

/*
--filenotes-placeholder
*/

public class StopWatchStatistic extends TimeStatistic
{
	private long stopTime;
	
	@Override
	public long getMilliseconds()
	{
		return this.stopTime - this.startTime;
	}
	
	public void reset()
	{
		this.startTime = System.currentTimeMillis();
	}
	
	public void stop()
	{
		this.stopTime = System.currentTimeMillis();
	}
}
