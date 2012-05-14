package eu.ha3.matmos.engine.action;

public interface SoundEventEngine
{
	public void setSoundEngine(SoundEngine soundEngine);

	public void addReaction(String machine, MachineAction action, String sound);
	
	public void addSound(String sound, String path, float volMin, float volMax,
			float pitchMin, float pitchMax);
	
	public void addBender(String controller, String sound, String benderType,
			String benderParams);
	
}
