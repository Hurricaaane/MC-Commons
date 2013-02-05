package net.minecraft.src;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3KeyManager;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsKeyEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;
import eu.ha3.util.property.simple.ConfigProperty;

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

public class MinapticsHaddon extends HaddonImpl implements SupportsFrameEvents, SupportsTickEvents, SupportsKeyEvents
{
	private Minecraft mc;
	
	private Ha3KeyManager keyManager;
	
	private MinapticsMouseFilter mouseFilterXAxis;
	private MinapticsMouseFilter mouseFilterYAxis;
	
	/*
		this.zoomKey = (int) parseFloat(as[1]);
		this.zoomDuration = (int) parseFloat(as[1]);
		this.maxZoomField = parseFloat(as[1]);
		this.minZoomField = parseFloat(as[1]);
		this.smootherLevel = parseFloat(as[1]);
		this.smootherIntensity = parseFloat(as[1]);
		this.smootherIntensityWhenIdle = parseFloat(as[1]);
		this.disableSmootherEvenDuringZooming = parseFloat(as[1]) == 1F ? true : false;
		*/
	
	private File optionsFile;
	private float smootherIntensityWhenIdle;
	private float fovLevel;
	private float fovLevelTransition;
	private float fovLevelSetup;
	
	private boolean isZoomed;
	private int eventNumOnZoom;
	
	private long zoomTime;
	
	private long lastTime;
	
	private float basePlayerPitch;
	
	private int eventNum;
	
	private float wasMouseSensitivity;
	private boolean wasAlreadySmoothing;
	
	private float smootherLevel;
	private float smootherIntensity;
	
	private MinapticsVariator VAR;
	private ConfigProperty memory;
	
	@Override
	@SuppressWarnings("static-access")
	public void onLoad()
	{
		this.mc = manager().getMinecraft();
		this.keyManager = new Ha3KeyManager();
		
		this.memory = new ConfigProperty();
		this.memory.setProperty("fov_level", 0.3f);
		this.memory.commit();
		
		// Load memory from source
		try
		{
			this.memory.setSource(new File(Minecraft.getMinecraftDir(), "minaptics_memory.cfg").getCanonicalPath());
			this.memory.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error caused memory not to work: " + e.getMessage());
		}
		
		this.VAR = new MinapticsVariator();
		File configFile = new File(Minecraft.getMinecraftDir(), "minaptics.cfg");
		if (configFile.exists())
		{
			log("Config file found. Loading...");
			try
			{
				ConfigProperty config = new ConfigProperty();
				config.setSource(configFile.getCanonicalPath());
				config.load();
				
				MinapticsVariator var = new MinapticsVariator();
				var.loadConfig(config);
				
				this.VAR = var;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			log("Loaded.");
		}
		
		//
		
		this.smootherIntensityWhenIdle = 4F;
		
		this.fovLevel = this.memory.getFloat("fov_level");
		this.smootherIntensity = 0.5F;
		
		this.wasMouseSensitivity = 0;
		this.wasAlreadySmoothing = false;
		
		this.zoomTime = 0;
		
		this.eventNum = 0;
		this.eventNumOnZoom = 0;
		
		this.lastTime = 0;
		this.basePlayerPitch = 0;
		
		this.optionsFile = new File(this.mc.getMinecraftDir(), "minaptics_options.txt");
		this.mouseFilterXAxis = new MinapticsMouseFilter();
		this.mouseFilterYAxis = new MinapticsMouseFilter();
		
		this.fovLevelTransition = this.fovLevel;
		this.fovLevelSetup = this.fovLevel;
		
		KeyBinding zoomKeyBinding = new KeyBinding("key.zoom", this.VAR.ZOOM_KEY);
		manager().addKeyBinding(zoomKeyBinding, "Zoom (Minaptics)");
		this.keyManager.addKeyBinding(zoomKeyBinding, new MinapticsZoomBinding(this));
		
		if (this.VAR.SMOOTHER_ENABLE)
		{
			updateSmootherStatus();
			try
			{
				// mouseFilterXAxis
				// mouseFilterYAxis
				util().setPrivateValueLiteral(
					net.minecraft.src.EntityRenderer.class, this.mc.entityRenderer, "v", 7, this.mouseFilterXAxis);
				util().setPrivateValueLiteral(
					net.minecraft.src.EntityRenderer.class, this.mc.entityRenderer, "w", 8, this.mouseFilterYAxis);
			}
			catch (PrivateAccessException e)
			{
				e.printStackTrace();
			}
		}
		
		manager().hookFrameEvents(true);
		manager().hookTickEvents(true);
		
	}
	
	public void setCameraZoom(float value)
	{
		try
		{
			// debugCamFOV
			// prevDebugCamFOV
			util().setPrivateValueLiteral(
				net.minecraft.src.EntityRenderer.class, this.mc.entityRenderer, "M", 24, value);
			util().setPrivateValueLiteral(
				net.minecraft.src.EntityRenderer.class, this.mc.entityRenderer, "N", 25, value);
			
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
		return;
		
	}
	
	@Override
	public void onFrame(float semi)
	{
		runtimeThink();
		
	}
	
	public void runtimeThink()
	{
		if (this.isZoomed)
		{
			this.mc.gameSettings.mouseSensitivity = doChangeSensitivity(this.wasMouseSensitivity);
			if (this.smootherLevel == 0F)
			{
				doForceSmoothCamera();
			}
			
		}
		if (shouldChangeFOV())
		{
			float fov = 70F;
			fov += this.mc.gameSettings.fovSetting * 40F;
			
			if (this.mc.thePlayer.isInsideOfMaterial(Material.water))
			{
				fov = fov * 60F / 70F;
			}
			
			setCameraZoom((1F - doChangeFOV(1F)) * -1 * fov);
			
		}
		
	}
	
	@Override
	public void onKey(KeyBinding event)
	{
		this.keyManager.handleKeyDown(event);
		
	}
	
	void zoomToggle()
	{
		this.isZoomed = !this.isZoomed;
		
		if (this.isZoomed)
		{
			this.wasMouseSensitivity = this.mc.gameSettings.mouseSensitivity;
			
			if (this.smootherLevel != 0F || !this.VAR.SMOOTHER_WHILE_ZOOMED)
			{
				if (this.smootherLevel == 0F)
				{
					this.wasAlreadySmoothing = this.mc.gameSettings.smoothCamera;
				}
				
				this.mc.gameSettings.smoothCamera = true;
				
			}
			
		}
		else
		{
			this.mc.gameSettings.mouseSensitivity = this.wasMouseSensitivity;
			
			if (this.smootherLevel == 0F)
			{
				this.mc.gameSettings.smoothCamera = this.wasAlreadySmoothing;
				
				doLetSmoothCamera();
				
			}
			
		}
		
		if (System.currentTimeMillis() - this.zoomTime > this.VAR.ZOOM_DURATION)
		{
			this.zoomTime = System.currentTimeMillis();
		}
		else
		{
			this.zoomTime = System.currentTimeMillis() * 2 - this.zoomTime - this.VAR.ZOOM_DURATION;
		}
		
	}
	
	@Override
	public void onTick()
	{
		this.keyManager.handleRuntime();
	}
	
	void zoomDoBefore()
	{
		if (!util().isCurrentScreen(net.minecraft.src.GuiChat.class))
		{
			if (!util().isCurrentScreen(net.minecraft.src.GuiInventory.class)
				&& !util().isCurrentScreen(net.minecraft.src.GuiContainerCreative.class))
			{
				if (!this.isZoomed)
				{
					zoomToggle();
					this.eventNumOnZoom = this.eventNum;
					
				}
				
			}
			
		}
		
	}
	
	private boolean isHolding;
	
	void zoomDoDuring(int timeKey)
	{
		if (timeKey >= 4 && !this.isHolding)
		{
			this.isHolding = true;
			
			this.basePlayerPitch = this.mc.thePlayer.rotationPitch;
			this.lastTime = System.currentTimeMillis();
			
		}
		else if (timeKey >= 4)
		{
			if (this.mc.gameSettings.thirdPersonView == 0)
			{
				float diffPitch = this.basePlayerPitch - this.mc.thePlayer.rotationPitch;
				
				this.fovLevelSetup = this.fovLevel - diffPitch * 0.5F;
				
				if (this.fovLevelSetup < this.VAR.FOV_MIN)
				{
					this.fovLevelSetup = this.VAR.FOV_MIN;
				}
				else if (this.fovLevelSetup > this.VAR.FOV_MAX)
				{
					this.fovLevelSetup = this.VAR.FOV_MAX;
				}
				
			}
			
		}
		
	}
	
	void zoomDoAfter(int timeKey)
	{
		
		if (timeKey >= 4)
		{
			this.fovLevel = this.fovLevelSetup;
			this.memory.setProperty("fov_level", this.fovLevelSetup);
			
		}
		else
		{
			if (this.isZoomed && this.eventNumOnZoom != this.eventNum)
			{
				zoomToggle();
				saveMemory();
			}
			
		}
		this.isHolding = false;
		
		this.eventNum++;
		
	}
	
	public void updateSmootherStatus()
	{
		if (this.smootherLevel == 0F)
		{
			doLetSmoothCamera();
			this.mc.gameSettings.smoothCamera = false;
			
		}
		else
		{
			this.mc.gameSettings.smoothCamera = true;
			doForceSmoothCamera();
			
		}
		
	}
	
	public boolean shouldChangeFOV()
	{
		return this.isZoomed || System.currentTimeMillis() - this.zoomTime < this.VAR.ZOOM_DURATION;
		
	}
	
	public float doChangeFOV(float inFov)
	{
		float baseLevel;
		float delta = (System.currentTimeMillis() - this.lastTime) / 1000F;
		
		delta = delta * 4F;
		
		if (delta > 1F)
		{
			delta = 1F;
		}
		
		this.lastTime = System.currentTimeMillis();
		
		this.fovLevelTransition = this.fovLevelTransition + (this.fovLevelSetup - this.fovLevelTransition) * delta;
		
		baseLevel = this.fovLevelTransition;
		
		if (System.currentTimeMillis() - this.zoomTime > this.VAR.ZOOM_DURATION)
			return inFov * baseLevel;
		
		float flushtrum = (System.currentTimeMillis() - this.zoomTime) / (float) this.VAR.ZOOM_DURATION;
		
		if (!this.isZoomed)
		{
			flushtrum = 1F - flushtrum;
		}
		
		flushtrum = flushtrum * flushtrum;
		
		return inFov * (1F - (1F - this.fovLevel) * flushtrum);
		
	}
	
	public void doForceSmoothCamera()
	{
		doForceSmoothCameraXAxis();
		doForceSmoothCameraYAxis();
	}
	
	public void doForceSmoothCameraXAxis()
	{
		float f2 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		f2 = f2 * f2 * f2 * 8F;
		float smoothBase =
			this.smootherLevel == 0F ? this.smootherIntensityWhenIdle : (1F - this.smootherLevel * 0.999F)
				* this.smootherIntensity;
		
		if (this.isZoomed)
		{
			smoothBase = smoothBase * 1 / this.fovLevelSetup;
		}
		
		float cSmooth = f2 * smoothBase;
		if (cSmooth > 1F)
		{
			cSmooth = 1F;
		}
		
		this.mouseFilterXAxis.force(cSmooth);
		
	}
	
	public void doForceSmoothCameraYAxis()
	{
		float f2 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		f2 = f2 * f2 * f2 * 8F;
		float smoothBase =
			this.smootherLevel == 0F ? this.smootherIntensityWhenIdle : (1F - this.smootherLevel * 0.999F)
				* this.smootherIntensity;
		
		if (this.isZoomed)
		{
			smoothBase = smoothBase * 1 / this.fovLevelSetup;
		}
		
		float cSmooth = f2 * smoothBase;
		if (cSmooth > 1F)
		{
			cSmooth = 1F;
		}
		
		this.mouseFilterYAxis.force(cSmooth);
		
	}
	
	public void doLetSmoothCamera()
	{
		doLetSmoothCameraXAxis();
		doLetSmoothCameraYAxis();
		
	}
	
	public void doLetSmoothCameraXAxis()
	{
		this.mouseFilterXAxis.let();
		
	}
	
	public void doLetSmoothCameraYAxis()
	{
		this.mouseFilterYAxis.let();
		
	}
	
	public boolean shouldChangeSensitivity()
	{
		return this.isZoomed;
		
	}
	
	public float doChangeSensitivity(float f1)
	{
		return f1 * (float) Math.max(0.5, this.fovLevelSetup);
		
	}
	
	public void saveMemory()
	{
		// If there were changes...
		if (this.memory.commit())
		{
			log("Saving configuration...");
			
			// Write changes on disk.
			this.memory.save();
		}
		
		/*
		try
		{
			PrintWriter printwriter = new PrintWriter(new FileWriter(this.optionsFile));
			printwriter.println(new StringBuilder("key_zoom:").append(this.zoomKey).toString());
			printwriter.println(new StringBuilder("fovlevel:").append(this.fovLevel).toString());
			printwriter.println(new StringBuilder("smootherlevel:").append(this.smootherLevel).toString());
			printwriter.println(new StringBuilder("smoothershape:").append(this.smootherIntensity).toString());
			printwriter.println(new StringBuilder("zoomduration:").append(this.zoomDuration).toString());
			printwriter.println(new StringBuilder("maximumzoomfield:").append(this.maxZoomField).toString());
			printwriter.println(new StringBuilder("minimumzoomfield:").append(this.minZoomField).toString());
			printwriter.println(new StringBuilder("smootherintensitywhenidle:")
				.append(this.smootherIntensityWhenIdle).toString());
			printwriter.println(new StringBuilder("disablesmootherevenduringzooming:").append(
				this.disableSmootherEvenDuringZooming ? "true" : "false").toString());
			
			printwriter.close();
			
		}
		catch (Exception exception)
		{
			System.out.println("Failed to save MinapticsLite options");
			exception.printStackTrace();
		}*/
		
	}
	
	public void log(String contents)
	{
		System.out.println("(Minaptics) " + contents);
		
	}
	
}