package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.ha3.matmos.engine.MAtmosData;
import eu.ha3.matmos.engine.MAtmosException;
import eu.ha3.matmos.engine.MAtmosKnowledge;
import eu.ha3.matmos.engine.MAtmosSoundManager;
import eu.ha3.matmos.engine.MAtmosUtilityLoader;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

public class MAtExpansion
{
	private DocumentBuilder documentBuilder;
	private Document document;
	private XPath xpath;
	private MAtmosKnowledge knowledge;
	
	private String userDefinedIdentifier;
	
	private String docName;
	private String docDescription;
	
	private boolean isReady;
	private MAtExpansionError error;
	
	private boolean hasStructure;
	
	private int dataFrequency;
	private int dataCyclic;
	
	MAtExpansion(String userDefinedIdentifier)
	{
		this.userDefinedIdentifier = userDefinedIdentifier;
		this.isReady = false;
		this.hasStructure = false;
		this.error = MAtExpansionError.NO_DOCUMENT;
		
		this.docName = userDefinedIdentifier;
		this.docDescription = "";
		
		this.knowledge = new MAtmosKnowledge();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		XPathFactory xpf = XPathFactory.newInstance();
		
		this.xpath = xpf.newXPath();
		
		this.dataFrequency = 1;
		this.dataCyclic = 0;
		
		try
		{
			documentBuilder = dbf.newDocumentBuilder();
			
		}
		catch (ParserConfigurationException e)
		{
			// FIXME: Unhandled recoverable error thrown as unrecoverable
			e.printStackTrace();
			throw new RuntimeException();
			
		}
		
	}
	
	public void setSoundManager(MAtmosSoundManager soundManager)
	{
		knowledge.setSoundManager(soundManager);
		
	}
	
	public void setData(MAtmosData data)
	{
		knowledge.setData(data);
		
	}
	
	public void inputStructure(InputStream stream)
	{
		hasStructure = false;
		try
		{
			document = documentBuilder.parse(stream);
			NodeList explist = document.getElementsByTagName("expansion");
			if (explist.getLength() == 1)
			{
				Node exp = explist.item(0);
				
				String name = xpath.evaluate("./name", exp);
				String desc = xpath.evaluate("./description", exp);
				String dataFreq = xpath.evaluate("./data", exp);
				
				if (name != null)
					this.docName = name;
				
				if (desc != null)
					this.docDescription = desc;
				
				if (dataFreq != null)
				{
					try
					{
						this.dataFrequency = Integer.parseInt(dataFreq);
						if (this.dataFrequency < 1)
							this.dataFrequency = 1;
						
						MAtMod.LOGGER.fine("Set " + this.userDefinedIdentifier
								+ " frequency to " + this.dataFrequency);
						
					}
					catch (NumberFormatException e)
					{
						;
						
					}
					
				}
				
			}
			
			hasStructure = true;
			
		}
		catch (SAXException e)
		{
			error = MAtExpansionError.COULD_NOT_PARSE_XML;
			e.printStackTrace();
			
		}
		catch (IOException e)
		{
			error = MAtExpansionError.COULD_NOT_PARSE_XML;
			e.printStackTrace();
			
		}
		catch (XPathExpressionException e)
		{
			MAtMod.LOGGER.warning("Error with XPath on expansion "
					+ userDefinedIdentifier);
			e.printStackTrace();
			
		}
		
	}
	
	public void buildKnowledge()
	{
		if (document == null)
			return;
		
		if (!hasStructure)
			return;
		
		try
		{
			knowledge.patchKnowledge();
			// loadKnowledge returns the validity of the knowledge
			isReady = MAtmosUtilityLoader.getInstance().loadKnowledge(
					knowledge,
					document, false);
			
		}
		catch (MAtmosException e)
		{
			error = MAtExpansionError.COULD_NOT_MAKE_KNOWLEDGE;
			e.printStackTrace();
			
		}
		
	}
	
	public void soundRoutine()
	{
		if (isReady)
			knowledge.soundRoutine();
		
	}
	
	public void dataRoutine()
	{
		if (isReady)
		{
			if (dataFrequency > 1)
			{
				if (dataCyclic == 0)
					knowledge.dataRoutine();
				
				dataCyclic = (dataCyclic + 1) % dataFrequency;
				
			}
			else
			{
				knowledge.dataRoutine();
				
			}
			
		}
		
	}
	
	public MAtExpansionError getError()
	{
		return error;
		
	}
	
	public String getUserDefinedName()
	{
		return userDefinedIdentifier;
		
	}
	
	public String getName()
	{
		return docName;
		
	}
	
	public String getDescription()
	{
		return docDescription;
		
	}
	
	public boolean isRunning()
	{
		return knowledge.isTurnedOn();
		
	}
	
	public boolean isReady()
	{
		return isReady;
		
	}
	
	public boolean hasStructure()
	{
		return hasStructure;
		
	}
	
	public void turnOn()
	{
		//if (!isReady || isRunning())
		if (isRunning())
			return;
		
		if (!isReady && hasStructure)
			this.buildKnowledge();
		
		if (isReady)
			knowledge.turnOn();
		
	}
	
	public void turnOff()
	{
		if (!isReady || !isRunning())
			return;
		
		knowledge.turnOff();
		
	}
	
	public float getVolume()
	{
		return 1.0F; // TOGO getVolume
		
	}
	
	public String getDocumentStringForm()
	{
		if (document == null)
			return null;
		
		/*DOMImplementationLS domImplementation = (DOMImplementationLS) document
		.getImplementation();
		LSSerializer lsSerializer = domImplementation.createLSSerializer();
		return lsSerializer.writeToString(document);*/
		
		// http://stackoverflow.com/questions/1636792/domimplementationls-serialize-to-string-in-utf-8-in-java
		
		StringWriter output = new StringWriter();
		
		try
		{
			Transformer transformer = TransformerFactory.newInstance()
			.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(
					output));
		}
		catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerFactoryConfigurationError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output.toString();
		
	}
	
	public void printKnowledge() // XXX Debugging function, remove me
	{
		try
		{
			System.out.println(knowledge.createXML());
		}
		catch (XMLStreamException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void patchKnowledge()
	{
		knowledge.patchKnowledge();
		isReady = false;
		
	}
	
}
