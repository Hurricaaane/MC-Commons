package net.minecraft.src;

import java.nio.charset.Charset;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import eu.ha3.mc.haddon.SupportsConnectEvents;
import eu.ha3.mc.haddon.SupportsIncomingMessages;
import eu.ha3.mc.haddon.SupportsTickEvents;

/**
 * WorldEdit JUST THE FUCKING RECTANGLE<br>
 * A light version of WorldEdit with just the fucking rectangle
 * 
 * @author Hurry
 * 
 */
public class WEJTFR extends HaddonImpl implements SupportsIncomingMessages, SupportsConnectEvents, SupportsTickEvents
{
	private static final int protocolVersion = 2;
	private RenderWE renderRelay;
	
	boolean visible;
	int xa;
	int ya;
	int za;
	int xb;
	int yb;
	int zb;
	
	@Override
	public void onLoad()
	{
		this.renderRelay = new RenderWE(manager().getMinecraft());
		
		manager().enlistIncomingMessages("WECUI");
		manager().enlistOutgoingMessages("WECUI"); // This does almost nothing
		manager().addRenderable(this.renderRelay.getRenderEntityClass(), this.renderRelay.getRenderHook());
		manager().hookTickEvents(true);
	}
	
	@Override
	public void onIncomingMessage(Packet250CustomPayload message)
	{
		String properMessage = new String(message.data, Charset.forName("UTF-8"));
		//this.controller.getEventManager().callEvent(channelevent);
		//System.out.println("WEJTFR " + properMessage);
		System.out.println(properMessage);
		try
		{
			String[] parts = properMessage.split("\\|");
			System.out.println(parts.length);
			if (parts[0].equals("p"))
			{
				if (parts[1].equals("0"))
				{
					this.xa = Integer.parseInt(parts[2]);
					this.ya = Integer.parseInt(parts[3]);
					this.za = Integer.parseInt(parts[4]);
					
					if (parts[5].equals("-1"))
					{
						this.xb = this.xa;
						this.yb = this.ya;
						this.zb = this.za;
					}
					this.visible = true;
				}
				else if (parts[1].equals("1"))
				{
					this.xb = Integer.parseInt(parts[2]);
					this.yb = Integer.parseInt(parts[3]);
					this.zb = Integer.parseInt(parts[4]);
					
					if (parts[5].equals("-1"))
					{
						this.xa = this.xb;
						this.ya = this.yb;
						this.za = this.zb;
					}
					this.visible = true;
				}
			}
			else if (parts[0].equals("s"))
			{
				this.visible = false;
			}
		}
		catch (Throwable e)
		{
			this.visible = false;
			e.printStackTrace();
		}
	}
	
	@Override
	public void onConnectEvent(NetClientHandler handler)
	{
		byte[] buffer = ("v|" + WEJTFR.protocolVersion).getBytes(Charset.forName("UTF-8"));
		manager().sendOutgoingMessage(new Packet250CustomPayload("WECUI", buffer));
		//System.out.println("WEJTFR Sent init...");
		
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
			if (!WEJTFR.this.visible)
				return;
			
			beginTrace();
			
			int x = Math.min(WEJTFR.this.xa, WEJTFR.this.xb);
			int y = Math.min(WEJTFR.this.ya, WEJTFR.this.yb);
			int z = Math.min(WEJTFR.this.za, WEJTFR.this.zb);
			int xxx = Math.max(WEJTFR.this.xa, WEJTFR.this.xb) + 1;
			int yyy = Math.max(WEJTFR.this.ya, WEJTFR.this.yb) + 1;
			int zzz = Math.max(WEJTFR.this.za, WEJTFR.this.zb) + 1;
			
			traceColor(255, 0, 0);
			GL11.glLineWidth(3f);
			traceACube(
				dx, dy, dz, WEJTFR.this.xa, WEJTFR.this.ya, WEJTFR.this.za, WEJTFR.this.xa + 1, WEJTFR.this.ya + 1,
				WEJTFR.this.za + 1);
			traceColor(0, 255, 0);
			traceACube(
				dx, dy, dz, WEJTFR.this.xb, WEJTFR.this.yb, WEJTFR.this.zb, WEJTFR.this.xb + 1, WEJTFR.this.yb + 1,
				WEJTFR.this.zb + 1);
			
			traceColor(255, 0, 0);
			GL11.glLineWidth(3f);
			traceACube(dx, dy, dz, x, y, z, xxx, yyy, zzz);
			
			/*traceColor(255, 255, 0);
			trace(dx, dy, dz, x, y, z, xxx, y, zzz);
			trace(dx, dy, dz, xxx, y, z, x, y, zzz);
			trace(dx, dy, dz, x, yyy, z, xxx, yyy, zzz);
			trace(dx, dy, dz, xxx, yyy, z, x, yyy, zzz);*/
			
			finishTrace();
			
		}
		
		private void traceACube(double dx, double dy, double dz, int x, int y, int z, int xxx, int yyy, int zzz)
		{
			trace(dx, dy, dz, xxx, y, z, x, y, z);
			trace(dx, dy, dz, x, y, zzz, x, y, z);
			trace(dx, dy, dz, xxx, y, z, xxx, y, zzz);
			trace(dx, dy, dz, x, y, zzz, xxx, y, zzz);
			
			trace(dx, dy, dz, xxx, y, z, xxx, yyy, z);
			trace(dx, dy, dz, x, y, zzz, x, yyy, zzz);
			trace(dx, dy, dz, xxx, y, zzz, xxx, yyy, zzz);
			trace(dx, dy, dz, x, y, z, x, yyy, z);
			
			trace(dx, dy, dz, xxx, yyy, z, x, yyy, z);
			trace(dx, dy, dz, x, yyy, zzz, x, yyy, z);
			trace(dx, dy, dz, xxx, yyy, z, xxx, yyy, zzz);
			trace(dx, dy, dz, x, yyy, zzz, xxx, yyy, zzz);
		}
		
		private void beginTrace()
		{
			RenderHelper.disableStandardItemLighting();
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_LIGHTING);
			
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
			GL11.glEnable(GL11.GL_LIGHTING);
			
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
	
	@Override
	public void onTick()
	{
		if (this.renderRelay.ensureExists())
		{
			System.out.println("Respawned Render Entity");
		}
		
	}
	
}
