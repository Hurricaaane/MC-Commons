package net.minecraft.src;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3Scroller;

/*
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
 */

public class MAtScroller extends Ha3Scroller
{
	final String msgvol = "MAtmos Volume";
	final String msgmus = "MAtmos Music";
	final String msgup = "+";
	final String msgdown = "-";
	
	private String msgd = msgvol;
	
	private MAtMod mod;
	private float prevPitch;
	
	private boolean knowsHowToUse;
	private float doneValue;
	
	public MAtScroller(MAtUserControl userControlIn, MAtMod modIn)
	{
		super(modIn.manager());
		mod = modIn;
		
		knowsHowToUse = false;
		
	}
	
	@Override
	protected void doDraw(float fspan)
	{
		String msgper;
		
		Minecraft mc = manager().getMinecraft();
		
		msgper = (int) Math.floor(doneValue * 100) + "%";
		
		ScaledResolution screenRes = new ScaledResolution(mc.gameSettings,
				mc.displayWidth, mc.displayHeight);
		
		int scrWidth = screenRes.getScaledWidth();
		int scrHeight = screenRes.getScaledHeight();
		
		int uwidth = width("_");
		int uposx = (scrWidth - uwidth) / 2 + width(msgd) / 2;
		
		mc.fontRenderer.drawStringWithShadow(msgd, uposx + uwidth * 2,
				scrHeight / 2,
				0xffffff);
		
		mc.fontRenderer.drawStringWithShadow(msgper, uposx + uwidth * 2,
				scrHeight / 2 + 10, 255 << 16
				| ((int) (200 + 55 * (doneValue < 1 ? 1
						: (4 - doneValue) / 3F))) << 8);
		
		if (!knowsHowToUse)
		{
			float glocount = mod.corn().util().getClientTick() + fspan;
			int blink = (int) (200 + 55 * (Math.sin(glocount * Math.PI * 0.07) + 1) / 2F);
			mc.fontRenderer.drawStringWithShadow("<Look up/down>", uposx
					+ uwidth * 2, scrHeight / 2 + 10 * 2, blink << 16
					| blink << 8 | blink);
			
			if (Math.abs(getInitialPitch() - getPitch()) > 60)
			{
				knowsHowToUse = true;
				
			}
			
		}
		
		mc.fontRenderer.drawStringWithShadow(msgup, uposx + uwidth * 2,
				scrHeight / 2 - scrHeight / 6 + 3, 0xffff00);
		
		mc.fontRenderer.drawStringWithShadow(msgdown, uposx + uwidth * 2,
				scrHeight / 2 + scrHeight / 6 + 3,
				0xffff00);
		
		final int ucount = 8;
		final float speedytude = 20;
		for (int i = 0; i < ucount; i++)
		{
			float perx = (((getPitch() + 90F) % speedytude) / speedytude + i)
					/ (ucount);
			double pirx = Math.cos(Math.PI * perx);
			
			mc.fontRenderer.drawStringWithShadow("_", uposx, scrHeight / 2
					+ (int) Math.floor(pirx * scrHeight / 6), 0xffff00);
			
		}
		
	}
	
	private int width(String s)
	{
		return mod.manager().getMinecraft().fontRenderer.getStringWidth(s);
		
	}
	
	public float getValue()
	{
		return doneValue;
		
	}
	
	@Override
	protected void doRoutineBefore()
	{
		final int caps = 10;
		if (Math.floor((prevPitch + 90F) / caps) != Math
				.floor((getPitch() + 90F) / caps))
		{
			float hgn = (float) Math.pow((-getPitch() + 90F) / 90F, 2);
			float res = (float) Math
					.pow(2, -Math.floor(getPitch() / caps) / 12);
			
			EntityPlayer ply = mod.manager().getMinecraft().thePlayer;
			float posX = (float) ply.posX;
			float posY = (float) ply.posY;
			float posZ = (float) ply.posZ;
			
			mod.corn().sound().playSoundViaManager("random.click", posX, posY,
					posZ, hgn, res);
			
		}
		
		doneValue = -getPitch() / 90F + 1F;
		doneValue = (float) Math.pow(doneValue, 2);
		if (Math.abs(getPitch()) < 3)
		{
			doneValue = 1F;
			
		}
		if (Math.abs(doneValue - 0.2F) < 0.05F)
		{
			doneValue = 0.2F;
			
		}
		
		prevPitch = getPitch();
		
	}
	
	@Override
	protected void doRoutineAfter()
	{
		
	}
	
	@Override
	protected void doStart()
	{
		prevPitch = getPitch();
		
	}
	
	@Override
	protected void doStop()
	{
		
	}
	
	public void start(boolean scrollModeIsMusic)
	{
		if (!scrollModeIsMusic)
			msgd = msgvol;
		else
			msgd = msgmus;
		
		this.start();
		
	}
	
}
