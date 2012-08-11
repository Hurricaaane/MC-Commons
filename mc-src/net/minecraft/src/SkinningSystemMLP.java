package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;

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

public class SkinningSystemMLP implements SkinningSystem
{
	private SkinningInGameHaddon haddon;
	
	private Pony pony;
	private final String skinPath = "/skin_edit.png";
	private int sort;
	
	private Minecraft mc;
	
	public SkinningSystemMLP(SkinningInGameHaddon haddon)
	{
		this.haddon = haddon;
		this.mc = this.haddon.manager().getMinecraft();
	}
	
	@Override
	public void enable()
	{
		update();
		
	}
	
	@Override
	public void disable()
	{
		String skinUrl = "http://skins.minecraft.net/MinecraftSkins/" + this.mc.thePlayer.username + ".png";
		new CheckOnlinePonySkin(getMyPony(), skinUrl, this.mc.renderEngine).start();
		
	}
	
	@Override
	@SuppressWarnings("static-access")
	public void update()
	{
		this.pony = getMyPony();
		loadSkinFromFile(this.pony, new File(this.mc.getMinecraftDir(), this.skinPath));
		
		if (this.pony == null)
		{
			this.haddon.addMessageToStack("Error: Current player has no Pony model attached.", 4);
			return;
		}
		
		if (this.pony.isPonySkin())
		{
			if (this.pony.isUnicorn)
			{
				if (this.pony.isPegasus)
				{
					this.sort = 4; // Winged Unicorn
					
				}
				else
				{
					this.sort = 3; // Unicorn
					
				}
			}
			else
			{
				if (this.pony.isPegasus)
				{
					this.sort = 2; // Pegasus
					
				}
				else
				{
					this.sort = 1; // Pony
				}
			}
			
		}
		else if (Pony.getPonyLevel() == 2)
		{
			this.sort = 0; // Inaccurate
			
		}
		else
		{
			this.sort = -1; // Human
			
		}
		
	}
	
	@Override
	public void render(float semi)
	{
		/*try
		{
			// debug
			this.haddon.util().setPrivateValueLiteral(Pony.class, null, "ponyLevel", 0, 1);
		}
		catch (PrivateAccessException e)
		{
			return;
		}*/
		
		switch (this.sort)
		{
		case -1:
			this.mc.fontRenderer.drawStringWithShadow("Human edit mode", 2, 2, 0xffff00);
			break;
		case 0:
			this.mc.fontRenderer.drawStringWithShadow("Fallback mode (Invalid pixel, + PonyLevel = 2)", 2, 2, 0xffff00);
			break;
		case 1:
			this.mc.fontRenderer.drawStringWithShadow("Pony edit mode", 2, 2, 0xffff00);
			break;
		case 2:
			this.mc.fontRenderer.drawStringWithShadow("Pegasus edit mode", 2, 2, 0xffff00);
			break;
		case 3:
			this.mc.fontRenderer.drawStringWithShadow("Unicorn edit mode", 2, 2, 0xffff00);
			break;
		case 4:
			this.mc.fontRenderer.drawStringWithShadow("Winged Unicorn edit mode", 2, 2, 0xffff00);
			break;
		default:
			;
		}
		
	}
	
	//
	
	private Pony getMyPony()
	{
		return getPonyOf(this.mc.thePlayer.username);
		
	}
	
	private Pony getPonyOf(String playerName)
	{
		return Pony.getPonyFromRegistry(playerName, this.mc.renderEngine);
		
	}
	
	//
	
	private void loadSkinFromFile(Pony pony, File file)
	{
		try
		{
			loadSkinFromStream(pony, new FileInputStream(file));
		}
		catch (FileNotFoundException e)
		{
			failSkin(e);
		}
		
	}
	
	private void loadSkinFromStream(Pony ponyIn, InputStream instream)
	{
		try
		{
			BufferedImage bufferedimage = ImageIO.read(instream);
			this.pony.checkSkin(bufferedimage);
			
			this.pony.texture = this.skinPath;
			
			RenderEngine re = this.mc.renderEngine;
			re.setupTexture(bufferedimage, re.getTexture(this.pony.texture));
			this.pony.skinUrl = null;
			
		}
		catch (IOException e)
		{
			failSkin(e);
			
		}
		finally
		{
			try
			{
				if (instream != null)
				{
					instream.close();
				}
			}
			catch (IOException e)
			{
			}
			
		}
		
	}
	
	private void failSkin(Exception e)
	{
		if (e != null)
		{
			this.haddon.addMessageToStack("Error: " + e.getMessage(), 2);
		}
		else
		{
			this.haddon.addMessageToStack("Error: (?)", 2);
		}
		
		if (this.pony != null)
		{
			this.pony.isPonySkin = false;
		}
		
	}
	
}
