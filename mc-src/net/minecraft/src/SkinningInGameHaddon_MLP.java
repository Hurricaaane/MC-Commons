package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.Display;

import eu.ha3.mc.convenience.Ha3EdgeModel;
import eu.ha3.mc.convenience.Ha3EdgeTrigger;
import eu.ha3.mc.convenience.Ha3StaticUtilities;
import eu.ha3.mc.haddon.SupportsFrameEvents;
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

public class SkinningInGameHaddon_MLP extends HaddonImpl implements
SupportsTickEvents,
SupportsFrameEvents
{
	private boolean canWork;
	private boolean isCaptureEnabled;
	
	private Ha3EdgeTrigger bindTrigger;
	
	private boolean previousFocusState;
	
	private Pony pony;
	
	@Override
	public void onLoad()
	{
		if (Ha3StaticUtilities.classExists("Pony", this)
				|| Ha3StaticUtilities.classExists("net.minecraft.src.Pony",
						this))
		{
			bindTrigger = new Ha3EdgeTrigger(new Ha3EdgeModel() {
				@Override
				public void onTrueEdge()
				{
					toggleCaptureState();
				}
				
				@Override
				public void onFalseEdge()
				{
				}
				
			});
			manager().hookTickEvents(true);
		}
		else
			System.out.println("MLP is not installed?");
		
	}
	
	@SuppressWarnings("static-access")
	public void refreshCurrentPlayerSkin()
	{
		checkPonySkin(getMyPony(), new File(manager().getMinecraft()
				.getMinecraftDir(), "/pony_edit.png"));
		
	}
	
	private Pony getMyPony()
	{
		return getPonyOf(manager().getMinecraft().thePlayer.username);
		
	}
	
	private Pony getPonyOf(String playerName)
	{
		return Pony.getPonyFromRegistry(playerName,
				manager().getMinecraft().renderEngine);
		
	}
	
	public void checkPonySkin(Pony pony, URL url)
	{
		try
		{
			InputStream is = url.openStream();
			
			checkPonySkin(pony, is);
			
		}
		catch (Exception e)
		{
			failPonySkin();
			
		}
		
	}
	
	private void checkPonySkin(Pony pony, File file)
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
	
	private void checkPonySkin(Pony ponyIn, InputStream instream)
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
	
	private void checkOnlinePlayerSkin()
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
		// ctrl shift P
		bindTrigger.signalState(util().areKeysDown(29, 42, 25));
		
		if (isCaptureEnabled)
		{
			boolean isRefreshTick = util().getClientTick() % 30 == 0;
			
			if (isRefreshTick)
				refreshCurrentPlayerSkin();
			
			if (!Display.isActive()
					&& util().isCurrentScreen(
							net.minecraft.src.GuiIngameMenu.class))
			{
				manager().getMinecraft().displayGuiScreen(
						new SkinningInGamePauseGUI());
				
			}
			
			if (previousFocusState != Display.isActive())
			{
				previousFocusState = Display.isActive();
				
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
