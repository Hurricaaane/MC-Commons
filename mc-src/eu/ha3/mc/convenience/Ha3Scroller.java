package eu.ha3.mc.convenience;

import eu.ha3.mc.haddon.Manager;

public abstract class Ha3Scroller
{
	private Manager manager;
	
	private boolean isRunning;
	
	private float pitchBase;
	private float pitchGlobal;
	
	protected abstract void doDraw(float fspan);
	
	protected abstract void doRoutineBefore();
	
	protected abstract void doRoutineAfter();
	
	protected abstract void doStart();
	
	protected abstract void doStop();
	
	public Ha3Scroller(Manager managerIn)
	{
		manager = managerIn;
		pitchBase = 0;
		pitchGlobal = 0;
		
	}
	
	public Manager manager()
	{
		return manager;
		
	}
	
	public float getInitialPitch()
	{
		return pitchBase;
		
	}
	
	public float getPitch()
	{
		return pitchGlobal;
		
	}
	
	public void draw(float fspan)
	{
		if (!isRunning)
			return;
		
		doDraw(fspan);
		
	}
	
	public void routine()
	{
		if (!isRunning)
			return;
		
		doRoutineBefore();
		
		pitchGlobal = manager.getMinecraft().thePlayer.rotationPitch;
		
		doRoutineAfter();
		
	}
	
	public void start()
	{
		if (isRunning)
			return;
		
		isRunning = true;
		
		pitchBase = manager.getMinecraft().thePlayer.rotationPitch;
		
		doStart();
		
	}
	
	public void stop()
	{
		if (!isRunning)
			return;
		
		isRunning = false;
		
		doStop();
		
	}
	
	public boolean isRunning()
	{
		return isRunning;
		
	}
	
}
