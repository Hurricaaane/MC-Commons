package net.minecraft.src;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3KeyManager;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
 */

public class mod_MinapticsLite extends BaseMod
{
	Minecraft mc;
	KeyBinding zoomKeyBinding;
	Ha3KeyManager keyManager;
	
	MinapticsLiteMouseFilter mouseFilterXAxis;
	MinapticsLiteMouseFilter mouseFilterYAxis;
	
	final int zoomSafetyVariableRestore = 150;
	
	File optionsFile;
	float smootherIntensityWhenIdle;
	int zoomKey;
	float fovLevel;
	float fovLevelTransition;
	float fovLevelSetup;
	boolean fovLevelTransitionning;
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
	
	
	@SuppressWarnings("static-access")
	public mod_MinapticsLite()
	{
		mc = ModLoader.getMinecraftInstance();
		keyManager = new Ha3KeyManager();
		
		smootherIntensityWhenIdle = 4F;
		
		zoomKey = 15; // TAB
		fovLevel = 0.3F;
		zoomDuration = 300;
		maxZoomField = 0.65F;
		smootherIntensity = 0.5F;
		
		isSmootherSettingEvent = false;
		fovLevelTransitionning = false;
		
		disableSmootherEvenDuringZooming = false;
		
		wasMouseSensitivity = 0;
		wasAlreadySmoothing = false;
		
		zoomTime = 0;
		
		eventNum = 0;
		eventNumOnZoom = 0;
		
		lastWorldTime = 0;
		lastTime = 0;
		basePlayerPitch = 0;
		basePlayerYaw = 0;
		
		optionsFile = new File(mc.getMinecraftDir(), "minaptics_options.txt");
		mouseFilterXAxis = new MinapticsLiteMouseFilter();
		mouseFilterYAxis = new MinapticsLiteMouseFilter();
		
		loadOptions();
		fovLevelTransition = fovLevel;
		fovLevelSetup = fovLevel;
		
		zoomKeyBinding = new KeyBinding("key.zoom", zoomKey);
		
		ModLoader.registerKey(this, zoomKeyBinding, true);
		ModLoader.addLocalization("key.zoom", "Zoom");
		
		keyManager.addKeyBinding(zoomKeyBinding, new MinapticsLiteZoomBinding(
				this));
		
		// The 3rd param determines it's it's a "frame" think
		ModLoader.setInGameHook(this, true, false);
		
		updateSmootherStatus();
		
		try
		{
			ModLoader.setPrivateValue(net.minecraft.src.EntityRenderer.class,
					mc.entityRenderer, 7, mouseFilterXAxis);
			ModLoader.setPrivateValue(net.minecraft.src.EntityRenderer.class,
					mc.entityRenderer, 8, mouseFilterYAxis);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void setCameraZoom(float value)
	{
		try
		{
			ModLoader.setPrivateValue(net.minecraft.src.EntityRenderer.class,
					mc.entityRenderer, 24, value);
			ModLoader.setPrivateValue(net.minecraft.src.EntityRenderer.class,
					mc.entityRenderer, 25, value);
			
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		
		return;
		
	}
	
	@Override
	public boolean onTickInGame(float fspan, Minecraft game) //Actually "OnRenderFrameInGame"
	{
		if (mc.theWorld.worldInfo.getWorldTime() != lastWorldTime)
		{
			tickThink();
			lastWorldTime = mc.theWorld.worldInfo.getWorldTime();
			
		}
		
		displayThink();
		runtimeThink();
		
		return true;
		
	}
	
	public void runtimeThink()
	{
		if (isZoomed)
		{
			mc.gameSettings.mouseSensitivity = doChangeSensitivity(wasMouseSensitivity);
			if (smootherLevel == 0F)
				doForceSmoothCamera();
			
		}
		if (shouldChangeFOV())
		{
			float fov = 70F;
			fov += mc.gameSettings.fovSetting * 40F;
			if(mc.thePlayer.isInsideOfMaterial(Material.water))
			{
				fov = (fov * 60F) / 70F;
			}
			setCameraZoom((1F - doChangeFOV(1F)) * -1 * fov);
			
		}
		
	}
	
	
	@Override
	public void keyboardEvent(KeyBinding event)
	{
		keyManager.handleKeyDown(event);
		
	}
	void zoomToggle()
	{
		isZoomed = !isZoomed;
		
		if (isZoomed)
		{
			wasMouseSensitivity = mc.gameSettings.mouseSensitivity;
			
			if (smootherLevel != 0F || !disableSmootherEvenDuringZooming)
			{
				if (smootherLevel == 0F)
					wasAlreadySmoothing = mc.gameSettings.smoothCamera;
				
				mc.gameSettings.smoothCamera = true;
				
			}
			
		}
		else
		{
			mc.gameSettings.mouseSensitivity = wasMouseSensitivity;
			
			if (smootherLevel == 0F)
			{
				mc.gameSettings.smoothCamera = wasAlreadySmoothing;
				
				doLetSmoothCamera();
				
			}
			
		}
		
		if ((System.currentTimeMillis() - zoomTime) > zoomDuration)
			zoomTime = System.currentTimeMillis();
		
		else
			zoomTime = System.currentTimeMillis() * 2 - zoomTime - zoomDuration;
		
	}
	
	public void tickThink()
	{
		keyManager.handleRuntime();
		
		if (isSmootherSettingEvent)
		{
			float rPitch = -mc.thePlayer.rotationPitch;
			float scales = ((rPitch + 90) / 180F);
			
			if (scales == 0)
				disableSmootherEvenDuringZooming = true;
			else
				disableSmootherEvenDuringZooming = false;
			
			if (scales < 0.02F)
				scales = 0F;
			
			else if (scales > 1F)
				scales = 1F;
			
			smootherLevel = scales;
			
		}
		
	}
	
	public void displayThink()
	{
		if (!isSmootherSettingEvent)
			return;
		
		if (!ModLoader.isGUIOpen(null))
		{
			String msg1 = "Close your menu to start tweaking Minaptics.";
			
			ScaledResolution screenRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			int width = screenRes.getScaledWidth();
			//int height = screenRes.getScaledHeight();
			
			int msg1width = mc.fontRenderer.getStringWidth( msg1 );
			mc.fontRenderer.func_50103_a(msg1, (width - msg1width) / 2, 10,
					0xffffff);
			
		}
		else
		{
			String msgup = "Intensity+";
			String msgdown = "Intensity-";
			
			String msg1 = "Smoother:";
			String msg2;
			
			if (smootherLevel == 0F)
			{
				if (disableSmootherEvenDuringZooming)
					msg2 = "Disabled, including while zooming";
				else
					msg2 = "Disabled";
			}
			else
				msg2 = "" + ((int)(smootherLevel * 1000))/10F;
			
			ScaledResolution screenRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			int width = screenRes.getScaledWidth();
			int height = screenRes.getScaledHeight();
			
			int msg1width = mc.fontRenderer.getStringWidth( msg1 );
			mc.fontRenderer.func_50103_a(msg1, (width - msg1width) / 2,
					height / 2 + 10, 0xffffff);
			
			int msg2width = mc.fontRenderer.getStringWidth( msg2 );
			mc.fontRenderer.func_50103_a(msg2, (width - msg2width) / 2, height
					/ 2 + 10 + height / 32, 0xffff00);
			
			int msgupwidth = mc.fontRenderer.getStringWidth( msgup );
			mc.fontRenderer.func_50103_a(msgup, (width - msgupwidth) / 2,
					height
					/ 2 + 10 - height / 8, 0xffff00);
			
			int msgdownwidth = mc.fontRenderer.getStringWidth( msgdown );
			mc.fontRenderer.func_50103_a(msgdown, (width - msgdownwidth) / 2,
					height / 2 + 10 + height / 8, 0xffff00);
			
			//String movemouse = "Move your mouse around, press Zoom key to finish.";
			
			//int movemousewidth = mc.fontRenderer.getStringWidth( movemouse );
			//mc.fontRenderer.drawStringWithShadow(movemouse, (width - movemousewidth) / 2, 10 + height / 16, 0xffff00);
			
		}
		
	}
	
	
	
	void zoomDoBefore()
	{
		if (!ModLoader.isGUIOpen(net.minecraft.src.GuiChat.class))
		{
			if (!ModLoader.isGUIOpen(net.minecraft.src.GuiInventory.class)
					&& !ModLoader
					.isGUIOpen(net.minecraft.src.GuiContainerCreative.class))
			{
				if (isSmootherSettingEvent)
				{
					isSmootherSettingEvent = false;
					saveOptions();
					updateSmootherStatus();
					
				}
				else if (!isZoomed)
				{
					zoomToggle();
					eventNumOnZoom = eventNum;
					
				}
				
			}
			else // Smoother Event
			{
				isSmootherSettingEvent = !isSmootherSettingEvent;
				if (isSmootherSettingEvent)
				{
					mc.gameSettings.smoothCamera = false;
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
		if (isSmootherSettingEvent) return;
		
		if (timeKey == 4)
		{
			fovLevelTransitionning = true;
			
			basePlayerPitch = mc.thePlayer.rotationPitch;
			basePlayerYaw = mc.thePlayer.rotationYaw;
			lastTime = System.currentTimeMillis();
			
		}
		else if (timeKey > 4)
		{
			if (mc.gameSettings.thirdPersonView == 0)
			{
				float diffPitch = basePlayerPitch - mc.thePlayer.rotationPitch;
				
				fovLevelSetup = fovLevel - diffPitch * 0.5F;
				
				if (fovLevelSetup < 0.001F)
					fovLevelSetup = 0.001F;
				
				else if (fovLevelSetup > maxZoomField)
					fovLevelSetup = maxZoomField;
				
			}
			
		}
		
	}
	void zoomDoAfter(int timeKey)
	{
		if (!isSmootherSettingEvent)
		{
			if (timeKey > 4)
			{
				fovLevel = fovLevelSetup;
				saveOptions();
				
			}
			else
			{
				if (isZoomed && (eventNumOnZoom != eventNum))
					zoomToggle();
				
			}
			fovLevelTransitionning = false;
			
		}
		//else // NO NO NO.
		//{
		//updateSmootherStatus();
		//saveOptions();
		
		//}
		
		eventNum++;
		
	}
	public void updateSmootherStatus()
	{
		if (smootherLevel == 0F)
		{
			doLetSmoothCamera();
			mc.gameSettings.smoothCamera = false;
			
		}
		else
		{
			mc.gameSettings.smoothCamera = true;
			doForceSmoothCamera();
			
		}
		
	}
	
	public boolean shouldChangeFOV() {
		return (isZoomed || ((System.currentTimeMillis() - zoomTime) < zoomDuration));
		
	}
	public float doChangeFOV(float inFov) {
		float baseLevel;
		float delta = (System.currentTimeMillis() - lastTime) / 1000F;
		
		delta = delta * 4F;
		
		if (delta > 1F)
			delta = 1F;
		
		lastTime = System.currentTimeMillis();
		
		fovLevelTransition = fovLevelTransition + (fovLevelSetup - fovLevelTransition) * delta;
		
		baseLevel = fovLevelTransition;
		
		
		if ((System.currentTimeMillis() - zoomTime) > zoomDuration)
			return inFov * baseLevel;
		
		float flushtrum = (System.currentTimeMillis() - zoomTime) / (float)zoomDuration;
		
		if (!isZoomed)
			flushtrum = 1F - flushtrum;
		
		flushtrum = flushtrum * flushtrum;
		
		return inFov * (1F - (1F - fovLevel) * flushtrum );
		
	}
	
	
	/*public boolean shouldSmoothCamera()
	{
		return isZoomed;
		
	}*/
	public void doForceSmoothCamera()
	{
		doForceSmoothCameraXAxis();
		doForceSmoothCameraYAxis();
		
	}
	public void doForceSmoothCameraXAxis()
	{
		float f2 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		f2 = f2 * f2 * f2 * 8F;
		float smoothBase = ( smootherLevel == 0F ? smootherIntensityWhenIdle : (1F - smootherLevel * 0.999F) * smootherIntensity );
		
		if (isZoomed)
			smoothBase = smoothBase * 1 / fovLevelSetup;
		
		float cSmooth = f2 * smoothBase;
		if (cSmooth > 1F) cSmooth = 1F;
		
		mouseFilterXAxis.force(cSmooth);
		
	}
	public void doForceSmoothCameraYAxis()
	{
		float f2 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		f2 = f2 * f2 * f2 * 8F;
		float smoothBase = ( smootherLevel == 0F ? smootherIntensityWhenIdle : (1F - smootherLevel * 0.999F) * smootherIntensity );
		
		if (isZoomed)
			smoothBase = smoothBase * 1 / fovLevelSetup;
		
		float cSmooth = f2 * smoothBase;
		if (cSmooth > 1F) cSmooth = 1F;
		
		mouseFilterYAxis.force(cSmooth);
		
	}
	
	public void doLetSmoothCamera()
	{
		doLetSmoothCameraXAxis();
		doLetSmoothCameraYAxis();
		
	}
	public void doLetSmoothCameraXAxis()
	{
		mouseFilterXAxis.let();
		
	}
	public void doLetSmoothCameraYAxis()
	{
		mouseFilterYAxis.let();
		
	}
	
	public boolean shouldChangeSensitivity() {
		return isZoomed;
		
	}
	public float doChangeSensitivity(float f1) {
		return f1 * (float)Math.max(0.5, fovLevelSetup);
		
	}
	
	public void loadOptions()
	{
		try
		{
			if(!optionsFile.exists())
			{
				return;
			}
			BufferedReader bufferedreader = new BufferedReader(new FileReader(optionsFile));
			for(String s = ""; (s = bufferedreader.readLine()) != null;)
			{
				try
				{
					String as[] = s.split(":");
					if(as[0].equals("key_zoom"))
					{
						zoomKey = (int)parseFloat(as[1]);
						
					}
					if(as[0].equals("fovlevel"))
					{
						fovLevel = parseFloat(as[1]);
						
					}
					if(as[0].equals("zoomduration"))
					{
						zoomDuration = (int)parseFloat(as[1]);
						
					}
					if(as[0].equals("maximumzoomfield"))
					{
						maxZoomField = parseFloat(as[1]);
						
					}
					if(as[0].equals("smootherlevel"))
					{
						smootherLevel = parseFloat(as[1]);
						
					}
					if(as[0].equals("smoothershape"))
					{
						smootherIntensity = parseFloat(as[1]);
						
					}
					
					if (as[0].equals("smootherintensitywhenidle"))
					{
						smootherIntensityWhenIdle = parseFloat(as[1]);
						
					}
					if (as[0].equals("disablesmootherevenduringzooming"))
					{
						disableSmootherEvenDuringZooming = parseFloat(as[1]) == 1F
								? true : false;
						
					}
					
				}
				catch(Exception exception1)
				{
					System.out.println((new StringBuilder("Skipping bad option: ")).append(s).toString());
					
				}
			}
			
			bufferedreader.close();
		}
		catch(Exception exception)
		{
			System.out.println("Failed to load MinapticsLite options");
			exception.printStackTrace();
			
		}
		
	}
	
	private float parseFloat(String s)
	{
		if(s.equals("true"))
		{
			return 1.0F;
		}
		if(s.equals("false"))
		{
			return 0.0F;
		} else
		{
			return Float.parseFloat(s);
		}
	}
	
	public void saveOptions()
	{
		try
		{
			PrintWriter printwriter = new PrintWriter(new FileWriter(optionsFile));
			printwriter.println((new StringBuilder("key_zoom:")).append(zoomKey).toString());
			printwriter.println((new StringBuilder("fovlevel:")).append(fovLevel).toString());
			printwriter.println((new StringBuilder("smootherlevel:")).append(smootherLevel).toString());
			printwriter.println((new StringBuilder("smoothershape:")).append(smootherIntensity).toString());
			printwriter.println((new StringBuilder("zoomduration:")).append(zoomDuration).toString());
			printwriter.println((new StringBuilder("maximumzoomfield:")).append(maxZoomField).toString());
			printwriter
			.println((new StringBuilder("smootherintensitywhenidle:"))
					.append(smootherIntensityWhenIdle).toString());
			printwriter.println((new StringBuilder(
					"disablesmootherevenduringzooming:")).append(
							disableSmootherEvenDuringZooming ? "true" : "false")
							.toString());
			
			printwriter.close();
			
		}
		catch(Exception exception)
		{
			System.out.println("Failed to save MinapticsLite options");
			exception.printStackTrace();
			
		}
		
	}
	
	@Override
	public String getVersion()
	{
		return "r10 for 1.1.x";
		
	}
	
	@Override
	public void load()
	{
		// TODO Auto-generated method stub
		
	}
	
}