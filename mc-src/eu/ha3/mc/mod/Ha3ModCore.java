package eu.ha3.mc.mod;

import net.minecraft.src.KeyBinding;

public interface Ha3ModCore
{
	public void setMod(Ha3Mod modIn);
	
	public void doFrame(float fspan);
	
	public void doKeyBindingEvent(KeyBinding event);
	
	public void load();
	
	//public void unload();
	
	public void doManagerReady();
	
}
