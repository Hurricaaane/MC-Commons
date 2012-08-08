package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.ha3.mc.convenience.Ha3Personalizable;

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

public class MAtUpdateNotifier extends Thread implements Ha3Personalizable
{
	private MAtMod mod;
	
	private int lastFound;
	private int displayCount;
	private int displayRemaining;
	private boolean enabled;
	final private int defLastFound;
	final private int defDisplayCount = 3;
	final private int defDisplayRemaining = 0;
	final private boolean defEnabled = true;
	
	private Properties config;
	
	MAtUpdateNotifier(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		this.defLastFound = mAtmosHaddon.VERSION;
		
		this.lastFound = this.defLastFound;
		this.displayCount = this.defDisplayCount;
		this.displayRemaining = this.defDisplayRemaining;
		this.enabled = this.defEnabled;
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
			URL url = new URL("http://ha3extra.googlecode.com/svn/trunk/matmos/version.xml");
			
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
			MAtMod.LOGGER.info("Update version found: " + maxvn);
			
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return;
				
			}
			
			if (maxvn > this.mod.VERSION)
			{
				boolean needsSave = false;
				if (maxvn != this.lastFound)
				{
					this.lastFound = maxvn;
					this.displayRemaining = this.displayCount;
					
					needsSave = true;
					
				}
				
				if (this.displayRemaining > 0)
				{
					this.displayRemaining = this.displayRemaining - 1;
					
					int vc = maxvn - this.mod.VERSION;
					this.mod.printChat(
						Ha3Utility.COLOR_GOLD, "A ", Ha3Utility.COLOR_WHITE, "r" + maxvn, Ha3Utility.COLOR_GOLD,
						" update is available (You're ", Ha3Utility.COLOR_WHITE, vc, Ha3Utility.COLOR_GOLD, " version"
							+ (vc > 1 ? "s" : "") + " late).");
					
					if (this.displayRemaining > 0)
					{
						this.mod.printChat(
							Ha3Utility.COLOR_GRAY, "This message will display ", Ha3Utility.COLOR_WHITE,
							this.displayRemaining, Ha3Utility.COLOR_GRAY, " more time"
								+ (this.displayRemaining > 1 ? "s" : "") + ".");
					}
					else
					{
						this.mod.printChat(
							Ha3Utility.COLOR_GRAY, "You won't be notified anymore until a newer version.");
					}
					
					needsSave = true;
					
				}
				
				if (needsSave)
				{
					this.mod.getOptions().saveOptions();
				}
				
			}
			
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		finally
		{
			
		}
	}
	
	@Override
	public void inputOptions(Properties options)
	{
		if (this.config == null)
		{
			this.config = createDefaultOptions();
		}
		
		try
		{
			{
				String query = "update.found.version.value";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					this.lastFound = Integer.parseInt(prop);
					this.config.put(query, prop);
				}
				
			}
			{
				String query = "update.found.display.remaining.value";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					this.displayRemaining = Integer.parseInt(prop);
					this.config.put(query, prop);
				}
				
			}
			{
				String query = "update.found.display.count.value";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					this.displayCount = Integer.parseInt(prop);
					this.config.put(query, prop);
				}
				
			}
			{
				String query = "update.found.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					this.enabled = Integer.parseInt(prop) == 1 ? true : false;
					this.config.put(query, prop);
				}
				
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public Properties outputOptions()
	{
		if (this.config == null)
			return createDefaultOptions();
		
		this.config.setProperty("update.found.version.value", "" + this.lastFound);
		this.config.setProperty("update.found.display.remaining.value", "" + this.displayRemaining);
		this.config.setProperty("update.found.display.count.value", "" + this.displayCount);
		this.config.setProperty("update.found.use", this.enabled ? "1" : "0");
		
		return this.config;
	}
	
	@Override
	public void defaultOptions()
	{
		inputOptions(createDefaultOptions());
		
	}
	
	private Properties createDefaultOptions()
	{
		Properties options = new Properties();
		options.setProperty("update.found.version.value", "" + this.defLastFound);
		options.setProperty("update.found.display.remaining.value", "" + this.defDisplayRemaining);
		options.setProperty("update.found.display.count.value", "" + this.defDisplayCount);
		options.setProperty("update.found.use", this.defEnabled ? "1" : "0");
		
		return options;
		
	}
	
}
