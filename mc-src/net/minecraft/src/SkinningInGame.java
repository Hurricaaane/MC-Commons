package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;

import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;

public class SkinningInGame extends HaddonImpl implements SupportsTickEvents,
SupportsFrameEvents
{
	private boolean canWork;
	private boolean isCaptureEnabled;
	
	private boolean bindingIsDown;
	private boolean previousFocusState;
	
	@Override
	public void onInitialize()
	{
	}
	
	@Override
	public void onLoad()
	{
		try
		{
			canWork = Class.forName("net.minecraft.src.RenderPony", false, this
					.getClass().getClassLoader()) != null;
			manager().hookTickEvents(true);
			
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("MLP is not installed?");
		}
		
	}
	
	//private String location;
	private Pony pony;
	
	public void refreshCurrentPlayerSkin()
	{
		checkPonySkin(getMyPony(), new File(manager().getMinecraft()
				.getMinecraftDir(), "/pony_edit.png"));
		/*checkPonySkin(getMyPony(), (net.minecraft.client.Minecraft.class)
				.getResource("/pony_edit.png"));*/
		
	}
	
	public Pony getMyPony()
	{
		return getPonyOf(manager().getMinecraft().thePlayer.username);
		
	}
	
	public Pony getPonyOf(String playerName)
	{
		return Pony.getPonyFromRegistry(playerName,
				manager().getMinecraft().renderEngine);
		
	}
	
	public void checkPonySkin(Pony pony, URL url)
	{
		//HttpURLConnection httpurlconnection = null;
		
		try
		{
			/*httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setDoInput(true);
			httpurlconnection.setDoOutput(false);
			httpurlconnection.connect();
			
			if (httpurlconnection.getResponseCode() / 100 == 4)
			{
				failPonySkin();
				return;
			}*/
			
			InputStream is = url.openStream();
			
			checkPonySkin(pony, is);
			
		}
		catch (Exception e)
		{
			failPonySkin();
			
		}
		
	}
	
	public void checkPonySkin(Pony pony, File file)
	{
		try
		{
			checkPonySkin(pony, new FileInputStream(file));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			failPonySkin();
		}
		
	}
	
	public void checkPonySkin(Pony ponyIn, InputStream instream)
	{
		pony = ponyIn;
		
		try
		{
			BufferedImage bufferedimage = ImageIO.read(instream);
			pony.checkSkin(bufferedimage);
			
			if (!pony.isPonySkin)
			{
				pony.isPony = true;
				pony.isPegasus = pony.backgroundIsPegasus;
				pony.isUnicorn = pony.backgroundIsUnicorn;
				pony.wantTail = pony.backgroundWantTail;
				pony.isMale = pony.backgroundIsMale;
				pony.advancedTexturing = pony.backgroundAdvancedTexturing;
				
			}
			else
			{
				pony.isPonySkin = false;
				pony.texture = "/pony_edit.png";
				
				RenderEngine re = manager().getMinecraft().renderEngine;
				re.setupTexture(bufferedimage, re.getTexture(pony.texture));
				pony.skinUrl = null;
				
			}
			
		}
		catch (Exception exception)
		{
			failPonySkin();
			return;
			
		}
		finally
		{
			try
			{
				if (instream != null)
					instream.close();
			}
			catch (IOException e)
			{
			}
			
		}
		
	}
	
	public void checkOnlinePlayerSkin()
	{
		String skinUrl = "http://s3.amazonaws.com/MinecraftSkins/"
				+ manager().getMinecraft().thePlayer.username + ".png";
		(new CheckOnlinePonySkin(getMyPony(), skinUrl)).start();
		
	}
	
	private void failPonySkin()
	{
		System.out.println("Failed to read a player texture");
		
		if (pony != null)
			pony.isPonySkin = false;
		
	}
	
	@Override
	public void onTick()
	{
		/*System.out.println(getMyPony().texture
				+ " "
				+ manager().getMinecraft().thePlayer.texture);
		System.out.println(getMyPony().skinUrl + " "
				+ manager().getMinecraft().thePlayer.skinUrl);*/
		
		if (Keyboard.isKeyDown(29) && Keyboard.isKeyDown(42)
				&& Keyboard.isKeyDown(25))
		{
			if (!bindingIsDown)
			{
				toggleCaptureState();
				bindingIsDown = true;
				
			}
			
		}
		else if (bindingIsDown)
		{
			bindingIsDown = false;
		}
		
		if (isCaptureEnabled)
		{
			boolean isRefreshTick = util().getClientTick() % 30 == 0;
			
			if (isRefreshTick)
				refreshCurrentPlayerSkin();
			
			if (previousFocusState != manager().getMinecraft().inGameHasFocus)
			{
				previousFocusState = manager().getMinecraft().inGameHasFocus;
				
				if (!isRefreshTick && previousFocusState == true)
					refreshCurrentPlayerSkin();
				
			}
			
		}
		
	}
	
	private void toggleCaptureState()
	{
		if (!isCaptureEnabled)
			enableCaptureState();
		else
			disableCaptureState();
		
	}
	
	public void enableCaptureState()
	{
		if (isCaptureEnabled)
			return;
		isCaptureEnabled = true;
		
		previousFocusState = true;
		
		refreshCurrentPlayerSkin();
		manager().hookFrameEvents(true);
		
	}
	
	public void disableCaptureState()
	{
		if (!isCaptureEnabled)
			return;
		isCaptureEnabled = false;
		
		checkOnlinePlayerSkin();
		manager().hookFrameEvents(false);
		
	}
	
	@Override
	public void onFrame(float semi)
	{
		manager().getMinecraft().fontRenderer.drawStringWithShadow(
				"Pony edit mode", 2, 2, 0xffff00);
	}
	
}
