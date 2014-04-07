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
import eu.ha3.mc.haddon.Bridge;
import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Manager;
import eu.ha3.mc.haddon.SupportsChatEvents;
import eu.ha3.mc.haddon.SupportsConnectEvents;
import eu.ha3.mc.haddon.SupportsEverythingReady;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsIncomingMessages;
import eu.ha3.mc.haddon.SupportsInitialization;
import eu.ha3.mc.haddon.SupportsKeyEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;
import eu.ha3.mc.haddon.UnsupportedInterfaceException;
import eu.ha3.mc.haddon.Utility;
import eu.ha3.mc.haddon.implem.HaddonUtilityModLoader;

import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.core.LiteLoader;

import eu.ha3.mc.haddon.implem.HaddonUtilityImpl;

/*
--filenotes-placeholder
*/

public class LiteBase implements LiteMod, Manager, Bridge
{
	private Haddon haddon;
	private Utility utility;
	
	private boolean supportsTick;
	private boolean supportsFrame;
	private boolean supportsChat;
	private boolean supportsKey;
	private boolean supportsIncomingMessages;
	private boolean supportsEverythingReady;
	private boolean supportsConnect;
	
	private Set<String> enlistedIncomingMessages;
	private Set<String> enlistedOutgoingMessages;
	
	private boolean tickEnabled;
	private boolean frameEnabled;
	private boolean chatEnabled;
	
	private Map<Object, Object> renderPairs;
	
	private boolean impl_continueTicking;
	
	private long nextTick;
	
	private long ticksRan;
	
	public LiteBase(Haddon haddon)
	{
		this.haddon = haddon;
		
		this.utility = new HaddonUtilityModLoader(this);
		haddon.setManager(this);
		
		this.supportsTick = haddon instanceof SupportsTickEvents;
		this.supportsFrame = haddon instanceof SupportsFrameEvents;
		this.supportsChat = haddon instanceof SupportsChatEvents;
		this.supportsKey = haddon instanceof SupportsKeyEvents;
		this.supportsIncomingMessages = haddon instanceof SupportsIncomingMessages;
		this.supportsEverythingReady = haddon instanceof SupportsEverythingReady;
		this.supportsConnect = haddon instanceof SupportsConnectEvents;
		
		this.impl_continueTicking = this.supportsTick || this.supportsFrame;
		
		this.nextTick = -1;
		
		this.enlistedOutgoingMessages = new HashSet<String>();
		if (this.supportsIncomingMessages)
		{
			this.enlistedIncomingMessages = new HashSet<String>();
			
		}
		
		if (haddon instanceof SupportsInitialization)
		{
			((SupportsInitialization) haddon).onInitialize();
		}
		
	}
	
	@Override
	public Haddon getHaddon()
	{
		return this.haddon;
		
	}
	
	@Override
	public void load()
	{
		this.haddon.onLoad();
		
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
	
	@Override
	public void clientChat(String contents)
	{
		if (this.supportsChat && this.chatEnabled)
		{
			((SupportsChatEvents) this.haddon).onChat(contents);
		}
		
	}
	
	@Override
	public void hookTickEvents(boolean enable)
	{
		if (!this.supportsTick)
			throw new UnsupportedInterfaceException();
		
		this.tickEnabled = enable;
		
	}
	
	@Override
	public void hookFrameEvents(boolean enable)
	{
		if (!this.supportsFrame)
			throw new UnsupportedInterfaceException();
		
		this.frameEnabled = enable;
		
	}
	
	@Override
	public void hookChatEvents(boolean enable)
	{
		if (!this.supportsChat)
			throw new UnsupportedInterfaceException();
		
		this.chatEnabled = enable;
		
	}
	
	@Override
	public String getVersion()
	{
		return "REMOVE ME FROM IMPL"; //TODO
	}
	
	@Override
	public Utility getUtility()
	{
		return this.utility;
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addRenderable(Class renderable, Object renderer)
	{
		if (this.renderPairs == null)
		{
			this.renderPairs = new HashMap<Object, Object>();
		}
		
		this.renderPairs.put(renderable, renderer);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addRenderer(Map map)
	{
		if (this.renderPairs != null)
		{
			map.putAll(this.renderPairs);
		}
		
	}
	
	@Override
	public void addKeyBinding(KeyBinding keyBindingIn, String localization)
	{
		ModLoader.addLocalization(keyBindingIn.keyDescription, localization);
		ModLoader.registerKey(this, keyBindingIn, true);
		
	}
	
	@Override
	public void keyboardEvent(KeyBinding event)
	{
		if (!this.supportsKey)
			return;
		
		((SupportsKeyEvents) this.haddon).onKey(event);
		
	}
	
	@Override
	public void enlistIncomingMessages(String channel)
	{
		if (!this.supportsIncomingMessages)
			throw new UnsupportedInterfaceException();
		
		this.enlistedIncomingMessages.add(channel);
		
		// XXX: Need to implement sending the REGISTER(channel) packet to the server
		// Modloader implementation may not work as expected
		ModLoader.registerPacketChannel(this, channel);
		
	}
	
	@Override
	public void enlistOutgoingMessages(String channel)
	{
		if (channel == null)
			throw new IllegalArgumentException();
		
		this.enlistedOutgoingMessages.add(channel);
		
	}
	
	@Override
	public void delistIncomingMessages(String channel)
	{
		if (!this.supportsIncomingMessages)
			throw new UnsupportedInterfaceException();
		
		this.enlistedIncomingMessages.remove(channel);
		// XXX: Need to implement sending the UNREGISTER(channel) packet to the server
	}
	
	@Override
	public void delistOutgoingMessages(String channel)
	{
		this.enlistedOutgoingMessages.remove(channel);
		
	}
	
	@Override
	public void sendOutgoingMessage(Packet250CustomPayload message)
	{
		ModLoader.clientSendPacket(message);
		
	}
	
	@Override
	public void clientCustomPayload(NetClientHandler var1, Packet250CustomPayload message)
	{
		if (!this.supportsIncomingMessages)
			return;
		
		if (this.enlistedIncomingMessages.contains(message.channel) || this.enlistedIncomingMessages.contains(null))
		{
			((SupportsIncomingMessages) this.haddon).onIncomingMessage(message);
			
		}
		
	}
	
	public long bridgeTicksRan()
	{
		return this.ticksRan;
		
	}
	
	@Override
	public void modsLoaded()
	{
		if (this.supportsEverythingReady)
		{
			((SupportsEverythingReady) this.haddon).onEverythingReady();
		}
	}
	
	@Override
	public void clientConnect(NetClientHandler handler)
	{
		if (this.supportsConnect)
		{
			((SupportsConnectEvents) this.haddon).onConnectEvent(handler);
		}
		
	}
	
	@Override
	public boolean onTickInGUI(float semi, Minecraft minecraft, GuiScreen gui)
	{
		if (this.supportsGuiTick && this.guiTickEnabled)
		{
			long tick = this.utility.getClientTick();
			if (tick != this.lastGuiTick)
			{
				((SupportsGuiTickEvents) this.haddon).onGuiTick(gui);
				this.lastGuiTick = tick;
				
			}
			
		}
		
		if (this.supportsGuiFrame && this.guiFrameEnabled)
		{
			((SupportsGuiFrameEvents) this.haddon).onGuiFrame(gui, semi);
			
		}
		
		return this.impl_continueGuiTicking;
		
	}
	
	@Override
	public boolean onTickInGame(float semi, Minecraft minecraft)
	{
		if (this.supportsTick && this.tickEnabled)
		{
			/*long tick = this.utility.getClientTick();
			if (tick != this.lastTick)
			{
				((SupportsTickEvents) this.haddon).onTick();
				this.lastTick = tick;
				
			}*/
			
			// Shitty inaccurate workaround because the normal method has evolved
			// (1.3.1 version can jump tick by running it multiple times at once
			// and doesn't have a tick accumulator)
			long tick = System.currentTimeMillis();
			if (tick > this.nextTick)
			{
				((SupportsTickEvents) this.haddon).onTick();
				this.nextTick = tick + 50;
				this.ticksRan++;
				
			}
			
		}
		
		if (this.supportsFrame && this.frameEnabled)
		{
			((SupportsFrameEvents) this.haddon).onFrame(semi);
			
		}
		
		return this.impl_continueTicking;
		
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
	public void hookGuiTickEvents(boolean enable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hookGuiFrameEvents(boolean enable) {
		// TODO Auto-generated method stub
		
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
