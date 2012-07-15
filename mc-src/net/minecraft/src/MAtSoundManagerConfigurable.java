package net.minecraft.src;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import net.minecraft.client.Minecraft;
import paulscode.sound.SoundSystem;
import eu.ha3.matmos.engine.MAtmosSoundManager;
import eu.ha3.mc.convenience.Ha3Personalizable;
import eu.ha3.mc.haddon.PrivateAccessException;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
 */

public class MAtSoundManagerConfigurable implements MAtmosSoundManager,
Ha3Personalizable
{
	// XXX Implement me: Does not do anything and sndcomms is down
	
	private MAtMod mod;
	
	private float soundVolume;
	private float musicVolume;
	private boolean musicVolUsesMinecraft;
	final private float defSoundVolume = 1F;
	final private float defMusicVolume = 1F;
	final private boolean defMusicVolUsesMinecraft = false;
	
	private int nbTokens;
	private Random random;
	private Map<String, String> soundequivalences;
	private ArrayList<String> tokenPaths;
	private ArrayList<Boolean> tokenSetFirst;
	private ArrayList<Float> tokenVolume;
	private ArrayList<URL> tokenURL;
	private ArrayList<String> sourcesAsMusic;
	private Map<String, Float> paulsCodeBug_markForFadeIn;
	
	private float settingsVolume;
	private float settingsMusicVolume;
	private float previousMusicVolume;
	
	private Properties config;
	
	public MAtSoundManagerConfigurable(MAtMod mAtmosHaddon)
	{
		mod = mAtmosHaddon;
		
		soundVolume = defSoundVolume;
		musicVolume = defMusicVolume;
		musicVolUsesMinecraft = defMusicVolUsesMinecraft;
		
		nbTokens = 0;
		random = new Random(System.currentTimeMillis());
		soundequivalences = new HashMap<String, String>();
		tokenPaths = new ArrayList<String>();
		tokenSetFirst = new ArrayList<Boolean>();
		tokenURL = new ArrayList<URL>();
		tokenVolume = new ArrayList<Float>();
		paulsCodeBug_markForFadeIn = new HashMap<String, Float>();
		sourcesAsMusic = new ArrayList<String>();
		
		settingsVolume = 0F;
		settingsMusicVolume = 0F;
		previousMusicVolume = 0F;
		
	}
	
	public SoundSystem sndSystem()
	{
		return mod.sound().getSoundSystem();
		
	}
	
	public void setCustomSoundVolume(float modifier)
	{
		soundVolume = modifier;
		
	}
	
	public float getCustomSoundVolume()
	{
		return soundVolume;
		
	}
	
	public void setCustomMusicVolume(float mod)
	{
		musicVolume = mod;
		
	}
	
	public float getCustomMusicVolume()
	{
		return musicVolume;
		
	}
	
	public void setMusicVolumeIsBasedOffMinecraft(boolean use)
	{
		musicVolUsesMinecraft = use;
		
	}
	
	public boolean getMusicVolumeIsBasedOffMinecraft()
	{
		return musicVolUsesMinecraft;
		
	}
	
	@Override
	public void routine()
	{
		updateSettingsVolume();
		
		if (paulsCodeBug_markForFadeIn.size() != 0)
		{
			for (Iterator<Entry<String, Float>> iter = paulsCodeBug_markForFadeIn
					.entrySet().iterator(); iter.hasNext();)
			{
				Entry<String, Float> entry = iter.next();
				sndSystem().setVolume(entry.getKey(),
						entry.getValue()
						* settingsVolume);
				
			}
			paulsCodeBug_markForFadeIn.clear();
			
		}
	}
	
	private void updateSettingsVolume()
	{
		Minecraft mc = mod.manager().getMinecraft();
		boolean changedSettings = settingsVolume != mc.gameSettings.soundVolume;
		
		if (changedSettings)
			settingsVolume = mc.gameSettings.soundVolume;
		
		if (musicVolUsesMinecraft)
		{
			float currentMusicVolume = mc.gameSettings.musicVolume;
			if (changedSettings || (currentMusicVolume != settingsMusicVolume))
			{
				for (Iterator<String> iter = sourcesAsMusic.iterator(); iter
						.hasNext();)
				{
					sndSystem().setVolume(iter.next(),
							currentMusicVolume
							* settingsVolume);
					
				}
				
			}
			settingsMusicVolume = currentMusicVolume;
			
		}
		else
		{
			if (changedSettings
					|| (getCustomMusicVolume() != previousMusicVolume))
			{
				for (Iterator<String> iter = sourcesAsMusic.iterator(); iter
						.hasNext();)
				{
					sndSystem().setVolume(iter.next(),
							getCustomMusicVolume()
							* settingsVolume);
					
				}
				
			}
			previousMusicVolume = getCustomMusicVolume();
			
		}
		
	}
	
	@Override
	public void cacheSound(String path)
	{
		getSound(path);
		
	}
	
	@SuppressWarnings("static-access")
	String getSound(String soundPath)
	{
		if (soundequivalences.containsKey(soundPath))
			return soundequivalences.get(soundPath);
		
		File soundFile = new File(mod.manager().getMinecraft()
				.getMinecraftDir()
				+ "/resources",
				soundPath);
		
		// FIXME DO IT BETTER
		String path = (new StringBuilder()).append(soundPath).toString();
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
		
		soundequivalences.put(soundPath, dotted);
		
		if (!soundFile.exists())
			MAtMod.LOGGER.warning("File " + soundPath + " is missing " + " ("
					+ dotted + ")");
		
		//System.out.println("File " + soundPath + (soundFile.exists()?" exists":" is missing") + " (" + dotted + ")");
		
		return dotted;
		
	}
	
	@Override
	public void playSound(String path, float volume, float pitch, int meta)
	{
		Minecraft mc = mod.manager().getMinecraft();
		float nx = (float) mc.thePlayer.posX;
		float ny = (float) mc.thePlayer.posY;
		float nz = (float) mc.thePlayer.posZ;
		
		String equivalent = getSound(path);
		
		// FIXME: Doesn't play if musicVolUsesMinecraft is true and the music volume is low...
		float actualVolume = volume == 0 ? (musicVolUsesMinecraft
				? settingsMusicVolume : getCustomMusicVolume()) : volume
				* getCustomSoundVolume();
		
		if (actualVolume == 0) //TODO Check if okay
			return;
		
		if (meta > 0)
		{
			double angle = random.nextFloat() * 2 * Math.PI;
			nx = nx + (float) (Math.cos(angle) * meta);
			ny = ny + random.nextFloat() * meta * 0.2F - meta * 0.01F;
			nz = nz + (float) (Math.sin(angle) * meta);
			
			//mc.sndManager.playSound(equivalent, nx, ny, nz, actualVolume, pitch);
			mod.sound().playSound(equivalent, nx, ny, nz, actualVolume,
					pitch, 0, 0F);
		}
		else
			//if meta == 0
		{
			// NOTE: playSoundFX from Minecraft SoundManager
			//   does NOT work. Must use playSoundFX Proxy
			//   which will play the sound 192 blocks above the player...
			//   ...and that somehow does the trick!
			//
			
			ny = ny + 2048;
			mod.sound().playSound(equivalent, nx, ny, nz, actualVolume,
					pitch, 0, 0F);
			//mc.sndManager.playSoundFX(equivalent, volume * customVolumeMod, pitch);
			
		}
		
	}
	
	@Override
	public synchronized int getNewStreamingToken() // TODO Sync?
	{
		int token = nbTokens;
		nbTokens = nbTokens + 1;
		
		tokenPaths.add("");
		tokenSetFirst.add(false);
		tokenURL.add(null);
		tokenVolume.add(0F);
		
		//System.out.println(token);
		
		return token;
	}
	
	@Override
	public synchronized boolean setupStreamingToken(int token, String path,
			float volume,
			float pitch)
	{
		try
		{
			String sourceName = "MATMOS_SRM_" + token;
			
			tokenPaths.set(token, path);
			
			cacheSound(path);
			//System.out.println(getSound(path));
			String poolName = getSound(path);
			SoundPoolEntry soundpoolentry;
			soundpoolentry = ((SoundPool) (mod.util().getPrivateValue(
					net.minecraft.src.SoundManager.class, mod.manager()
							.getMinecraft().sndManager, "b", 1)))
					.getRandomSoundFromSoundPool(poolName);
			
			if (soundpoolentry != null)
			{
				if (volume == 0)
					sourcesAsMusic.add(sourceName);
				
				tokenURL.set(token, soundpoolentry.soundUrl);
				tokenVolume.set(token, volume);
				
				//System.out.println(sourceName + " " + soundpoolentry.soundUrl
				//+ " " + path);
				
				sndSystem().newStreamingSource(true, sourceName,
						soundpoolentry.soundUrl, path, true, 0, 0, 0, 0, 0);
				sndSystem().setTemporary(sourceName, false);
				sndSystem().setPitch(sourceName, pitch);
				//sndSystem.setVolume(sourceName, volume * settingsVolume);
				
				sndSystem().setLooping(sourceName, true);
				sndSystem().activate(sourceName); // XXX ??? Is it alright?
				
			}
			
			return true;
			
		}
		catch (PrivateAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	@Override
	public synchronized void startStreaming(int token, float fadeDuration,
			int timesToPlay)
	{
		String sourceName = "MATMOS_SRM_" + token;
		
		if (tokenSetFirst.get(token) == false)
		{
			tokenSetFirst.set(token, true);
			
			if (timesToPlay == 0)
				sndSystem().setLooping(sourceName, true);
			
			else
				sndSystem().setLooping(sourceName, false);
			
		}
		
		// pcSystem.rewind(sourceName);
		
		float volume = tokenVolume.get(token);
		float playVolume;
		
		if (volume == 0)
			playVolume = settingsVolume
			* (musicVolUsesMinecraft ? settingsMusicVolume
					: getCustomMusicVolume());
		
		else
			playVolume = volume * settingsVolume * getCustomSoundVolume();
		
		if (fadeDuration == 0)
		{
			sndSystem().setVolume(sourceName, playVolume);
			sndSystem().play(sourceName);
			
		}
		else
		{
			// This is a workaround to counter the bug that makes FadeIn impossible
			// Set 1 millisecond fade out
			//
			// http://www.java-gaming.org/index.php?action=profile;u=11099;sa=showPosts
			
			// Disabled in r7 because of fail
			
			/*String path = tokenPaths.get(token);
			paulsCodeBug_markForFadeIn.put(sourceName, playVolume);
			sndSystem().setVolume(sourceName, 0);
			sndSystem().play(sourceName);
			sndSystem().fadeOutIn(sourceName, tokenURL.get(token), path, 1,
					((long) fadeDuration) * 1000L);*/
			
			// Disabled version:
			
			String path = tokenPaths.get(token);
			sndSystem().setVolume(sourceName, playVolume);
			sndSystem().play(sourceName);
			sndSystem().fadeOutIn(sourceName, tokenURL.get(token), path, 1,
					((long) fadeDuration) * 1000L);
			
		}
		
	}
	
	@Override
	public synchronized void stopStreaming(int token, float fadeDuration)
	{
		String sourceName = "MATMOS_SRM_" + token;
		
		if (fadeDuration == 0)
			//sndSystem.fadeOut(sourceName, null, 0);
			sndSystem().stop(sourceName);
		
		else
			sndSystem()
			.fadeOut(sourceName, null, ((long) fadeDuration) * 1000L);
		
	}
	
	@Override
	public synchronized void pauseStreaming(int token, float fadeDuration)
	{
		String sourceName = "MATMOS_SRM_" + token;
		sndSystem().pause(sourceName);
		// TODO
		
	}
	
	@Override
	public synchronized void eraseStreamingToken(int token)
	{
		String sourceName = "MATMOS_SRM_" + token;
		
		stopStreaming(token, 0);
		
		sndSystem().removeSource(sourceName);
		
		sourcesAsMusic.remove(sourceName);
		
	}
	/*
	public void addLocator(int i, MAtCustomSheet cs)
	{
		locators.put(i, cs);
		
	}*/
	
	@Override
	public void inputOptions(Properties options)
	{
		if (config == null)
			config = createDefaultOptions();
		
		try
		{
			{
				String query = "volume.generic.value";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					setCustomSoundVolume(Float.parseFloat(prop));
					config.put(query, prop);
				}
				
			}
			{
				String query = "volume.music.value";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					setCustomMusicVolume(Float.parseFloat(prop));
					config.put(query, prop);
				}
				
			}
			{
				String query = "volume.music.minecraft.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					setMusicVolumeIsBasedOffMinecraft(Integer.parseInt(prop) == 1
							? true : false);
					config.put(query, prop);
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
		if (config == null)
			return createDefaultOptions();
		
		config.setProperty("volume.generic.value", "" + getCustomSoundVolume());
		config.setProperty("volume.music.value", "" + getCustomMusicVolume());
		config.setProperty("volume.music.minecraft.use",
				getMusicVolumeIsBasedOffMinecraft() ? "1" : "0");
		
		return config;
	}
	
	@Override
	public void defaultOptions()
	{
		inputOptions(createDefaultOptions());
		
	}
	
	private Properties createDefaultOptions()
	{
		Properties options = new Properties();
		options.setProperty("volume.generic.value", "" + defSoundVolume);
		options.setProperty("volume.music.value", "" + defMusicVolume);
		options.setProperty("volume.music.minecraft.use",
				defMusicVolUsesMinecraft ? "1" : "0");
		
		return options;
		
	}
	
}
