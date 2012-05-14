package net.minecraft.src;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.mod.Ha3Mod;
import eu.ha3.mc.mod.Ha3ModManager_ModLoader;

public abstract class Ha3Util_ModLoader extends net.minecraft.src.BaseMod
{
	final private Ha3Mod mod;
	
	/**
	 * Instanciates the mod and its core.
	 * 
	 * @return
	 */
	abstract Ha3Mod instantiateMod();
	
	public Ha3Util_ModLoader()
	{
		mod = instantiateMod();
		mod.setReference(this);
		mod.setManager(new Ha3ModManager_ModLoader());
		
	}
	
	@Override
	public void load()
	{
		mod.core().load();
		
	}
	
	@Override
	public boolean onTickInGame(float fspan, Minecraft game) // Actually, "RenderFrame"
	{
		mod.manager().communicateFrame(fspan);
		
		return true;
		
	}
	
	@Override
	public void keyboardEvent(KeyBinding event)
	{
		mod.manager().communicateKeyBindingEvent(event);
		
	}
	
	@Override
	public void receiveChatPacket(String contents)
	{
		mod.manager().communicateChat(contents);
		
	}
	
	@Override
	public void receiveCustomPacket(Packet250CustomPayload packet)
	{
		mod.manager().communicateIncomingChannelPacket(packet);
		
	}
	
}
