package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import eu.ha3.mc.haddon.SupportsChatEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;

public class LBVisHaddon extends HaddonImpl implements SupportsTickEvents,
SupportsChatEvents
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
	
	private LBVisRenderEntity renderEntity;
	
	public LBVisHaddon()
	{
		this.step = LBVisStep.WAITING_FOR_START;
		this.reports = new ArrayList<LBReport>();
		
		makePatterns();
		
	}
	
	@Override
	public void onLoad()
	{
		manager().hookTickEvents(true);
		manager().hookChatEvents(true);
		manager().addRenderable(LBVisRenderEntity.class,
				new LBVisRenderHooks(this));
		
	}
	
	private void makePatterns()
	{
		patternStart = Pattern.compile("^(?:§.)?Block changes?");
		patternChange = Pattern.compile("^(?:§.)?(\\d+) changes? found\\.");
		patternPage = Pattern.compile("^(?:§.)?Page (\\d+)/(\\d+)");
		patternLogLine = Pattern
				.compile("^(?:§.)?\\((\\d+)\\) (\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) ([a-zA-Z0-9_]+) (.*?) at (-?[0-9]+):(-?[0-9]+):(-?[0-9]+)");
		
	}
	
	@Override
	public void onChat(String contents)
	{
		boolean win = false;
		
		switch (step)
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
			
		}
		
		if (!win)
		{
			boolean pass = tryMatchPageCount(contents);
			
			if (!pass)
			{
				tryMatchStart(contents);
				
			}
			
		}
		
		//System.out.println(step);
		
	}
	
	private boolean tryMatchLogLine(String contents)
	{
		if (lastReport == null)
			return false;
		
		System.out.println(contents);
		
		Matcher match = patternLogLine.matcher(contents);
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
			
			lastReport.addChange(change);
			
			return true;
			
		}
		
		return false;
		
	}
	
	private boolean tryMatchPageCount(String contents)
	{
		if (lastReport == null)
			return false;
		
		Matcher match = patternPage.matcher(contents);
		if (match.find())
		{
			step = LBVisStep.WAITING_FOR_LOGLINE;
			lastReport.setPageData(Integer.parseInt(match.group(1)), Integer
					.parseInt(match.group(2)));
			
			return true;
			
		}
		
		return false;
		
	}
	
	private boolean tryMatchChangeCount(String contents)
	{
		if (lastReport == null)
			return false;
		
		Matcher match = patternChange.matcher(contents);
		if (match.find())
		{
			step = LBVisStep.WAITING_FOR_PAGECOUNT;
			lastReport.setChangedTotal(Integer.parseInt(match.group(1)));
			
			return true;
			
		}
		
		return false;
		
	}
	
	private boolean tryMatchStart(String contents)
	{
		Matcher match = patternStart.matcher(contents);
		if (match.find())
		{
			step = LBVisStep.WAITING_FOR_CHANGECOUNT;
			
			if (lastReport != null && !lastReport.isValid())
				reports.remove(lastReport);
			
			lastReport = new LBReport();
			reports.add(lastReport);
			
			return true;
			
		}
		
		return false;
		
	}
	
	@Override
	public void onTick()
	{
		Minecraft mc = manager().getMinecraft();
		
		if (mc.theWorld != lastWorld || mc.thePlayer != lastPlayer)
		{
			renderEntity = new LBVisRenderEntity(this, mc.theWorld);
			renderEntity.setPosition(mc.thePlayer.posX, mc.thePlayer.posY,
					mc.thePlayer.posZ);
			mc.theWorld.spawnEntityInWorld(renderEntity);
			renderEntity.setPosition(mc.thePlayer.posX, mc.thePlayer.posY,
					mc.thePlayer.posZ);
			
			lastWorld = mc.theWorld;
			lastPlayer = mc.thePlayer;
			
			System.out.println("f");
			changedLocation();
			
		}
		
		/*if (manager().getMinecraft().thePlayer != null)
			if (manager().getMinecraft().thePlayer.posX != lastPosX)
			{
				lastPosX = manager().getMinecraft().thePlayer.posX;
				manager().getMinecraft().thePlayer.sendChatMessage("/lb next");
				
			}*/
		
	}
	
	private void changedLocation()
	{
		reports.clear();
		lastReport = null;
		
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
	
	public void render(double dx, double dy, double dz, float f, float f1)
	{
		double ax = 0, ay = 0, az = 0;
		int count = 0;
		
		for (LBReport report : reports)
			//if (report.isValid())
			for (LBChange change : report.getStoredChanges())
			{
				count++;
				
				ax = ax + change.getX();
				ay = ay + change.getY();
				az = az + change.getZ();
				
				qdb(change.getX(), change.getY(), change.getZ(), f);
				
			}
		
		ax = (float) ax / count;
		ay = (float) ay / count;
		az = (float) az / count;
		
		trace(ax, ay, az, f1);
		
	}
	
	private void trace(double ax, double ay, double az, float delta)
	{
		EntityPlayer ply = manager().getMinecraft().thePlayer;
		
		double px = ply.posX + (ply.posX - ply.lastTickPosX) * delta;
		double py = ply.posY + (ply.posY - ply.lastTickPosY) * delta;
		double pz = ply.posZ + (ply.posZ - ply.lastTickPosZ) * delta;
		
		RenderHelper.disableStandardItemLighting();
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawing(GL11.GL_LINE_LOOP);
		GL11.glColor4f(1f, 0f, 1f, 1f);
		GL11.glLineWidth(4f);
		GL11.glDepthFunc(GL11.GL_NOTEQUAL);
		
		tessellator.setTranslation(-px, -py, -pz);
		tessellator.addVertex(ax, ay, az);
		tessellator.addVertex(px, py, pz);
		
		tessellator.draw();
		tessellator.setTranslation(0, 0, 0);
		
		RenderHelper.enableStandardItemLighting();
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
	}
	
}
