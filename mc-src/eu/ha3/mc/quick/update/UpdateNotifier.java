package eu.ha3.mc.quick.update;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.ha3.mc.quick.chat.ChatColorsSimple;
import eu.ha3.mc.quick.chat.Chatter;
import eu.ha3.util.property.simple.ConfigProperty;

/* x-placeholder-wtfplv2 */

/**
 * The Update Notifier.
 * 
 * @author Hurry
 * 
 */
public class UpdateNotifier extends Thread
{
	private final NotifiableHaddon haddon;
	private final String queryLocation;
	
	private int lastFound;
	
	private int displayCount = 3;
	private int displayRemaining = 0;
	private boolean enabled = true;
	
	public UpdateNotifier(NotifiableHaddon mod, String queryLocation)
	{
		this.haddon = mod;
		this.queryLocation = queryLocation;
		
		this.lastFound = mod.getIdentity().getHaddonVersionNumber();
	}
	
	public void attempt()
	{
		if (!this.enabled)
			return;
		
		start();
	}
	
	@Override
	public void run()
	{
		try
		{
			final int currentVersionNumber = this.haddon.getIdentity().getHaddonVersionNumber();
			
			URL url = new URL(String.format(this.queryLocation, currentVersionNumber));
			
			InputStream contents = url.openStream();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(contents);
			
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xp = xpf.newXPath();
			
			NodeList nl = doc.getElementsByTagName("release");
			
			int maxvn = 0;
			for (int i = 0; i < nl.getLength(); i++)
			{
				Node release = nl.item(i);
				String versionnumber = xp.evaluate("./version", release);
				if (versionnumber != null)
				{
					int vn = Integer.parseInt(versionnumber);
					if (vn > maxvn)
					{
						maxvn = vn;
					}
					
				}
				
			}
			
			System.out.println("(UN: "
				+ this.haddon.getIdentity().getHaddonName() + ") Update version found: " + maxvn + " (running "
				+ currentVersionNumber + ")");
			
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return;
			}
			
			if (maxvn > currentVersionNumber)
			{
				ConfigProperty config = this.haddon.getConfig();
				Chatter chatter = this.haddon.getChatter();
				
				boolean needsSave = false;
				if (maxvn != this.lastFound)
				{
					this.lastFound = maxvn;
					this.displayRemaining = this.displayCount;
					
					needsSave = true;
					config.setProperty("update_found.version", this.lastFound);
					config.setProperty("update_found.display.remaining.value", this.displayRemaining);
				}
				
				if (this.displayRemaining > 0)
				{
					this.displayRemaining = this.displayRemaining - 1;
					config.setProperty("update_found.display.remaining.value", this.displayRemaining);
					
					int vc = maxvn - currentVersionNumber;
					chatter.printChat(
						ChatColorsSimple.COLOR_GOLD, "A ", ChatColorsSimple.COLOR_WHITE, "r" + maxvn,
						ChatColorsSimple.COLOR_GOLD, " update is available (You're ", ChatColorsSimple.COLOR_WHITE, vc,
						ChatColorsSimple.COLOR_GOLD, " version" + (vc > 1 ? "s" : "") + " late). ");
					chatter.printChatShort(ChatColorsSimple.COLOR_BRIGHTGREEN
						+ ChatColorsSimple.THEN_UNDERLINE + " " + this.haddon.getIdentity().getHaddonAddress());
					
					if (this.displayRemaining > 0)
					{
						chatter.printChatShort(
							ChatColorsSimple.COLOR_GRAY, "This message will display ", ChatColorsSimple.COLOR_WHITE,
							this.displayRemaining, ChatColorsSimple.COLOR_GRAY, " more time"
								+ (this.displayRemaining > 1 ? "s" : "") + ".");
					}
					else
					{
						chatter.printChatShort(
							ChatColorsSimple.COLOR_GRAY,
							"You won't be notified anymore unless a newer version comes out.");
					}
					
					needsSave = true;
				}
				
				if (needsSave)
				{
					this.haddon.saveConfig();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		}
	}
	
	public void fillDefaults(ConfigProperty configuration)
	{
		configuration.setProperty("update_found.enabled", true);
		configuration.setProperty("update_found.version", this.haddon.getIdentity().getHaddonVersionNumber());
		configuration.setProperty("update_found.display.remaining.value", 0);
		configuration.setProperty("update_found.display.count.value", 3);
	}
	
	public void loadConfig(ConfigProperty configuration)
	{
		this.enabled = configuration.getBoolean("update_found.enabled");
		this.lastFound = configuration.getInteger("update_found.version");
		this.displayRemaining = configuration.getInteger("update_found.display.remaining.value");
		this.displayCount = configuration.getInteger("update_found.display.count.value");
	}
	
}
