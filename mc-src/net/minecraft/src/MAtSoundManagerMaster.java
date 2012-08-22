package net.minecraft.src;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import paulscode.sound.SoundSystem;
import eu.ha3.matmos.engine.MAtmosSoundManager;
import eu.ha3.mc.convenience.Ha3Personalizable;

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

public class MAtSoundManagerMaster implements MAtmosSoundManager, Ha3Personalizable, MAtCustomVolume
{
	// XXX Implement me: Does not do anything and sndcomms is down
	
	private MAtMod mod;
	
	private float volume;
	final private float defSoundVolume = 1F;
	
	private int nbTokens;
	//private Random random;
	private Map<String, String> soundequivalences;
	/*private ArrayList<String> tokenPaths;
	private ArrayList<Boolean> tokenSetFirst;
	private ArrayList<Float> tokenVolume;
	private ArrayList<Float> tokenVolModulator;
	private ArrayList<Float> tokenPitch;
	private ArrayList<URL> tokenURL;
	private ArrayList<Boolean> tokenRegisteredInEngine;
	private Map<String, Float> paulsCodeBug_markForFadeIn;*/
	
	private float settingsVolume;
	
	private Properties config;
	
	public MAtSoundManagerMaster(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		
		this.volume = this.defSoundVolume;
		
		this.nbTokens = 0;
		//this.random = new Random(System.currentTimeMillis());
		this.soundequivalences = new HashMap<String, String>();
		/*this.tokenPaths = new ArrayList<String>();
		this.tokenSetFirst = new ArrayList<Boolean>();
		this.tokenURL = new ArrayList<URL>();
		this.tokenVolume = new ArrayList<Float>();
		this.tokenVolModulator = new ArrayList<Float>();
		this.tokenPitch = new ArrayList<Float>();
		this.tokenRegisteredInEngine = new ArrayList<Boolean>();
		//this.paulsCodeBug_markForFadeIn = new HashMap<String, Float>();*/
		
		this.settingsVolume = 0F;
		
	}
	
	public SoundSystem sndSystem()
	{
		return this.mod.sound().getSoundSystem();
		
	}
	
	@Override
	public float getVolume()
	{
		return this.volume;
		
	}
	
	@Override
	public void routine()
	{
		updateSettingsVolume();
		
		/*if (this.paulsCodeBug_markForFadeIn.size() != 0)
		{
			for (Iterator<Entry<String, Float>> iter = this.paulsCodeBug_markForFadeIn.entrySet().iterator(); iter
				.hasNext();)
			{
				Entry<String, Float> entry = iter.next();
				sndSystem().setVolume(entry.getKey(), entry.getValue() * this.settingsVolume);
				
			}
			this.paulsCodeBug_markForFadeIn.clear();
			
		}*/
	}
	
	@Override
	public void cacheSound(String path)
	{
		getSound(path);
		
	}
	
	@SuppressWarnings("static-access")
	String getSound(String soundPath)
	{
		if (this.soundequivalences.containsKey(soundPath))
			return this.soundequivalences.get(soundPath);
		
		File soundFile = new File(this.mod.manager().getMinecraft().getMinecraftDir() + "/resources", soundPath);
		
		// FIXME DO IT BETTER
		String path = new StringBuilder().append(soundPath).toString();
		int j = path.indexOf("/");
		int t = path.indexOf(".");
		String quant = path.substring(j + 1, t);
		String dotted = quant.replaceAll("/", ".");
		dotted = dotted.replaceAll("0", "");
		dotted = dotted.replaceAll("1", "");
		dotted = dotted.replaceAll("2", "");
		dotted = dotted.replaceAll("3", "");
		dotted = dotted.replaceAll("4", "");
		dotted = dotted.replaceAll("5", "");
		dotted = dotted.replaceAll("6", "");
		dotted = dotted.replaceAll("7", "");
		dotted = dotted.replaceAll("8", "");
		dotted = dotted.replaceAll("9", "");
		
		this.soundequivalences.put(soundPath, dotted);
		
		if (!soundFile.exists())
		{
			MAtMod.LOGGER.warning("File " + soundPath + " is missing " + " (" + dotted + ")");
		}
		
		//System.out.println("File " + soundPath + (soundFile.exists()?" exists":" is missing") + " (" + dotted + ")");
		
		return dotted;
		
	}
	
	@Override
	public void playSound(String path, float volume, float pitch, int meta)
	{
		// The MASTER shall never play sounds.
	}
	
	@Override
	public synchronized int getNewStreamingToken()
	{
		int token = this.nbTokens;
		this.nbTokens = this.nbTokens + 1;
		
		/*this.tokenPaths.add("");
		this.tokenSetFirst.add(false);
		this.tokenURL.add(null);
		this.tokenVolume.add(0F);
		this.tokenVolModulator.add(1F);
		this.tokenPitch.add(0F);
		this.tokenRegisteredInEngine.add(false);*/
		
		return token;
	}
	
	/*private void ensureInitialized(int token)
	{
		if (this.tokenRegisteredInEngine.get(token))
			return;
		
		this.tokenRegisteredInEngine.set(token, true);
		
		try
		{
			String sourceName = "MATMOS_SRM_" + token;
			String path = this.tokenPaths.get(token);
			float volume = this.tokenVolume.get(token);
			float pitch = this.tokenPitch.get(token);
			
			MAtMod.LOGGER.info("Initializing source: " + sourceName);
			
			cacheSound(path);
			String poolName = getSound(path);
			SoundPoolEntry soundpoolentry;
			soundpoolentry =
				((SoundPool) this.mod.util().getPrivateValueLiteral(
					net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "b", 1))
					.getRandomSoundFromSoundPool(poolName);
			
			if (soundpoolentry != null)
			{
				this.tokenURL.set(token, soundpoolentry.soundUrl);
				
				sndSystem().newStreamingSource(true, sourceName, soundpoolentry.soundUrl, path, true, 0, 0, 0, 0, 0);
				sndSystem().setTemporary(sourceName, false);
				sndSystem().setPitch(sourceName, pitch);
				
				sndSystem().setLooping(sourceName, true);
				sndSystem().activate(sourceName); // XXX ??? Is it alright?
				
			}
		}
		catch (PrivateAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	@Override
	public synchronized boolean setupStreamingToken(int token, String path, float volume, float pitch)
	{
		// Master NEVER manages stream playback.
		
		return true;
		
	}
	
	@Override
	public synchronized void startStreaming(int token, float fadeDuration, int timesToPlay)
	{
		// Master NEVER manages stream playback.
	}
	
	@Override
	public synchronized void stopStreaming(int token, float fadeDuration)
	{
		// Master NEVER manages stream playback.
		
	}
	
	@Override
	public synchronized void pauseStreaming(int token, float fadeDuration)
	{
		// Master NEVER manages stream playback.
	}
	
	@Override
	public synchronized void eraseStreamingToken(int token)
	{
		// Master NEVER manages stream playback.
	}
	
	@Override
	public void inputOptions(Properties options)
	{
		if (this.config == null)
		{
			this.config = createDefaultOptions();
		}
		
		try
		{
			{
				String query = "volume.generic.value";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					setVolume(Float.parseFloat(prop));
					this.config.put(query, prop);
				}
				
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public Properties outputOptions()
	{
		if (this.config == null)
			return createDefaultOptions();
		
		this.config.setProperty("volume.generic.value", "" + getVolume());
		
		return this.config;
	}
	
	@Override
	public void defaultOptions()
	{
		inputOptions(createDefaultOptions());
		
	}
	
	private Properties createDefaultOptions()
	{
		Properties options = new Properties();
		options.setProperty("volume.generic.value", "" + this.defSoundVolume);
		
		return options;
		
	}
	
	/*@Override
	public void volumeStreaming(int token, float modulator)
	{
		String sourceName = "MATMOS_SRM_" + token;
		
		this.tokenVolModulator.set(token, modulator);
		
		float playVolume = modulator * this.tokenVolume.get(token) * this.settingsVolume * getVolume();
		sndSystem().setVolume(sourceName, playVolume);
	}*/
	/*
	public void recomputeVolumeStreaming(int token)
	{
		String sourceName = "MATMOS_SRM_" + token;
		
		float playVolume =
			this.tokenVolModulator.get(token) * this.tokenVolume.get(token) * this.settingsVolume * getVolume();
		sndSystem().setVolume(sourceName, playVolume);
	}
	*/
	@Override
	public void setVolume(float modifier)
	{
		this.volume = modifier;
		
	}
	
	private void updateSettingsVolume()
	{
		Minecraft mc = this.mod.manager().getMinecraft();
		
		if (this.settingsVolume != mc.gameSettings.soundVolume)
		{
			this.settingsVolume = mc.gameSettings.soundVolume;
			
		}
		
	}
	
	public float getSettingsVolume()
	{
		return this.settingsVolume;
		
	}
	
}
