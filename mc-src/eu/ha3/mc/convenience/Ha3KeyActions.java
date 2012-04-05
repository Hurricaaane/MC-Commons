package eu.ha3.mc.convenience;


public interface Ha3KeyActions
{
	void doBefore();
	
	void doDuring(int curTime);
	
	void doAfter(int curTime);
	
}
