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
 * ----------------------------------------------------------------------------
 * "THE COLA-WARE LICENSE" (Revision 0):
 * Hurricaaane wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a cola in return
 * Georges "Hurricaaane" Yam
 * ----------------------------------------------------------------------------
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
		defLastFound = mAtmosHaddon.VERSION;
		
		lastFound = defLastFound;
		displayCount = defDisplayCount;
		displayRemaining = defDisplayRemaining;
		enabled = defEnabled;
	}
	
	public void attempt()
	{
		if (!enabled)
			return;
		
		this.start();
		
	}
	
	@Override
	public void run()
	{
		try
		{
			URL url = new URL(
					"http://ha3extra.googlecode.com/svn/trunk/matmos/version.xml");
			
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
						maxvn = vn;
					
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
			
			if (maxvn > mod.VERSION)
			{
				boolean needsSave = false;
				if (maxvn != lastFound)
				{
					lastFound = maxvn;
					displayRemaining = displayCount;
					
					needsSave = true;
					
				}
				
				if (displayRemaining > 0)
				{
					displayRemaining = displayRemaining - 1;
					
					int vc = maxvn - mod.VERSION;
					mod.printChat(Ha3Utility.COLOR_GOLD,
							"A ",
							Ha3Utility.COLOR_WHITE, "r" + maxvn,
							Ha3Utility.COLOR_GOLD,
							" update is available (You're ",
							Ha3Utility.COLOR_WHITE, vc, Ha3Utility.COLOR_GOLD,
							" version" + (vc > 1 ? "s" : "") + " late).");
					
					if (displayRemaining > 0)
						mod.printChat(Ha3Utility.COLOR_GRAY,
								"This message will display ",
								Ha3Utility.COLOR_WHITE,
								displayRemaining,
								Ha3Utility.COLOR_GRAY,
								" more time"
										+ (displayRemaining > 1 ? "s" : "")
										+ ".");
					
					else
						mod.printChat(Ha3Utility.COLOR_GRAY,
								"You won't be notified anymore until a newer version.");
					
					needsSave = true;
					
				}
				
				if (needsSave)
					mod.options().saveOptions();
				
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
		if (config == null)
			config = createDefaultOptions();
		
		try
		{
			{
				String query = "update.found.version.value";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					lastFound = Integer.parseInt(prop);
					config.put(query, prop);
				}
				
			}
			{
				String query = "update.found.display.remaining.value";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					displayRemaining = Integer.parseInt(prop);
					config.put(query, prop);
				}
				
			}
			{
				String query = "update.found.display.count.value";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					displayCount = Integer.parseInt(prop);
					config.put(query, prop);
				}
				
			}
			{
				String query = "update.found.use";
				if (options.containsKey(query))
				{
					String prop = options.getProperty(query);
					enabled = Integer.parseInt(prop) == 1 ? true : false;
					config.put(query, prop);
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
		if (config == null)
			return createDefaultOptions();
		
		config.setProperty("update.found.version.value", "" + lastFound);
		config.setProperty("update.found.display.remaining.value", ""
				+ displayRemaining);
		config.setProperty("update.found.display.count.value", ""
				+ displayCount);
		config.setProperty("update.found.use", enabled ? "1" : "0");
		
		return config;
	}
	
	@Override
	public void defaultOptions()
	{
		inputOptions(createDefaultOptions());
		
	}
	
	private Properties createDefaultOptions()
	{
		Properties options = new Properties();
		options.setProperty("update.found.version.value", "" + defLastFound);
		options.setProperty("update.found.display.remaining.value", ""
				+ defDisplayRemaining);
		options.setProperty("update.found.display.count.value", ""
				+ defDisplayCount);
		options.setProperty("update.found.use", defEnabled ? "1" : "0");
		
		return options;
		
	}
	
}
