package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
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

public class DebuggingHa3Haddon extends HaddonImpl implements SupportsTickEvents, SupportsFrameEvents
{
	private EdgeTrigger button;
	private boolean toggle;
	private RenderSpawnPoints renderRelay;
	
	@Override
	public void onLoad()
	{
		this.button = new EdgeTrigger(new EdgeModel() {
			@Override
			public void onTrueEdge()
			{
				in();
			}
			
			@Override
			public void onFalseEdge()
			{
				out();
			}
		});
		
		this.renderRelay = new RenderSpawnPoints(manager().getMinecraft());
		
		manager().hookTickEvents(true);
		manager().hookFrameEvents(true);
		manager().addRenderable(this.renderRelay.getRenderEntityClass(), this.renderRelay.getRenderHook());
	}
	
	protected void in()
	{
		/*Minecraft mc = manager().getMinecraft();
		mc.thePlayer.sendChatMessage("beep boop i am a robot1");
		mc.thePlayer.sendChatMessage("beep boop i am a robot2");
		mc.thePlayer.sendChatMessage("beep boop i am a robot3");
		mc.thePlayer.sendChatMessage("beep boop i am a robot4");
		mc.thePlayer.sendChatMessage("beep boop i am a robot5");
		mc.thePlayer.sendChatMessage("beep boop i am a robot6");
		mc.thePlayer.sendChatMessage("beep boop i am a robot7");
		mc.theWorld.sendQuittingDisconnectingPacket();
		mc.loadWorld((WorldClient) null);
		mc.displayGuiScreen(new GuiMainMenu());*/
		
		this.toggle = !this.toggle;
	}
	
	protected void out()
	{
	}
	
	@Override
	public void onTick()
	{
		this.renderRelay.ensureExists();
		this.button.signalState(util().areKeysDown(29, 42, 49));
	}
	
	@Override
	public void onFrame(float semi)
	{
		if (!this.toggle)
			return;
		
		/*int sc = 1400;
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
		
		float var = 0.5f;
		int tar = (int) (var * 255);
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawing(5);
		tessellator.setColorRGBA(tar, tar, tar, 255);
		tessellator.addVertex(0, 0, 0.0D);
		tessellator.addVertex(0, sc, 0.0D);
		tessellator.addVertex(sc, sc, 0.0D);
		tessellator.addVertex(sc, 0, 0.0D);
		tessellator.addVertex(0, 0, 0.0D);
		tessellator.draw();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);*/
		
		if (true)
		{
			showGPS(1523, 736, 0x00FF00);
			showGPS(602, 275, 0xFFFF00);
			showGPS(-102, 187, 0xFF0000);
			showGPS(-508, -55, 0xFFFFFF);
		}
		else
		{
			showGPS(1497 / 8, 672 / 8, 0x0000FF); // inverse nether portal for sandstone village
		}
		
		//showGPS(34, -509, 0x00FF00);
	}
	
	private void showGPS(int xDest, int zDest, int color)
	{
		EntityPlayer ply = manager().getMinecraft().thePlayer;
		double myX = ply.posX;
		double myZ = ply.posZ;
		
		double toX = xDest - myX;
		double toZ = zDest - myZ;
		double distance = Math.sqrt(toX * toX + toZ * toZ);
		
		double ang = Math.atan2(toZ, toX) / Math.PI * 180;
		
		double diffang = Math.floor(ang - 90 - ply.rotationYaw);
		
		double modu = diffang - Math.floor(diffang / 360) * 360;
		if (modu > 180)
		{
			modu = modu - 360;
		}
		
		util().prepareDrawString();
		util().drawString(
			(int) distance + "", (float) modu / 150f + 0.5f, 0.05f, 0, 0, '5', color >> 16 & 0xFF, color >> 8 & 0xFF,
			color & 0xFF, color >> 24 & 0xFF, true);
		
	}
	
	private class RenderSpawnPoints extends Ha3RenderRelay
	{
		public RenderSpawnPoints(Minecraft mc)
		{
			super(mc);
		}
		
		@Override
		public void doRender(Entity entity, double dx, double dy, double dz, float f, float semi)
		{
			if (true)
				return;
			
			EntityPlayer ply = manager().getMinecraft().thePlayer;
			World world = manager().getMinecraft().theWorld;
			
			int x = (int) Math.floor(ply.posX);
			int y = (int) Math.floor(ply.posY);
			int z = (int) Math.floor(ply.posZ);
			
			final int rad = 64;
			final int hei = 32;
			
			beginTrace();
			/*for (int i = x - rad; i <= x + rad; i++)
			{
				for (int j = y - hei; j <= y + hei; j++)
					if (j > 0 && j < 253)
					{
						for (int k = z - rad; k <= z + rad; k++)
						{
							if (world.isBlockOpaqueCube(i, j - 1, k)
								&& !world.isBlockOpaqueCube(i, j, k) && !world.isBlockOpaqueCube(i, j + 1, k)
								&& world.getBlockId(i, j, k) == 0)
							{
								int acura = 4;
								int lv = world.getSavedLightValue(EnumSkyBlock.Block, i, j, k) + acura; // 4 = moonlight
								if (lv <= 7)
								{
									float lvs = (1 - (7f - lv) / (7f - acura)) * 0.4f;
									
									trace(dx, dy, dz, i + lvs, j, k + lvs, i + 1 - lvs, j, k + 1 - lvs);
									trace(dx, dy, dz, i + 1 - lvs, j, k + lvs, i + lvs, j, k + 1 - lvs);
								}
								
							}
							
						}
					}
			}*/
			finishTrace();
		}
		
		private void beginTrace()
		{
			RenderHelper.disableStandardItemLighting();
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		}
		
		private void finishTrace()
		{
			GL11.glDisable(GL11.GL_BLEND);
			
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			
			RenderHelper.enableStandardItemLighting();
		}
		
		private void trace(
			double dx, double dy, double dz, double xa, double ya, double za, double xb, double yb, double zb)
		{
			GL11.glLineWidth(2f);
			
			GL11.glColor3f(1f, 0f, 0f);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawing(GL11.GL_LINE_STRIP);
			
			tessellator.setTranslation(-dx, -dy, -dz);
			tessellator.addVertex(xa, ya, za);
			tessellator.addVertex(xb, yb, zb);
			
			tessellator.draw();
			tessellator.setTranslation(0, 0, 0);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public Class getRenderEntityClass()
		{
			return MyRenderEntity.class;
		}
		
		@Override
		public Entity newRenderEntity()
		{
			return new MyRenderEntity();
		}
		
		private class MyRenderEntity extends HRenderEntity
		{
		}
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			Field f = Minecraft.class.getDeclaredField("minecraftDir");
			Field.setAccessible(new Field[] { f }, true);
			f.set(null, new File("."));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		String[] altArgs = new String[2];
		altArgs[0] = "";
		altArgs[1] = "";
		boolean useAltArgs = false;
		
		try
		{
			File f = new File("E:\\Dropbox\\Minecraft\\user\\mainline\\.minecraft\\mcsession.txt");
			
			if (f.exists())
			{
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String[] split = reader.readLine().split(" ");
				reader.close();
				if (split.length >= 2)
				{
					altArgs[0] = split[0];
					altArgs[1] = split[1];
					useAltArgs = true;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if (!useAltArgs)
		{
			Minecraft.main(args);
		}
		else
		{
			Minecraft.main(altArgs);
		}
		
	}
	
}
