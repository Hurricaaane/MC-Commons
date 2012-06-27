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

public class QuickThirdPerson extends HaddonImpl implements
SupportsFrameEvents, SupportsKeyEvents, SupportsTickEvents,
Ha3KeyActions
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
	public void onInitialize()
	{
		keyManager = new Ha3KeyManager();
		lockPlayerDirection = true;
		viewAsDirection = false;
		
		previousTPmode = 0;
		
	}
	
	@Override
	public void onLoad()
	{
		bind = new KeyBinding("key.quickthirdperson", 47);
		manager().addKeyBinding(bind, "QTP Forward");
		manager().hookFrameEvents(true);
		manager().hookTickEvents(true);
		
		keyManager.addKeyBinding(bind, this);
	}
	
	@Override
	public void onFrame(float semi)
	{
		Minecraft mc = manager().getMinecraft();
		
		int tpMode = mc.gameSettings.thirdPersonView;
		boolean shouldEnable = activate && (tpMode > 0)
				&& (tpMode == previousTPmode);
		previousTPmode = mc.gameSettings.thirdPersonView;
		
		manager().getMinecraft().gameSettings.debugCamEnable = shouldEnable;
		
		if (!shouldEnable)
		{
			wasEnabled = false;
			activate = false;
			return;
			
		}
		
		if (tpMode == 1)
			thirdPersonAlgorithmA();
		else if (tpMode == 2)
			thirdPersonAlgorithmB();
		
	}
	
	private void thirdPersonAlgorithmA()
	{
		
		if (!wasEnabled)
		{
			wasEnabled = true;
			
			copyDirection();
			
			resetDesiredAngles(directivePitch, directiveYaw);
			
		}
		
		if (lockPlayerDirection)
			if (util().isCurrentScreen(null))
				gatherDesiredAngles();
		
		if (viewAsDirection)
		{
			copyViewToDirection();
			viewAsDirection = false;
			
		}
		
		if (lockPlayerDirection)
			applyDirection();
		else
			copyDirection();
		
		float viewOffsetsYaw = 180f;
		
		try
		{
			// debugCamYaw;
			// prevDebugCamYaw;
			// debugCamPitch;
			// prevDebugCamPitch;
			
			util().setPrivateValue(EntityRenderer.class,
					manager().getMinecraft().entityRenderer, "t", 15,
					desiredYaw + viewOffsetsYaw);
			util().setPrivateValue(EntityRenderer.class,
					manager().getMinecraft().entityRenderer, "u", 16,
					desiredYaw + viewOffsetsYaw);
			util().setPrivateValue(EntityRenderer.class,
					manager().getMinecraft().entityRenderer, "v", 17,
					desiredPitch);
			util().setPrivateValue(EntityRenderer.class,
					manager().getMinecraft().entityRenderer, "w", 18,
					desiredPitch);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void thirdPersonAlgorithmB()
	{
		
		if (!wasEnabled)
		{
			wasEnabled = true;
			
			copyDirection();
			
			resetDesiredAngles(directivePitch, directiveYaw);
			
		}
		
		if (util().isCurrentScreen(null))
			gatherDesiredAngles();
		
		if (viewAsDirection)
		{
			copyViewToDirection();
			applyDirection();
			viewAsDirection = false;
			
		}
		
		if (lockPlayerDirection)
		{
			similarizeDirection();
			applyDirection();
		}
		else
			applyDirection();
		
		float viewOffsetsYaw = 180f;
		
		try
		{
			// debugCamYaw;
			// prevDebugCamYaw;
			// debugCamPitch;
			// prevDebugCamPitch;
			
			util().setPrivateValue(EntityRenderer.class,
					manager().getMinecraft().entityRenderer, "t", 15,
					desiredYaw + viewOffsetsYaw);
			util().setPrivateValue(EntityRenderer.class,
					manager().getMinecraft().entityRenderer, "u", 16,
					desiredYaw + viewOffsetsYaw);
			util().setPrivateValue(EntityRenderer.class,
					manager().getMinecraft().entityRenderer, "v", 17,
					desiredPitch);
			util().setPrivateValue(EntityRenderer.class,
					manager().getMinecraft().entityRenderer, "w", 18,
					desiredPitch);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void copyViewToDirection()
	{
		directivePitch = desiredPitch;
		directiveYaw = desiredYaw;
		normalizeDirectivePitch();
		
	}
	
	private void applyDirection()
	{
		if (!wasEnabled)
			return;
		
		EntityLiving ply = manager().getMinecraft().thePlayer;
		
		ply.rotationPitch = directivePitch;
		ply.rotationYaw = directiveYaw;
		
	}
	
	private void copyDirection()
	{
		if (!wasEnabled)
			return;
		
		EntityLiving ply = manager().getMinecraft().thePlayer;
		
		directivePitch = ply.rotationPitch;
		directiveYaw = ply.rotationYaw;
		normalizeDirectivePitch();
		
	}
	
	private void similarizeDirection()
	{
		if (!wasEnabled)
			return;
		
		directivePitch -= modifiedDesiredPitch;
		directiveYaw += modifiedDesiredYaw;
		normalizeDirectivePitch();
		
	}
	
	private void normalizeDirectivePitch()
	{
		if (directivePitch < -90F)
		{
			directivePitch = -90F;
		}
		
		if (directivePitch > 90F)
		{
			directivePitch = 90F;
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
	
	private void resetDesiredAngles(float desiredAnglesPitch,
			float desiredAnglesYaw)
	{
		this.desiredPitch = desiredAnglesPitch;
		this.desiredYaw = desiredAnglesYaw;
	}
	
	private void setDesiredAngles(float par1, float par2)
	{
		//float f = desiredPitch;
		//float f1 = desiredYaw;
		modifiedDesiredPitch = par2 * 0.15f;
		modifiedDesiredYaw = par1 * 0.15f;
		desiredPitch -= modifiedDesiredPitch;
		desiredYaw += modifiedDesiredYaw;
		
		if (desiredPitch < -90F)
		{
			desiredPitch = -90F;
		}
		
		if (desiredPitch > 90F)
		{
			desiredPitch = 90F;
		}
		
		//prevRotationPitch += desiredAnglesPitch - f;
		//prevRotationYaw += desiredAnglesYaw - f1;
		
	}
	
	@Override
	public void onKey(KeyBinding event)
	{
		if (event != bind)
			return;
		
		if (wasEnabled)
		{
			//directivePitch = desiredPitch;
			//directiveYaw = desiredYaw;
			//System.out.println(event.pressed + " " + event.isPressed());
			
		}
		
		keyManager.handleKeyDown(event);
		
	}
	
	@Override
	public void onTick()
	{
		keyManager.handleRuntime();
		
	}
	
	@Override
	public void doBefore()
	{
		activate = true;
		
	}
	
	@Override
	public void doDuring(int curTime)
	{
		if (curTime >= 5)
			lockPlayerDirection = false;
		
	}
	
	@Override
	public void doAfter(int curTime)
	{
		if (curTime < 5)
		{
			viewAsDirection = true;
			
		}
		else
		{
			lockPlayerDirection = true;
			
		}
		
	}
	
}
