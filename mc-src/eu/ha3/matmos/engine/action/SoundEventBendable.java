package eu.ha3.matmos.engine.action;

public interface SoundEventBendable extends SoundEvent
{
	public void addBender(SoundEventBender bender);
	
	public void removeBender(SoundEventBender bender);
	
}
