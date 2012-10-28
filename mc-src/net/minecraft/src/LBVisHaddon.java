package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.haddon.SupportsChatEvents;
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

public class LBVisHaddon extends HaddonImpl implements SupportsTickEvents, SupportsChatEvents
{
	private LBVisStep step;
	
	private Pattern patternStart;
	private Pattern patternChange;
	private Pattern patternPage;
	private Pattern patternLogLine;
	
	private List<LBReport> reports;
	private LBReport lastReport;
	
	private World lastWorld;
	private EntityPlayer lastPlayer;
	
	private RenderLB renderRelay;
	
	private EdgeTrigger repeater;
	
	private ArrayList<Runnable> queue;
	
	public LBVisHaddon()
	{
		this.step = LBVisStep.WAITING_FOR_START;
		this.reports = new ArrayList<LBReport>();
		this.queue = new ArrayList<Runnable>();
		
		this.repeater = new EdgeTrigger(new EdgeModel() {
			
			@Override
			public void onTrueEdge()
			{
				manager().getMinecraft().thePlayer.sendChatMessage("/lb next");
			}
			
			@Override
			public void onFalseEdge()
			{
				
			}
		});
		
		makePatterns();
		
	}
	
	@Override
	public void onLoad()
	{
		this.renderRelay = new RenderLB(manager().getMinecraft());
		
		manager().hookTickEvents(true);
		manager().hookChatEvents(true);
		
		manager().addRenderable(this.renderRelay.getRenderEntityClass(), this.renderRelay.getRenderHook());
		
	}
	
	private void makePatterns()
	{
		this.patternStart = Pattern.compile("^(?:\u00A7.)?Block changes?");
		this.patternChange = Pattern.compile("^(?:\u00A7.)?(\\d+) changes? found\\.");
		this.patternPage = Pattern.compile("^(?:\u00A7.)?Page (\\d+)/(\\d+)");
		this.patternLogLine =
			Pattern
				.compile("^(?:\u00A7.)?\\((\\d+)\\) (\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) ([a-zA-Z0-9_]+) (.*?) at (-?[0-9]+):(-?[0-9]+):(-?[0-9]+)");
		
	}
	
	@Override
	public void onChat(final String contents)
	{
		this.queue.add(new Runnable() {
			@Override
			public void run()
			{
				
				boolean win = false;
				
				switch (LBVisHaddon.this.step)
				{
				case WAITING_FOR_CHANGECOUNT:
					win = tryMatchChangeCount(contents);
					break;
				case WAITING_FOR_PAGECOUNT:
					win = tryMatchPageCount(contents);
					break;
				case WAITING_FOR_LOGLINE:
					win = tryMatchLogLine(contents);
					break;
				default:
					break;
				
				}
				
				if (!win)
				{
					boolean pass = tryMatchPageCount(contents);
					
					if (!pass)
					{
						tryMatchStart(contents);
						
					}
					
				}
			}
			
		});
		
		//System.out.println(step);
		
	}
	
	private boolean tryMatchLogLine(String contents)
	{
		if (this.lastReport == null)
			return false;
		
		System.out.println(contents);
		
		Matcher match = this.patternLogLine.matcher(contents);
		if (match.find())
		{
			LBChange change = new LBChange();
			change.setInformativeRank(Integer.parseInt(match.group(1)));
			change.setRawDate(match.group(2));
			change.setAuthor(match.group(3));
			change.setAction(match.group(4));
			change.setX(Integer.parseInt(match.group(5)));
			change.setY(Integer.parseInt(match.group(6)));
			change.setZ(Integer.parseInt(match.group(7)));
			
			this.lastReport.addChange(change);
			
			return true;
			
		}
		
		return false;
		
	}
	
	private boolean tryMatchPageCount(String contents)
	{
		if (this.lastReport == null)
			return false;
		
		Matcher match = this.patternPage.matcher(contents);
		if (match.find())
		{
			this.step = LBVisStep.WAITING_FOR_LOGLINE;
			this.lastReport.setPageData(Integer.parseInt(match.group(1)), Integer.parseInt(match.group(2)));
			
			return true;
			
		}
		
		return false;
		
	}
	
	private boolean tryMatchChangeCount(String contents)
	{
		if (this.lastReport == null)
			return false;
		
		Matcher match = this.patternChange.matcher(contents);
		if (match.find())
		{
			this.step = LBVisStep.WAITING_FOR_PAGECOUNT;
			this.lastReport.setChangedTotal(Integer.parseInt(match.group(1)));
			
			return true;
			
		}
		
		return false;
		
	}
	
	private boolean tryMatchStart(String contents)
	{
		Matcher match = this.patternStart.matcher(contents);
		if (match.find())
		{
			this.step = LBVisStep.WAITING_FOR_CHANGECOUNT;
			
			if (this.lastReport != null && !this.lastReport.isValid())
			{
				this.reports.remove(this.lastReport);
			}
			
			this.lastReport = new LBReport();
			this.reports.add(this.lastReport);
			
			return true;
			
		}
		
		return false;
		
	}
	
	@Override
	public void onTick()
	{
		Minecraft mc = manager().getMinecraft();
		if (this.renderRelay.ensureExists())
		{
			System.out.println("Respawned Render Entity");
		}
		this.repeater.signalState(util().areKeysDown(29, 42, 38));
		
		while (!this.queue.isEmpty())
		{
			this.queue.remove(0).run();
			
		}
		
		if (mc.theWorld != this.lastWorld || mc.thePlayer != this.lastPlayer)
		{
			this.lastWorld = mc.theWorld;
			this.lastPlayer = mc.thePlayer;
			
			changedLocation();
			
		}
		
	}
	
	private void changedLocation()
	{
		this.reports.clear();
		this.lastReport = null;
		
	}
	
	public void qdb(int x, int y, int z, float delta)
	{
		//        y
		//        ^
		//        |
		//        e     f
		//     h     g
		//
		//        a     b   --> x
		//     d     c
		//  ./
		// z
		
		//float xx = x, yy = y, zz = z;
		
		double ax = x, ay = y, az = z;
		double bx = x + 1f, by = y, bz = z;
		double cx = x + 1f, cy = y, cz = z + 1;
		double dx = x, dy = y, dz = z + 1;
		
		double ex = x, ey = y + 1f, ez = z;
		double fx = x + 1f, fy = y + 1f, fz = z;
		double gx = x + 1f, gy = y + 1f, gz = z + 1;
		double hx = x, hy = y + 1f, hz = z + 1;
		
		// draw selection box
		
		EntityPlayer ply = manager().getMinecraft().thePlayer;
		
		//delta = 0;
		
		double px = ply.posX + (ply.posX - ply.lastTickPosX) * delta;
		double py = ply.posY + (ply.posY - ply.lastTickPosY) * delta;
		double pz = ply.posZ + (ply.posZ - ply.lastTickPosZ) * delta;
		
		RenderHelper.disableStandardItemLighting();
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawing(GL11.GL_LINE_LOOP);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glLineWidth(4f);
		GL11.glDepthFunc(GL11.GL_NOTEQUAL);
		
		tessellator.setTranslation(-px, -py, -pz);
		tessellator.addVertex(ax, ay, az);
		tessellator.addVertex(bx, by, bz);
		tessellator.addVertex(cx, cy, cz);
		tessellator.addVertex(dx, dy, dz);
		tessellator.addVertex(hx, hy, hz);
		tessellator.addVertex(gx, gy, gz);
		tessellator.addVertex(fx, fy, fz);
		tessellator.addVertex(ex, ey, ez);
		tessellator.addVertex(ax, ay, az);
		
		tessellator.draw();
		tessellator.setTranslation(0, 0, 0);
		
		RenderHelper.enableStandardItemLighting();
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
	}
	
	class RenderLB extends Ha3RenderRelay
	{
		public RenderLB(Minecraft mc)
		{
			super(mc);
		}
		
		@Override
		public void doRender(Entity entity, double dx, double dy, double dz, float f, float semi)
		{
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
