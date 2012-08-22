package net.minecraft.src;

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

public class Ha3DebuggingHaddon extends HaddonImpl implements SupportsTickEvents, SupportsFrameEvents
{
	private EdgeTrigger button;
	private boolean toggle;
	
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
		manager().hookTickEvents(true);
		manager().hookFrameEvents(true);
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
		
		showGPS(1523, 736, 0x00FF00);
		showGPS(602, 275, 0xFFFF00);
		showGPS(-102, 187, 0xFF0000);
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
}
