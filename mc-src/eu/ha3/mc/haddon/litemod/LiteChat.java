package eu.ha3.mc.haddon.litemod;

import net.minecraft.util.IChatComponent;

import com.mumfrey.liteloader.ChatListener;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.OperatorChatter;
import eu.ha3.mc.haddon.supporting.SupportsChatEvents;

/*
--filenotes-placeholder
*/

public class LiteChat extends LiteBase implements OperatorChatter, ChatListener
{
	protected final boolean suChat;
	
	protected boolean enableChat;
	
	public LiteChat(Haddon haddon)
	{
		super(haddon);
		this.suChat = haddon instanceof SupportsChatEvents;
	}
	
	@Override
	public void setChatEnabled(boolean enabled)
	{
		this.enableChat = enabled;
	}
	
	@Override
	public void onChat(IChatComponent chat, String message)
	{
		if (this.suChat && this.enableChat)
		{
			((SupportsChatEvents) this.haddon).onChat(chat, message);
		}
	}
}
