package net.minecraft.src;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3KeyActions;
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

public class QuickThirdPersonHaddon extends HaddonImpl
	implements SupportsFrameEvents, SupportsKeyEvents, SupportsTickEvents, Ha3KeyActions
{
	private float directivePitch;
	private float directiveYaw;
	
	private float desiredPitch;
	private float desiredYaw;
	
	private float modifiedDesiredPitch;
	private float modifiedDesiredYaw;
	
	private boolean wasEnabled;
	private KeyBinding bind;
	
	private Ha3KeyManager keyManager;
	private boolean viewAsDirection;
	private boolean lockPlayerDirection;
	
	private boolean activate;
	private int previousTPmode;
	
	@Override
	public void onLoad()
	{
		this.keyManager = new Ha3KeyManager();
		this.lockPlayerDirection = true;
		this.viewAsDirection = false;
		
		this.previousTPmode = 0;
		
		this.bind = new KeyBinding("key.quickthirdperson", 47);
		manager().addKeyBinding(this.bind, "QTP Forward");
		manager().hookFrameEvents(true);
		manager().hookTickEvents(true);
		
		this.keyManager.addKeyBinding(this.bind, this);
	}
	
	@Override
	public void onFrame(float semi)
	{
		Minecraft mc = manager().getMinecraft();
		
		int tpMode = mc.gameSettings.thirdPersonView;
		boolean shouldEnable = this.activate && tpMode > 0 && tpMode == this.previousTPmode;
		this.previousTPmode = mc.gameSettings.thirdPersonView;
		
		manager().getMinecraft().gameSettings.debugCamEnable = shouldEnable;
		
		if (!shouldEnable)
		{
			this.wasEnabled = false;
			this.activate = false;
			return;
			
		}
		
		if (tpMode == 1)
		{
			thirdPersonAlgorithmA();
		}
		else if (tpMode == 2)
		{
			thirdPersonAlgorithmB();
		}
		
	}
	
	private void thirdPersonAlgorithmA()
	{
		
		if (!this.wasEnabled)
		{
			this.wasEnabled = true;
			
			copyDirection();
			
			resetDesiredAngles(this.directivePitch, this.directiveYaw);
			
		}
		
		if (this.lockPlayerDirection)
			if (util().isCurrentScreen(null))
			{
				gatherDesiredAngles();
			}
		
		if (this.viewAsDirection)
		{
			copyViewToDirection();
			this.viewAsDirection = false;
			
		}
		
		if (this.lockPlayerDirection)
		{
			applyDirection();
		}
		else
		{
			copyDirection();
		}
		
		float viewOffsetsYaw = 180f;
		
		try
		{
			// debugCamYaw;
			// prevDebugCamYaw;
			// debugCamPitch;
			// prevDebugCamPitch;
			
			util().setPrivateValueLiteral(
				EntityRenderer.class, manager().getMinecraft().entityRenderer, "t", 15,
				this.desiredYaw + viewOffsetsYaw);
			util().setPrivateValueLiteral(
				EntityRenderer.class, manager().getMinecraft().entityRenderer, "u", 16,
				this.desiredYaw + viewOffsetsYaw);
			util().setPrivateValueLiteral(
				EntityRenderer.class, manager().getMinecraft().entityRenderer, "v", 17, this.desiredPitch);
			util().setPrivateValueLiteral(
				EntityRenderer.class, manager().getMinecraft().entityRenderer, "w", 18, this.desiredPitch);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void thirdPersonAlgorithmB()
	{
		
		if (!this.wasEnabled)
		{
			this.wasEnabled = true;
			
			copyDirection();
			
			resetDesiredAngles(this.directivePitch, this.directiveYaw);
			
		}
		
		if (util().isCurrentScreen(null))
		{
			gatherDesiredAngles();
		}
		
		if (this.viewAsDirection)
		{
			copyViewToDirection();
			applyDirection();
			this.viewAsDirection = false;
			
		}
		
		if (this.lockPlayerDirection)
		{
			similarizeDirection();
			applyDirection();
		}
		else
		{
			applyDirection();
		}
		
		float viewOffsetsYaw = 180f;
		
		try
		{
			// debugCamYaw;
			// prevDebugCamYaw;
			// debugCamPitch;
			// prevDebugCamPitch;
			
			util().setPrivateValueLiteral(
				EntityRenderer.class, manager().getMinecraft().entityRenderer, "t", 15,
				this.desiredYaw + viewOffsetsYaw);
			util().setPrivateValueLiteral(
				EntityRenderer.class, manager().getMinecraft().entityRenderer, "u", 16,
				this.desiredYaw + viewOffsetsYaw);
			util().setPrivateValueLiteral(
				EntityRenderer.class, manager().getMinecraft().entityRenderer, "v", 17, this.desiredPitch);
			util().setPrivateValueLiteral(
				EntityRenderer.class, manager().getMinecraft().entityRenderer, "w", 18, this.desiredPitch);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void copyViewToDirection()
	{
		this.directivePitch = this.desiredPitch;
		this.directiveYaw = this.desiredYaw;
		normalizeDirectivePitch();
		
	}
	
	private void applyDirection()
	{
		if (!this.wasEnabled)
			return;
		
		EntityLiving ply = manager().getMinecraft().thePlayer;
		
		ply.rotationPitch = this.directivePitch;
		ply.rotationYaw = this.directiveYaw;
		
	}
	
	private void copyDirection()
	{
		if (!this.wasEnabled)
			return;
		
		EntityLiving ply = manager().getMinecraft().thePlayer;
		
		this.directivePitch = ply.rotationPitch;
		this.directiveYaw = ply.rotationYaw;
		normalizeDirectivePitch();
		
	}
	
	private void similarizeDirection()
	{
		if (!this.wasEnabled)
			return;
		
		this.directivePitch -= this.modifiedDesiredPitch;
		this.directiveYaw += this.modifiedDesiredYaw;
		normalizeDirectivePitch();
		
	}
	
	private void normalizeDirectivePitch()
	{
		if (this.directivePitch < -90F)
		{
			this.directivePitch = -90F;
		}
		
		if (this.directivePitch > 90F)
		{
			this.directivePitch = 90F;
		}
		
	}
	
	private void gatherDesiredAngles()
	{
		Minecraft mc = manager().getMinecraft();
		
		float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		float f1 = f * f * f * 8F;
		float f2 = mc.mouseHelper.deltaX * f1;
		float f3 = mc.mouseHelper.deltaY * f1;
		int l = 1;
		
		if (mc.gameSettings.invertMouse)
		{
			l = -1;
		}
		
		setDesiredAngles(f2, f3 * l);
		
	}
	
	private void resetDesiredAngles(float desiredAnglesPitch, float desiredAnglesYaw)
	{
		this.desiredPitch = desiredAnglesPitch;
		this.desiredYaw = desiredAnglesYaw;
	}
	
	private void setDesiredAngles(float par1, float par2)
	{
		this.modifiedDesiredPitch = par2 * 0.15f;
		this.modifiedDesiredYaw = par1 * 0.15f;
		this.desiredPitch -= this.modifiedDesiredPitch;
		this.desiredYaw += this.modifiedDesiredYaw;
		
		if (this.desiredPitch < -90F)
		{
			this.desiredPitch = -90F;
		}
		
		if (this.desiredPitch > 90F)
		{
			this.desiredPitch = 90F;
		}
		
	}
	
	@Override
	public void onKey(KeyBinding event)
	{
		if (event != this.bind)
			return;
		
		this.keyManager.handleKeyDown(event);
		
	}
	
	@Override
	public void onTick()
	{
		this.keyManager.handleRuntime();
		
	}
	
	@Override
	public void doBefore()
	{
		this.activate = true;
		
	}
	
	@Override
	public void doDuring(int curTime)
	{
		if (curTime >= 5)
		{
			this.lockPlayerDirection = false;
		}
		
	}
	
	@Override
	public void doAfter(int curTime)
	{
		if (curTime < 5)
		{
			this.viewAsDirection = true;
			
		}
		else
		{
			this.lockPlayerDirection = true;
			
		}
		
	}
	
}
