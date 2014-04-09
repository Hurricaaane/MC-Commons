package eu.ha3.mc.haddon.implem;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.src.Minecraft;
import net.minecraft.src.ScaledResolution;

import org.lwjgl.input.Keyboard;

import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.Utility;

/* x-placeholder-wtfplv2 */

public abstract class HaddonUtilityImpl implements Utility
{
	final private static int WORLD_HEIGHT = 256;
	
	private Map<String, PrivateEntry> getters = new HashMap<String, PrivateEntry>();
	private Map<String, PrivateEntry> setters = new HashMap<String, PrivateEntry>();
	
	protected long ticksRan;
	protected File modsFolder;
	
	public HaddonUtilityImpl()
	{
		// Initialize reflection (Call the static constructor)
		HaddonUtilitySingleton.getInstance();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void registerPrivateGetter(
		String name, Class classToPerformOn, int zeroOffsets, String... lessToMoreImportantFieldName)
	{
		this.getters.put(
			name, new HaddonPrivateEntry(name, classToPerformOn, zeroOffsets, lessToMoreImportantFieldName));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void registerPrivateSetter(
		String name, Class classToPerformOn, int zeroOffsets, String... lessToMoreImportantFieldName)
	{
		this.setters.put(
			name, new HaddonPrivateEntry(name, classToPerformOn, zeroOffsets, lessToMoreImportantFieldName));
	}
	
	@Override
	public Object getPrivate(Object instance, String name) throws PrivateAccessException
	{
		return this.getters.get(name).get(instance);
	}
	
	@Override
	public void setPrivate(Object instance, String name, Object value) throws PrivateAccessException
	{
		this.setters.get(name).set(instance, value);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getPrivateValue(Class classToPerformOn, Object instanceToPerformOn, int zeroOffsets)
		throws PrivateAccessException
	{
		return HaddonUtilitySingleton.getInstance().getPrivateValue(classToPerformOn, instanceToPerformOn, zeroOffsets);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setPrivateValue(Class classToPerformOn, Object instanceToPerformOn, int zeroOffsets, Object newValue)
		throws PrivateAccessException
	{
		HaddonUtilitySingleton.getInstance().setPrivateValue(
			classToPerformOn, instanceToPerformOn, zeroOffsets, newValue);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getPrivateValueLiteral(
		Class classToPerformOn, Object instanceToPerformOn, String obfPriority, int zeroOffsetsDebug)
		throws PrivateAccessException
	{
		Object ret;
		try
		{
			ret =
				HaddonUtilitySingleton.getInstance().getPrivateValueViaName(
					classToPerformOn, instanceToPerformOn, obfPriority);
			
		}
		catch (Exception e)
		{
			ret =
				HaddonUtilitySingleton.getInstance().getPrivateValue(
					classToPerformOn, instanceToPerformOn, zeroOffsetsDebug); // This throws a PrivateAccessException
			
		}
		
		return ret;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setPrivateValueLiteral(
		Class classToPerformOn, Object instanceToPerformOn, String obfPriority, int zeroOffsetsDebug, Object newValue)
		throws PrivateAccessException
	{
		try
		{
			HaddonUtilitySingleton.getInstance().setPrivateValueViaName(
				classToPerformOn, instanceToPerformOn, obfPriority, newValue);
			
		}
		catch (PrivateAccessException e)
		{
			HaddonUtilitySingleton.getInstance().setPrivateValue(
				classToPerformOn, instanceToPerformOn, zeroOffsetsDebug, newValue); // This throws a PrivateAccessException
			
		}
	}
	
	@Override
	public int getWorldHeight()
	{
		return WORLD_HEIGHT;
		
	}
	
	@Override
	public Object getCurrentScreen()
	{
		return Minecraft.getMinecraft().currentScreen;
		
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean isCurrentScreen(final Class classtype)
	{
		Object current = getCurrentScreen();
		
		if (classtype == null)
			return current == null;
		
		if (current == null)
			return false;
		
		return classtype.isInstance(current);
		
	}
	
	@Override
	public void closeCurrentScreen()
	{
		Minecraft.getMinecraft().displayGuiScreen(null);
		
	}
	
	@Override
	public void printChat(Object... args)
	{
		if (Minecraft.getMinecraft().thePlayer == null)
			return;
		
		StringBuilder builder = new StringBuilder();
		for (Object o : args)
		{
			builder.append(o);
		}
		Minecraft.getMinecraft().thePlayer.addChatMessage(builder.toString());
	}
	
	@Override
	public boolean areKeysDown(int... args)
	{
		for (int arg : args)
		{
			if (!Keyboard.isKeyDown(arg))
				return false;
			
		}
		
		return true;
		
	}
	
	private ScaledResolution drawString_scaledRes = null;
	private int drawString_screenWidth;
	private int drawString_screenHeight;
	private int drawString_textHeight;
	
	@Override
	public void prepareDrawString()
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		this.drawString_scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		this.drawString_screenWidth = this.drawString_scaledRes.getScaledWidth();
		this.drawString_screenHeight = this.drawString_scaledRes.getScaledHeight();
		this.drawString_textHeight = mc.fontRenderer.FONT_HEIGHT;
		
	}
	
	@Override
	public void drawString(
		String text, float px, float py, int offx, int offy, char alignment, int cr, int cg, int cb, int ca,
		boolean hasShadow)
	{
		if (this.drawString_scaledRes == null)
		{
			prepareDrawString();
		}
		
		Minecraft mc = Minecraft.getMinecraft();
		
		int xPos = (int) Math.floor(px * this.drawString_screenWidth) + offx;
		int yPos = (int) Math.floor(py * this.drawString_screenHeight) + offy;
		
		if (alignment == '2' || alignment == '5' || alignment == '8')
		{
			xPos = xPos - mc.fontRenderer.getStringWidth(text) / 2;
		}
		else if (alignment == '3' || alignment == '6' || alignment == '9')
		{
			xPos = xPos - mc.fontRenderer.getStringWidth(text);
		}
		
		if (alignment == '4' || alignment == '5' || alignment == '6')
		{
			yPos = yPos - this.drawString_textHeight / 2;
		}
		else if (alignment == '1' || alignment == '2' || alignment == '3')
		{
			yPos = yPos - this.drawString_textHeight;
		}
		
		int color = ca << 24 | cr << 16 | cg << 8 | cb;
		
		if (hasShadow)
		{
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, xPos, yPos, color);
		}
		else
		{
			Minecraft.getMinecraft().fontRenderer.drawString(text, xPos, yPos, color);
		}
	}
	
	@Override
	public File getModsFolder()
	{
		if (this.modsFolder != null)
			return this.modsFolder;
		
		this.modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
		return this.modsFolder;
	}
	
}
