package net.minecraft.src;

import java.nio.charset.Charset;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import eu.ha3.mc.haddon.SupportsConnectEvents;
import eu.ha3.mc.haddon.SupportsIncomingMessages;

/**
 * WorldEdit JUST THE FUCKING RECTANGLE<br>
 * A light version of WorldEdit with just the fucking rectangle
 * 
 * @author Hurry
 * 
 */
public class WEJTFR extends HaddonImpl implements SupportsIncomingMessages, SupportsConnectEvents
{
	private static final int protocolVersion = 2;
	private RenderWE renderRelay;
	
	@Override
	public void onLoad()
	{
		this.renderRelay = new RenderWE(manager().getMinecraft());
		
		manager().enlistIncomingMessages("WECUI");
		manager().enlistOutgoingMessages("WECUI"); // This does almost nothing
		manager().addRenderable(this.renderRelay.getRenderEntityClass(), this.renderRelay.getRenderHook());
	}
	
	@Override
	public void onIncomingMessage(Packet250CustomPayload message)
	{
		String properMessage = new String(message.data, Charset.forName("UTF-8"));
		//this.controller.getEventManager().callEvent(channelevent);
		System.out.println("WEJTFR " + properMessage);
	}
	
	@Override
	public void onConnectEvent(NetClientHandler handler)
	{
		byte[] buffer = ("v|" + WEJTFR.protocolVersion).getBytes(Charset.forName("UTF-8"));
		manager().sendOutgoingMessage(new Packet250CustomPayload("WECUI", buffer));
		System.out.println("WEJTFR Sent init...");
	}
	
	class RenderWE extends Ha3RenderRelay
	{
		public RenderWE(Minecraft mc)
		{
			super(mc);
		}
		
		@Override
		public void doRender(Entity entity, double dx, double dy, double dz, float f, float semi)
		{
			/*
			beginTrace();
			
			double ax = 0, ay = 0, az = 0;
			int count = 0;
			
			for (LBReport report : LBVisHaddon.this.reports)
			{
				//if (report.isValid())
				for (LBChange change : report.getStoredChanges())
				{
					count++;
					
					ax = ax + change.getX();
					ay = ay + change.getY();
					az = az + change.getZ();
					
					if (change.getAction().startsWith("destroyed"))
					{
						traceColor(255, 0, 0);
					}
					else if (change.getAction().startsWith("created"))
					{
						traceColor(0, 0, 255);
					}
					else if (change.getAction().startsWith("replaced"))
					{
						traceColor(255, 255, 0);
					}
					
					int x = change.getX();
					int y = change.getY();
					int z = change.getZ();
					
					GL11.glLineWidth(1.5f);
					trace(dx, dy, dz, x, y, z, x + 1, y, z);
					trace(dx, dy, dz, x + 1, y, z + 1, x + 1, y, z);
					trace(dx, dy, dz, x + 1, y, z + 1, x, y, z + 1);
					trace(dx, dy, dz, x, y, z + 1, x, y, z);
					
				}
			}
			
			ax = (float) ax / count;
			ay = (float) ay / count;
			az = (float) az / count;
			
			finishTrace();
			*/
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
		
		private void traceColor(int r, int g, int b)
		{
			GL11.glColor3f(r / 255f, g / 255f, b / 255f);
		}
		
		private void traceColor(int color)
		{
			GL11.glColor3b((byte) (color >> 16 & 0xff), (byte) (color >> 8 & 0xff), (byte) (color & 0xff));
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
			//GL11.glColor3f(1f, 0f, 0f);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawing(GL11.GL_LINE_STRIP);
			
			tessellator.setTranslation(-dx, -dy, -dz);
			tessellator.addVertex(xa, ya, za);
			tessellator.addVertex(xb, yb, zb);
			
			tessellator.draw();
			tessellator.setTranslation(0, 0, 0);
		}
		
		@Override
		public Class<?> getRenderEntityClass()
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
	
}
