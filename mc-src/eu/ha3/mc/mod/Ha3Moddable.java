package eu.ha3.mc.mod;

public abstract class Ha3Moddable implements Ha3Mod
{
	private Ha3ModCore core;
	private Ha3ModManager manager;
	private Object reference;
	
	@Override
	final public void setCore(Ha3ModCore coreIn)
	{
		core = coreIn;
		core().setMod(this);
		
	}
	
	@Override
	final public void setManager(Ha3ModManager managerIn)
	{
		manager = managerIn;
		manager.setMod(this);
		
	}
	
	@Override
	final public void setReference(Object referenceIn)
	{
		reference = referenceIn;
		
	}
	
	@Override
	final public Ha3ModCore core()
	{
		return core;
		
	}
	
	@Override
	final public Ha3ModManager manager()
	{
		return manager;
		
	}
	
	@Override
	final public Object reference()
	{
		return reference;
		
	}
	
}
