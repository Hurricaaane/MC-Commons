package eu.ha3.mc.haddon.litemod;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.src.GuiScreen;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Minecraft;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet250CustomPayload;
import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Manager;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.SupportsChatEvents;
import eu.ha3.mc.haddon.SupportsConnectEvents;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsIncomingMessages;
import eu.ha3.mc.haddon.SupportsInitialization;
import eu.ha3.mc.haddon.SupportsKeyEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;
import eu.ha3.mc.haddon.UnsupportedInterfaceException;
import eu.ha3.mc.haddon.Utility;

import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;

import eu.ha3.mc.haddon.implem.HaddonUtilityImpl;

/*
--filenotes-placeholder 
*/
/* x-placeholder-wtfplv2 */

public class LiteBase implements Tickable, InitCompleteListener, OperatorCaster
{
	private Utility utility;
	protected final Haddon haddon;
	protected final boolean shouldTick;
	protected final boolean suTick;
	protected final boolean suFrame;
	
	protected int tickCounter;
	protected boolean enableTick;
	protected boolean enableFrame;

	private long ticksRan;
	
	public LiteBase(Haddon haddon)
	{
		this.haddon = haddon;
		this.suTick = haddon instanceof SupportsTickEvents;
		this.suFrame = haddon instanceof SupportsFrameEvents;
		this.shouldTick = this.suTick || this.suFrame;
		
		this.haddon.setUtility(new HaddonUtilityImpl() {
			@Override
			public long getClientTick()
			{
				return getTicks();
			}
		});
		
		this.haddon.setOperator(this);

	}
	
	@Override
	public String getName()
	{
		return this.haddon.getIdentity().getHaddonName();
	}
	
	@Override
	public String getVersion()
	{
		return this.haddon.getIdentity().getHaddonHumanVersion();
	}
	
	@Override
	public void onInitCompleted(Minecraft minecraft, LiteLoader loader)
	{
		this.haddon.onLoad();
	}

	public Utility getUtility()
	{
		return this.utility;
		
	}
	
	public long bridgeTicksRan()
	{
		return this.ticksRan;
		
	}
	
	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
	{
		if (!this.shouldTick)
			return;
		
		if (!inGame)
			return;
		
		if (this.enableTick && clock)
		{
			if (this.suTick)
			{
				((SupportsTickEvents) this.haddon).onTick();
			}
			this.tickCounter++;
		}
		
		if (this.enableFrame)
		{
			if (this.suFrame)
			{
				((SupportsFrameEvents) this.haddon).onFrame(partialTicks);
			}
		}
	}
	
	@Override
	public void setTickEnabled(boolean enabled)
	{
		this.enableTick = enabled;
	}
	
	@Override
	public void setFrameEnabled(boolean enabled)
	{
		this.enableFrame = enabled;
	}
	
	@Override
	public int getTicks()
	{
		return this.tickCounter;
	}

	@Override
	public void init(File configPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void upgradeSettings(String version, File configPath,
			File oldConfigPath) {
		// TODO Auto-generated method stub
		
	}
}
