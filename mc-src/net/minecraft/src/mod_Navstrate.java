package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import eu.ha3.mc.convenience.Ha3KeyManager;

public class mod_Navstrate extends BaseMod
{
	final String VERSION = "1.8.1 rc1";
	Minecraft mc;
	private Ha3KeyManager keyManager;
	private KeyBinding navKeyBinding;
	
	public NavstrateData readData;
	public NavstrateData writeData;
	
	NavstrateGatherer gatherer;
	int stepLimit;
	
	boolean isOn;
	private boolean needsRebuffering;
	private int bufferedImage;
	private int[] intArray;
	private int intColor;
	private int bufferSize;
	
	long worldTime;
	
	AxisAlignedBB bbox;
	
	@Override
	public String getVersion()
	{
		return VERSION;
		
	}
	
	mod_Navstrate()
	{
		isOn = false;
		needsRebuffering = true;
		bufferSize = 256;
		
		stepLimit = 92;
		
		mc = ModLoader.getMinecraftInstance();
		worldTime = 0;
		
		readData = new NavstrateData(128, 48, 128);
		writeData = new NavstrateData(128, 48, 128);
		gatherer = null;
		
		navKeyBinding = new KeyBinding("key.navstrate", 65);
		keyManager = new Ha3KeyManager();
		
		ModLoader.addLocalization("key.navstrate", "Navstrate");
		ModLoader.registerKey(this, navKeyBinding, true);
		keyManager.addKeyBinding(navKeyBinding, new NavstrateKey(this));
		
		
		ModLoader.setInGameHook(this, true, false);
		
		bbox = AxisAlignedBB.getBoundingBoxFromPool(0, 0, 0, 0, 0, 0);
		
	}
	
	public synchronized void performSnapshot()
	{
		// DEBUG
		//stepLimit = 92;
		
		if (gatherer != null)
			return;
		
		int x = (int) Math.floor(mc.thePlayer.posX);
		int y = (int) Math.floor(mc.thePlayer.posY);
		int z = (int) Math.floor(mc.thePlayer.posZ);
		
		writeData.shiftData(x - writeData.xPos, y - writeData.yPos, z - writeData.zPos);
		gatherer = new NavstrateGatherer();
		gatherer.setDaemon(true);
		gatherer.setCaller(this);
		gatherer.setSleepTime(8);
		gatherer.prepareAnalysis(writeData, x, y, z, stepLimit);
		gatherer.start();
		//System.out.println("start.");
	}
	
	public synchronized void finishSnapshot()
	{
		//System.out.println("done.");
		gatherer = null;
	}
	
	@Override
	public boolean onTickInGame(float f, Minecraft game)
	{
		if (worldTime != mc.theWorld.getWorldTime())
		{
			keyManager.handleRuntime();
			worldTime = mc.theWorld.getWorldTime();
			
		}
		
		if (!isOn)
			return true;
		
		if (!ModLoader.isGUIOpen(null) && !ModLoader.isGUIOpen(net.minecraft.src.GuiChat.class))
			return true;
		
		int screenXshift = 32;
		int screenYshift = 32;
		int width = 100;
		int tall = 80;
		
		if (needsRebuffering)
		{
			needsRebuffering = false;
			
			bufferedImage = mc.renderEngine.allocateAndSetupTexture(new BufferedImage(bufferSize, bufferSize, 2));
			intArray = new int[bufferSize * bufferSize];
			
		}
		
		for (int i = 0; i < bufferSize * bufferSize; i++)
			intArray[i] = 0;
		
		
		NavstrateData parData = writeData;
		int px = (int) Math.floor(mc.thePlayer.posX);
		int py = (int) Math.floor(mc.thePlayer.posY);
		int pz = (int) Math.floor(mc.thePlayer.posZ);
		
		{
			int itrans_prep = parData.xSize / 2 + px - parData.xPos - width / 2;
			int jtrans_prep = parData.ySize / 2 - parData.yPos;
			int ktrans_prep = parData.zSize / 2 + pz - parData.zPos - tall / 2;
			
			for (int i = 0; i < width; i++)
			{
				//int ioffs = i - width / 2;
				//int itrans = parData.xSize / 2 + px - parData.xPos + ioffs;
				int itrans = itrans_prep + i;
				
				for (int k = 0; k < tall; k++)
				{
					//int koffs = k - tall / 2;
					//int ktrans = parData.zSize / 2 + pz - parData.zPos + koffs;
					int ktrans = ktrans_prep + k;
					
					if (parData.isValidPos(itrans, 0, ktrans))
					{
						int contains = 0;
						int min = stepLimit;
						int wall = 0;
						int wallCur = stepLimit;
						for (int j = 0; j < parData.ySize; j++)
						{
							int step = parData.getData(itrans, j, ktrans);
							
							if (step > 0)
							{
								if (step < min)
									min = step;
								
								contains++;
								
							}
							else if (step < 0)
							{
								wall++;
								
								if (j == jtrans_prep + py)
									wallCur = -step;
								
							}
							
						}
						
						if (min != stepLimit)
						{
							float alphaWork = (float) (stepLimit - min) / stepLimit;
							if (alphaWork > 1)
								alphaWork = 1;
							else if (alphaWork < 0)
								alphaWork = 0;
							else
								alphaWork = 1 - (float) Math.pow(1 - alphaWork, 2);
							
							int alpha = (int) (alphaWork * 8);
							
							setColor(0, 255, 0, alpha);
							setPixel(i, k, width);
							
						}
						if (contains > 0)
						{
							float alphaWork = (float) (contains) / 32;
							if (alphaWork > 1)
								alphaWork = 1;
							else
								alphaWork = 1 - (1 - alphaWork) * (1 - alphaWork);
							
							int alpha = (int) (alphaWork * 32);
							
							setColor(0, 255, 0, alpha);
							addPixel(i, k, width);
							
						}
						if (wall > 0)
						{
							float alphaWork = (float) (wall) / 8;
							if (alphaWork > 1)
								alphaWork = 1;
							else
								alphaWork = alphaWork * alphaWork;
							
							int alpha = (int) (alphaWork * 164);
							
							setColor(0, 255, 128, alpha);
							addPixel(i, k, width);
							
						}
						if (wallCur != stepLimit)
						{
							float alphaWork = ((float) (stepLimit - wallCur) / stepLimit) * 0.7f + (wall / 6);
							if (alphaWork > 1)
								alphaWork = 1;
							else
								alphaWork = 1 - (1 - alphaWork) * (1 - alphaWork);
							
							int alpha = (int) (alphaWork * 128);
							
							setColor(0, 255, 255, alpha);
							setPixel(i, k, width);
							
						}
						
					}
					
				}
				
			}
			
		}
		
		/*setColor(255, 255, 0, 255);
		setPixel(width / 2, tall / 2, width);
		setPixel(width / 2 - 1, tall / 2, width);
		setPixel(width / 2 + 1, tall / 2, width);
		setPixel(width / 2, tall / 2 - 1, width);
		setPixel(width / 2, tall / 2 + 1, width);*/
		
		mc.renderEngine.createTextureFromBytes(intArray, bufferSize, bufferSize, bufferedImage);
		
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
		GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
		GL11.glBlendFunc(770, 771);
		//GL11.glShadeModel(7424 /*GL_FLAT*/);
		
		drawQuad(screenXshift, screenYshift, screenXshift + width, screenYshift + tall);
		
		GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
		GL11.glBindTexture(3553, bufferedImage);
		drawQuadUV(screenXshift, screenYshift, screenXshift + width, screenYshift + tall, width, tall);
		GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
		
		GL11.glShadeModel(7425 /*GL_SMOOTH*/);
		@SuppressWarnings("unchecked")
		List<Entity> entityList = mc.theWorld.getEntitiesWithinAABB(net.minecraft.src.EntityPlayer.class,
				bbox.setBounds(
						px - width / 2,
						py - parData.ySize, // TODO for Y coords, should theorically be ySize / 2
						pz - tall / 2,
						px + width / 2,
						py + parData.ySize,
						pz + tall / 2
						));
		
		int centerX = screenXshift + width / 2;
		int centerY = screenYshift + tall / 2;
		int radii = tall / 5;
		for (Iterator<Entity> iter = entityList.iterator(); iter.hasNext();)
		{
			//float plyYaw = mc.thePlayer.rotationYaw;
			EntityPlayer player = (EntityPlayer) iter.next();
			int xWorldShift = (int) (px - player.posX);
			int zWorldShift = (int) (pz - player.posZ);
			if (xWorldShift < width / 2 && zWorldShift < tall / 2)
			{
				int alpha = 192 - (int) (128 * (Math.abs(py - player.posY) / 8));
				if (alpha < 0)
					alpha = 0;
				float yaw = (float) (((-90 + player.rotationYaw) / 360F) * Math.PI * 2);
				float xA = (float) Math.cos(yaw + Math.PI / 4) * radii;
				float yA = (float) Math.sin(yaw + Math.PI / 4) * radii;
				float xB = (float) Math.cos(yaw - Math.PI / 4) * radii;
				float yB = (float) Math.sin(yaw - Math.PI / 4) * radii;
				
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawing(4);
				tessellator.setColorRGBA(0, 255, 255, alpha);
				tessellator.addVertex(centerX + xWorldShift, centerY + zWorldShift, 0.0D);
				tessellator.setColorRGBA(0, 255, 0, 0);
				tessellator.addVertex(centerX + xA + xWorldShift, centerY + yA + zWorldShift, 0.0D);
				tessellator.addVertex(centerX + xB + xWorldShift, centerY + yB + zWorldShift, 0.0D);
				tessellator.draw();
				
			}
			
		}
		GL11.glShadeModel(7424 /*GL_FLAT*/);
		GL11.glDisable(3042 /*GL_BLEND*/);
		GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
		GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
		
		return true;
		
	}
	
	@Override
	public void keyboardEvent(KeyBinding event)
	{
		keyManager.handleKeyDown(event);
		
	}
	protected void drawQuadUV(int i, int j, int k, int l, int width, int height)
	{
		if (i < k)
		{
			int j1 = i;
			i = k;
			k = j1;
		}
		if (j < l)
		{
			int k1 = j;
			j = l;
			l = k1;
		}
		
		double wbf = (double) width / bufferSize;
		double hbf = (double) height / bufferSize;
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(i, l, 0.0D, 0.0D, hbf);
		tessellator.addVertexWithUV(k, l, 0.0D, wbf, hbf);
		tessellator.addVertexWithUV(k, j, 0.0D, wbf, 0.0D);
		tessellator.addVertexWithUV(i, j, 0.0D, 0.0D, 0.0D);
		tessellator.draw();
		
	}
	
	protected void drawQuad(int i, int j, int k, int l)
	{
		if (i < k)
		{
			int j1 = i;
			i = k;
			k = j1;
		}
		if (j < l)
		{
			int k1 = j;
			j = l;
			l = k1;
		}
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA(0, 0, 0, 64);
		tessellator.addVertex(i, l, 0.0D);
		tessellator.addVertex(k, l, 0.0D);
		tessellator.addVertex(k, j, 0.0D);
		tessellator.addVertex(i, j, 0.0D);
		tessellator.draw();
		
	}
	
	public void setPixel(int i, int j, int width)
	{
		intArray[i + j * bufferSize] = intColor;
		
	}
	
	public void addPixel(int i, int j, int width)
	{
		int prev = intArray[i + j * bufferSize];
		float a = (intColor >> 24 & 0xFF) / 255f;
		
		int ua = ((prev >> 24 & 0xFF) + (intColor >> 24 & 0xFF));
		int ur = ((prev >> 16 & 0xFF) + (int) (a * (intColor >> 16 & 0xFF)));
		int ug = ((prev >> 8 & 0xFF) + (int) (a * (intColor >> 8 & 0xFF)));
		int ub = ((prev & 0xFF) + (int) (a * (intColor & 0xFF)));
		if (ua > 255)
			ua = 255;
		if (ur > 255)
			ur = 255;
		if (ug > 255)
			ug = 255;
		if (ub > 255)
			ub = 255;
		
		intArray[i + j * bufferSize] = ua << 24 | ur << 16 | ug << 8 | ub;
		
	}
	public void setColor(int r, int g, int b, int a)
	{
		intColor = 0x00000000 | a << 24 | r << 16 | g << 8 | b;
		//intColor = 0xFF00FF00;
		
	}
	
	/*public void setColor(int r, int g, int b, int a)
	{
		intColor = 0x00000000 | r << 24 | g << 16 | b << 8 | a;
		//GL11.glColor4f(r / 255F, g / 255F, b / 255F, a / 255F);
		
	}*/
	
	public void toggle()
	{
		isOn = !isOn;
		
	}
	
	public void rescan()
	{
		resetData();
		isOn = true;
		performSnapshot();
		
	}
	
	public boolean isOn()
	{
		return isOn;
	}
	
	public synchronized void resetData()
	{
		if (gatherer != null)
			gatherer.interrupt();
		gatherer = null;
		
		writeData.emptyMemory();
		
	}
	
	@Override
	public void load()
	{
		// TODO Auto-generated method stub
		
	}
	
}
