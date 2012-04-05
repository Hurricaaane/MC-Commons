package eu.ha3.mc.mod;

public interface Ha3Mod
{
	void setCore(Ha3ModCore coreIn);
	
	void setManager(Ha3ModManager managerIn);
	
	void setReference(Object referenceIn);
	
	Ha3ModCore core();
	
	Ha3ModManager manager();
	
	Object reference();
	
}
