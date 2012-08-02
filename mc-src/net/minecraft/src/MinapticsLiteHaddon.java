package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3KeyManager;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsKeyEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;

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

public class MinapticsLiteHaddon extends HaddonImpl
	implements SupportsFrameEvents, SupportsTickEvents, SupportsKeyEvents
{
	private Minecraft mc;
	
	private Ha3KeyManager keyManager;
	
	private MinapticsLiteMouseFilter mouseFilterXAxis;
	private MinapticsLiteMouseFilter mouseFilterYAxis;
	
	final int zoomSafetyVariableRestore = 150;
	
	File optionsFile;
	float smootherIntensityWhenIdle;
	int zoomKey;
	float fovLevel;
	float fovLevelTransition;
	float fovLevelSetup;
	boolean fovLevelTransitionning;
	float minZoomField;
	float maxZoomField;
	boolean disableSmootherEvenDuringZooming;
	
	boolean isZoomed;
	int eventNumOnZoom;
	
	long zoomTime;
	int zoomDuration;
	
	long lastWorldTime;
	long lastTime;
	
	float basePlayerPitch;
	float basePlayerYaw;
	
	int eventNum;
	
	float wasMouseSensitivity;
	boolean wasAlreadySmoothing;
	
	float smootherLevel;
	float smootherIntensity;
	
	boolean isSmootherSettingEvent;
	
	@Override
	@SuppressWarnings("static-access")
	public void onLoad()
	{
		this.mc = manager().getMinecraft();
		this.keyManager = new Ha3KeyManager();
		
		this.smootherIntensityWhenIdle = 4F;
		
		this.zoomKey = 15; // TAB
		this.fovLevel = 0.3F;
		this.zoomDuration = 300;
		this.minZoomField = 0.001F;
		this.maxZoomField = 0.65F;
		this.smootherIntensity = 0.5F;
		
		this.isSmootherSettingEvent = false;
		this.fovLevelTransitionning = false;
		
		this.disableSmootherEvenDuringZooming = false;
		
		this.wasMouseSensitivity = 0;
		this.wasAlreadySmoothing = false;
		
		this.zoomTime = 0;
		
		this.eventNum = 0;
		this.eventNumOnZoom = 0;
		
		this.lastWorldTime = 0;
		this.lastTime = 0;
		this.basePlayerPitch = 0;
		this.basePlayerYaw = 0;
		
		this.optionsFile = new File(this.mc.getMinecraftDir(), "minaptics_options.txt");
		this.mouseFilterXAxis = new MinapticsLiteMouseFilter();
		this.mouseFilterYAxis = new MinapticsLiteMouseFilter();
		
		loadOptions();
		this.fovLevelTransition = this.fovLevel;
		this.fovLevelSetup = this.fovLevel;
		
		KeyBinding zoomKeyBinding = new KeyBinding("key.zoom", this.zoomKey);
		manager().addKeyBinding(zoomKeyBinding, "Zoom");
		this.keyManager.addKeyBinding(zoomKeyBinding, new MinapticsLiteZoomBinding(this));
		
		updateSmootherStatus();
		
		try
		{
			util().setPrivateValueLiteral(
				net.minecraft.src.EntityRenderer.class, this.mc.entityRenderer, "v", 7, this.mouseFilterXAxis);
			util().setPrivateValueLiteral(
				net.minecraft.src.EntityRenderer.class, this.mc.entityRenderer, "w", 8, this.mouseFilterYAxis);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
		manager().hookFrameEvents(true);
		manager().hookTickEvents(true);
		
	}
	
	public void setCameraZoom(float value)
	{
		try
		{
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
		displayThink();
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
			
			if (this.smootherLevel != 0F || !this.disableSmootherEvenDuringZooming)
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
		
		if (System.currentTimeMillis() - this.zoomTime > this.zoomDuration)
		{
			this.zoomTime = System.currentTimeMillis();
		}
		else
		{
			this.zoomTime = System.currentTimeMillis() * 2 - this.zoomTime - this.zoomDuration;
		}
		
	}
	
	@Override
	public void onTick()
	{
		this.keyManager.handleRuntime();
		
		if (this.isSmootherSettingEvent)
		{
			float rPitch = -this.mc.thePlayer.rotationPitch;
			float scales = (rPitch + 90) / 180F;
			
			if (scales == 0)
			{
				this.disableSmootherEvenDuringZooming = true;
			}
			else
			{
				this.disableSmootherEvenDuringZooming = false;
			}
			
			if (scales < 0.02F)
			{
				scales = 0F;
			}
			else if (scales > 1F)
			{
				scales = 1F;
			}
			
			this.smootherLevel = scales;
			
		}
		
	}
	
	public void displayThink()
	{
		if (!this.isSmootherSettingEvent)
			return;
		
		if (!util().isCurrentScreen(null))
		{
			util().prepareDrawString();
			util().drawString(
				"Close your menu to start tweaking Minaptics.", 0.5f, 0.01f, 0, 0, '8', 255, 255, 255, 255, true);
			
		}
		else
		{
			String intensity;
			if (this.smootherLevel == 0F)
			{
				if (this.disableSmootherEvenDuringZooming)
				{
					intensity = "Disabled, even when zoomed in";
				}
				else
				{
					intensity = "Disabled";
				}
			}
			else
			{
				intensity = "" + (int) (this.smootherLevel * 1000) / 10F;
			}
			
			int height = this.mc.fontRenderer.FONT_HEIGHT;
			
			util().prepareDrawString();
			util().drawString("Intensity+", 0.55f, 0.5f, 0, -height, '1', 255, 255, 0, 255, true);
			util().drawString("Intensity-", 0.55f, 0.5f, 0, height, '7', 255, 255, 0, 255, true);
			util().drawString(intensity, 0.55f, 0.5f, 0, 0, '4', 255, 255, 255, 255, true);
			
		}
		
	}
	
	void zoomDoBefore()
	{
		if (!util().isCurrentScreen(net.minecraft.src.GuiChat.class))
		{
			if (!util().isCurrentScreen(net.minecraft.src.GuiInventory.class)
				&& !util().isCurrentScreen(net.minecraft.src.GuiContainerCreative.class))
			{
				if (this.isSmootherSettingEvent)
				{
					this.isSmootherSettingEvent = false;
					saveOptions();
					updateSmootherStatus();
					
				}
				else if (!this.isZoomed)
				{
					zoomToggle();
					this.eventNumOnZoom = this.eventNum;
					
				}
				
			}
			else
			// Smoother Event
			{
				this.isSmootherSettingEvent = !this.isSmootherSettingEvent;
				if (this.isSmootherSettingEvent)
				{
					this.mc.gameSettings.smoothCamera = false;
					doLetSmoothCamera();
					
				}
				else
				{
					updateSmootherStatus();
					
				}
				
			}
			
		}
		
	}
	
	void zoomDoDuring(int timeKey)
	{
		if (this.isSmootherSettingEvent)
			return;
		
		if (timeKey == 4)
		{
			this.fovLevelTransitionning = true;
			
			this.basePlayerPitch = this.mc.thePlayer.rotationPitch;
			this.basePlayerYaw = this.mc.thePlayer.rotationYaw;
			this.lastTime = System.currentTimeMillis();
			
		}
		else if (timeKey > 4)
		{
			if (this.mc.gameSettings.thirdPersonView == 0)
			{
				float diffPitch = this.basePlayerPitch - this.mc.thePlayer.rotationPitch;
				
				this.fovLevelSetup = this.fovLevel - diffPitch * 0.5F;
				
				if (this.fovLevelSetup < this.minZoomField)
				{
					this.fovLevelSetup = this.minZoomField;
				}
				else if (this.fovLevelSetup > this.maxZoomField)
				{
					this.fovLevelSetup = this.maxZoomField;
				}
				
			}
			
		}
		
	}
	
	void zoomDoAfter(int timeKey)
	{
		if (!this.isSmootherSettingEvent)
		{
			if (timeKey > 4)
			{
				this.fovLevel = this.fovLevelSetup;
				saveOptions();
				
			}
			else
			{
				if (this.isZoomed && this.eventNumOnZoom != this.eventNum)
				{
					zoomToggle();
				}
				
			}
			this.fovLevelTransitionning = false;
			
		}
		
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
		return this.isZoomed || System.currentTimeMillis() - this.zoomTime < this.zoomDuration;
		
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
		
		if (System.currentTimeMillis() - this.zoomTime > this.zoomDuration)
			return inFov * baseLevel;
		
		float flushtrum = (System.currentTimeMillis() - this.zoomTime) / (float) this.zoomDuration;
		
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
	
	public void loadOptions()
	{
		try
		{
			if (!this.optionsFile.exists())
				return;
			BufferedReader bufferedreader = new BufferedReader(new FileReader(this.optionsFile));
			for (String s = ""; (s = bufferedreader.readLine()) != null;)
			{
				try
				{
					String as[] = s.split(":");
					if (as[0].equals("key_zoom"))
					{
						this.zoomKey = (int) parseFloat(as[1]);
						
					}
					if (as[0].equals("fovlevel"))
					{
						this.fovLevel = parseFloat(as[1]);
						
					}
					if (as[0].equals("zoomduration"))
					{
						this.zoomDuration = (int) parseFloat(as[1]);
						
					}
					if (as[0].equals("maximumzoomfield"))
					{
						this.maxZoomField = parseFloat(as[1]);
						
					}
					if (as[0].equals("minimumzoomfield"))
					{
						this.minZoomField = parseFloat(as[1]);
						
					}
					if (as[0].equals("smootherlevel"))
					{
						this.smootherLevel = parseFloat(as[1]);
						
					}
					if (as[0].equals("smoothershape"))
					{
						this.smootherIntensity = parseFloat(as[1]);
						
					}
					
					if (as[0].equals("smootherintensitywhenidle"))
					{
						this.smootherIntensityWhenIdle = parseFloat(as[1]);
						
					}
					if (as[0].equals("disablesmootherevenduringzooming"))
					{
						this.disableSmootherEvenDuringZooming = parseFloat(as[1]) == 1F ? true : false;
						
					}
					
				}
				catch (Exception exception1)
				{
					System.out.println(new StringBuilder("Skipping bad option: ").append(s).toString());
					
				}
			}
			
			bufferedreader.close();
		}
		catch (Exception exception)
		{
			System.out.println("Failed to load MinapticsLite options");
			exception.printStackTrace();
			
		}
		
	}
	
	private float parseFloat(String s)
	{
		if (s.equals("true"))
			return 1.0F;
		if (s.equals("false"))
			return 0.0F;
		else
			return Float.parseFloat(s);
	}
	
	public void saveOptions()
	{
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
			
		}
		
	}
	
}