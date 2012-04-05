package eu.ha3.matmos.engine;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

public class MAtmosUtilityLoader
{
	MAtmosKnowledge knowledgeWorkstation;
	
	static final String NAME = "name";
	
	static final String DESCRIPTIBLE = "descriptible";
	
	static final String NICKNAME = "nickname";
	static final String DESCRIPTION = "description";
	static final String ICON = "icon";
	static final String META = "meta";
	
	static final String LIST = "list";
	
	static final String CONDITION = "condition";
	static final String SHEET = "sheet";
	static final String KEY = "key";
	static final String DYNAMICKEY = "dynamickey";
	static final String SYMBOL = "symbol";
	static final String CONSTANT = "constant";
	
	static final String SET = "set";
	static final String TRUEPART = "truepart";
	static final String FALSEPART = "falsepart";
	
	static final String EVENT = "event";
	static final String VOLMIN = "volmin";
	static final String VOLMAX = "volmax";
	static final String PITCHMIN = "pitchmin";
	static final String PITCHMAX = "pitchmax";
	static final String METASOUND = "metasound";
	static final String PATH = "path";
	
	static final String MACHINE = "machine";
	static final String ALLOW = "allow";
	static final String RESTRICT = "restrict";
	
	static final String DYNAMIC = "dynamic";
	static final String ENTRY = "entry";
	
	static final String EVENTTIMED = "eventtimed";
	static final String EVENTNAME = "eventname";
	static final String VOLMOD = "volmod";
	static final String PITCHMOD = "pitchmod";
	static final String DELAYSTART = "delaystart";
	static final String DELAYMIN = "delaymin";
	static final String DELAYMAX = "delaymax";
	
	static final String STREAM = "stream";
	//PATH already covered
	static final String VOLUME = "volume";
	static final String PITCH = "pitch";
	static final String FADEINTIME = "fadeintime";
	static final String FADEOUTTIME = "fadeouttime";
	static final String DELAYBEFOREFADEIN = "delaybeforefadein";
	static final String DELAYBEFOREFADEOUT = "delaybeforefadeout";
	static final String ISLOOPING = "islooping";
	static final String ISUSINGPAUSE = "isusingpause";
	
	private XPath xp;
	
	private MAtmosUtilityLoader()
	{
		XPathFactory xpf = XPathFactory.newInstance();
		xp = xpf.newXPath();
		
	}
	
	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class MAtmosUtilityLoaderSingletonHolder
	{
		public static final MAtmosUtilityLoader instance = new MAtmosUtilityLoader();
	}
	
	public static MAtmosUtilityLoader getInstance()
	{
		return MAtmosUtilityLoaderSingletonHolder.instance;
	}
	
	///
	
	public boolean loadKnowledge(MAtmosKnowledge original, Document doc,
			boolean allowOverrides) throws MAtmosException
			{
		try
		{
			parseXML(original, doc, allowOverrides);
			return true;
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			return false;
			
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return false;
			
		}
		
		
			}
	
	private int toInt(String source)
	{
		try
		{
			return Integer.parseInt(source);
		}
		catch (NumberFormatException e)
		{
			// FIXME: Is cast from float to int safe for about everything?
			return ((int) Float.parseFloat(source));
			
		}
		
	}
	
	private void extractXMLdescriptible(MAtmosKnowledge original, Node capsule,
			MAtmosDescriptible descriptible) throws XPathExpressionException
			{
		if (xp.evaluate("./" + DESCRIPTIBLE, capsule) != null)
		{
			Node desc = (Node) xp.evaluate("./" + DESCRIPTIBLE, capsule,
					XPathConstants.NODE);
			parseXMLdescriptible(original, desc, descriptible);
			
		}
		
			}
	
	private void parseXMLdescriptible(MAtmosKnowledge original, Node descNode,
			MAtmosDescriptible descriptible) throws XPathExpressionException
			{
		if (descNode == null)
		{
			return;
		}
		
		String nickname = xp.evaluate("./" + NICKNAME, descNode);
		String description = xp.evaluate("./" + DESCRIPTION, descNode);
		String icon = xp.evaluate("./" + ICON, descNode);
		String meta = xp.evaluate("./" + META, descNode);
		
		if (nickname != null)
			descriptible.nickname = nickname;
		if (description != null)
			descriptible.description = description;
		if (icon != null)
			descriptible.icon = icon;
		if (meta != null)
			descriptible.meta = meta;
		
			}
	
	private void parseXMLdynamic(MAtmosKnowledge original, Node capsule,
			String name, boolean allowOverrides)
	throws XPathExpressionException
	{
		boolean exists = original.getDynamic(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
			original.addDynamic(name);
		
		MAtmosDynamic descriptible = original.getDynamic(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		int entrycount = (int) Math.floor((Double) xp.evaluate("count(./"
				+ ENTRY + ")", capsule, XPathConstants.NUMBER));
		for (int n = 1; n <= entrycount; n++) // n begins to 1, ends up to count included
		{
			Node entry = (Node) xp.evaluate("./" + ENTRY + "[" + n + "]",
					capsule, XPathConstants.NODE);
			
			Node nameNode = entry.getAttributes().getNamedItem(SHEET);
			
			if (nameNode != null)
			{
				descriptible.addCouple(nameNode.getNodeValue(), Integer
						.parseInt(entry.getTextContent()));
				
			}
			
		}
		
	}
	
	private void parseXMLlist(MAtmosKnowledge original, Node capsule,
			String name, boolean allowOverrides)
	throws XPathExpressionException
	{
		boolean exists = original.getList(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
			original.addList(name);
		
		MAtmosList descriptible = original.getList(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		int constcount = (int) Math.floor((Double) xp.evaluate("count(./"
				+ CONSTANT + ")", capsule, XPathConstants.NUMBER));
		for (int n = 1; n <= constcount; n++) // n begins to 1, ends up to count included
		{
			String constant = xp.evaluate("./" + CONSTANT + "[" + n + "]",
					capsule);
			
			descriptible.add(toInt(constant));
			
		}
		
	}
	
	private void parseXMLcondition(MAtmosKnowledge original, Node capsule,
			String name, boolean allowOverrides)
	throws XPathExpressionException
	{
		boolean exists = original.getDataCondition(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
			original.addDataCondition(name);
		
		MAtmosCondition descriptible = original.getDataCondition(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		String sheet = xp.evaluate("./" + SHEET, capsule);
		String key = xp.evaluate("./" + KEY, capsule);
		String dynamickey = xp.evaluate("./" + DYNAMICKEY, capsule);
		String symbol = xp.evaluate("./" + SYMBOL, capsule);
		String constant = xp.evaluate("./" + CONSTANT, capsule);
		String list = xp.evaluate("./" + LIST, capsule);
		
		if (sheet != null)
			descriptible.setSheet(sheet);
		
		if (key != null)
			descriptible.setKey(toInt(key));
		
		if (dynamickey != null && !dynamickey.equals(""))
			descriptible.setDynamic(dynamickey);
		
		if (symbol != null)
			descriptible.setSymbol(symbol);
		
		if (constant != null)
			descriptible.setConstant(toInt(constant));
		
		if (list != null)
			descriptible.setList(list);
		
	}
	
	private void parseXMLset(MAtmosKnowledge original, Node capsule,
			String name, boolean allowOverrides)
	throws XPathExpressionException
	{
		boolean exists = original.getConditionSet(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
			original.addConditionSet(name);
		
		MAtmosConditionSet descriptible = original.getConditionSet(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		int truepartcount = (int) Math.floor((Double) xp.evaluate("count(./"
				+ TRUEPART + ")", capsule, XPathConstants.NUMBER));
		for (int n = 1; n <= truepartcount; n++) // n begins to 1, ends up to count included
		{
			String truepart = xp.evaluate("./" + TRUEPART + "[" + n + "]",
					capsule);
			
			descriptible.addCondition(truepart, true);
			
		}
		
		int falsepartcount = (int) Math.floor((Double) xp.evaluate("count(./"
				+ FALSEPART + ")", capsule, XPathConstants.NUMBER));
		for (int n = 1; n <= falsepartcount; n++) // n begins to 1, ends up to count included
		{
			String falsepart = xp.evaluate("./" + FALSEPART + "[" + n + "]",
					capsule);
			
			descriptible.addCondition(falsepart, false);
			
		}
		
	}
	
	private void parseXMLevent(MAtmosKnowledge original, Node capsule,
			String name, boolean allowOverrides)
	throws XPathExpressionException
	{
		boolean exists = original.getEvent(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
			original.addEvent(name);
		
		MAtmosEvent descriptible = original.getEvent(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		String volmin = xp.evaluate("./" + VOLMIN, capsule);
		String volmax = xp.evaluate("./" + VOLMAX, capsule);
		String pitchmin = xp.evaluate("./" + PITCHMIN, capsule);
		String pitchmax = xp.evaluate("./" + PITCHMAX, capsule);
		String metasound = xp.evaluate("./" + METASOUND, capsule);
		
		if (volmin != null)
			descriptible.volMin = Float.parseFloat(volmin);
		
		if (volmax != null)
			descriptible.volMax = Float.parseFloat(volmax);
		
		if (pitchmin != null)
			descriptible.pitchMin = Float.parseFloat(pitchmin);
		
		if (pitchmax != null)
			descriptible.pitchMax = Float.parseFloat(pitchmax);
		
		if (metasound != null)
			descriptible.metaSound = toInt(metasound);
		
		int pathcount = (int) Math.floor((Double) xp.evaluate("count(./" + PATH
				+ ")", capsule, XPathConstants.NUMBER));
		for (int n = 1; n <= pathcount; n++) // n begins to 1, ends up to count included
		{
			String path = xp.evaluate("./" + PATH + "[" + n + "]", capsule);
			
			descriptible.paths.add(path);
			
		}
		
	}
	
	private void parseXMLmachine(MAtmosKnowledge original, Node capsule,
			String name, boolean allowOverrides)
	throws XPathExpressionException
	{
		boolean exists = original.getMachine(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
			original.addMachine(name);
		
		MAtmosMachine descriptible = original.getMachine(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		int eventtimedcount = (int) Math.floor((Double) xp.evaluate("count(./"
				+ EVENTTIMED + ")", capsule, XPathConstants.NUMBER));
		for (int n = 1; n <= eventtimedcount; n++) // n begins to 1, ends up to count included
		{
			Node eventtimedNode = (Node) xp.evaluate("./" + EVENTTIMED + "["
					+ n + "]", capsule, XPathConstants.NODE);
			
			int size = descriptible.addEventTimed();
			inscriptXMLeventTimed(descriptible.getEventTimed(size - 1),
					eventtimedNode);
			
		}
		
		int streamcount = (int) Math.floor((Double) xp.evaluate("count(./"
				+ STREAM + ")", capsule, XPathConstants.NUMBER));
		for (int n = 1; n <= streamcount; n++) // n begins to 1, ends up to count included
		{
			Node streamNode = (Node) xp.evaluate("./" + STREAM + "[" + n + "]",
					capsule, XPathConstants.NODE);
			
			int size = descriptible.addStream();
			inscriptXMLstream(descriptible.getStream(size - 1), streamNode);
			
		}
		
		int allowcount = (int) Math.floor((Double) xp.evaluate("count(./"
				+ ALLOW + ")", capsule, XPathConstants.NUMBER));
		for (int n = 1; n <= allowcount; n++) // n begins to 1, ends up to count included
		{
			String allow = xp.evaluate("./" + ALLOW + "[" + n + "]", capsule);
			descriptible.addAllow(allow);
			
		}
		
		int restrictcount = (int) Math.floor((Double) xp.evaluate("count(./"
				+ RESTRICT + ")", capsule, XPathConstants.NUMBER));
		for (int n = 1; n <= restrictcount; n++) // n begins to 1, ends up to count included
		{
			String restrict = xp.evaluate("./" + RESTRICT + "[" + n + "]",
					capsule);
			descriptible.addRestrict(restrict);
			
		}
		
	}
	
	private void inscriptXMLeventTimed(MAtmosEventTimed inscriptible, Node specs)
	throws XPathExpressionException
	{
		String eventname = xp.evaluate("./" + EVENTNAME, specs);
		String volmod = xp.evaluate("./" + VOLMOD, specs);
		String pitchmod = xp.evaluate("./" + PITCHMOD, specs);
		String delaystart = xp.evaluate("./" + DELAYSTART, specs);
		String delaymin = xp.evaluate("./" + DELAYMIN, specs);
		String delaymax = xp.evaluate("./" + DELAYMAX, specs);
		
		if (eventname != null)
			inscriptible.event = eventname;
		
		if (volmod != null)
			inscriptible.volMod = Float.parseFloat(volmod);
		
		if (pitchmod != null)
			inscriptible.pitchMod = Float.parseFloat(pitchmod);
		
		if (delaystart != null)
			inscriptible.delayStart = Float.parseFloat(delaystart);
		
		if (delaymin != null)
			inscriptible.delayMin = Float.parseFloat(delaymin);
		
		if (delaymax != null)
			inscriptible.delayMax = Float.parseFloat(delaymax);
		
	}
	
	private void inscriptXMLstream(MAtmosStream inscriptible, Node specs)
	throws XPathExpressionException
	{
		String _PATH = xp.evaluate("./" + PATH, specs);
		String _VOLUME = xp.evaluate("./" + VOLUME, specs);
		String _PITCH = xp.evaluate("./" + PITCH, specs);
		String _FADEINTIME = xp.evaluate("./" + FADEINTIME, specs);
		String _FADEOUTTIME = xp.evaluate("./" + FADEOUTTIME, specs);
		String _DELAYBEFOREFADEIN = xp
		.evaluate("./" + DELAYBEFOREFADEIN, specs);
		String _DELAYBEFOREFADEOUT = xp.evaluate("./" + DELAYBEFOREFADEOUT,
				specs);
		String _ISLOOPING = xp.evaluate("./" + ISLOOPING, specs);
		String _ISUSINGPAUSE = xp.evaluate("./" + ISUSINGPAUSE, specs);
		
		if (_PATH != null)
			inscriptible.path = _PATH;
		
		if (_VOLUME != null)
			inscriptible.volume = Float.parseFloat(_VOLUME);
		
		if (_PITCH != null)
			inscriptible.pitch = Float.parseFloat(_PITCH);
		
		if (_FADEINTIME != null)
			inscriptible.fadeInTime = Float.parseFloat(_FADEINTIME);
		
		if (_FADEOUTTIME != null)
			inscriptible.fadeOutTime = Float.parseFloat(_FADEOUTTIME);
		
		if (_DELAYBEFOREFADEIN != null)
			inscriptible.delayBeforeFadeIn = Float
			.parseFloat(_DELAYBEFOREFADEIN);
		
		if (_DELAYBEFOREFADEOUT != null)
			inscriptible.delayBeforeFadeOut = Float
			.parseFloat(_DELAYBEFOREFADEOUT);
		
		if (_ISLOOPING != null)
			inscriptible.isLooping = toInt(_ISLOOPING) == 1;
		
		if (_ISUSINGPAUSE != null)
			inscriptible.isUsingPause = toInt(_ISUSINGPAUSE) == 1;
		
	}
	
	private void parseXML(MAtmosKnowledge original, Document doc,
			boolean allowOverrides) throws XPathExpressionException,
			DOMException
			{
		
		{
			NodeList cat = doc.getElementsByTagName(DYNAMIC);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Node capsule = cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLdynamic(original, capsule, nameNode.getNodeValue(),
							allowOverrides);
					
				}
				
			}
			
		}
		
		{
			NodeList cat = doc.getElementsByTagName(LIST);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Node capsule = cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLlist(original, capsule, nameNode.getNodeValue(),
							allowOverrides);
					
				}
				
			}
			
		}
		
		{
			NodeList cat = doc.getElementsByTagName(CONDITION);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Node capsule = cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLcondition(original, capsule, nameNode
							.getNodeValue(), allowOverrides);
					
				}
				
			}
			
		}
		
		{
			NodeList cat = doc.getElementsByTagName(SET);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Node capsule = cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLset(original, capsule, nameNode.getNodeValue(),
							allowOverrides);
					
				}
				
			}
			
		}
		
		{
			NodeList cat = doc.getElementsByTagName(EVENT);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Node capsule = cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLevent(original, capsule, nameNode.getNodeValue(),
							allowOverrides);
					
				}
				
			}
			
		}
		
		{
			NodeList cat = doc.getElementsByTagName(MACHINE);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Node capsule = cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLmachine(original, capsule, nameNode.getNodeValue(),
							allowOverrides);
					
				}
				
			}
			
		}
		
			}
	
	/*
	private static void parseXMLstream(MAtmosStream stream, XMLEventReader eventReader) throws XMLStreamException
	{
		while (eventReader.hasNext())
		{
			XMLEvent event = eventReader.nextEvent();
			
			if (event.isStartElement())
			{
				StartElement startElement = event.asStartElement();
				String locale = startElement.getName().getLocalPart();
				
				// !!!ATTENTION!!! stream is NOT a descriptible !
				
				if (locale == PATH)
					stream.path = pickupNextEventData(eventReader);
				
				else if (locale == VOLUME)
					stream.volume = Float.parseFloat( pickupNextEventData(eventReader) );
				
				else if (locale == PITCH)
					stream.pitch = Float.parseFloat( pickupNextEventData(eventReader) );
				
				else if (locale == FADEINTIME)
					stream.fadeInTime = Float.parseFloat( pickupNextEventData(eventReader) );
				
				else if (locale == FADEOUTTIME)
					stream.fadeOutTime = Float.parseFloat( pickupNextEventData(eventReader) );
				
				else if (locale == DELAYBEFOREFADEIN)
					stream.delayBeforeFadeIn = Float.parseFloat( pickupNextEventData(eventReader) );
				
				else if (locale == DELAYBEFOREFADEOUT)
					stream.delayBeforeFadeOut = Float.parseFloat( pickupNextEventData(eventReader) );
				
				else if (locale == ISLOOPING)
					stream.isLooping = Integer.parseInt( pickupNextEventData(eventReader) ) == 1;
				
				else if (locale == ISUSINGPAUSE)
					stream.isUsingPause = Integer.parseInt( pickupNextEventData(eventReader) ) == 1;
				
			}
			if (event.isEndElement())
			{
				EndElement endElement = event.asEndElement();
				
				if (endElement.getName().getLocalPart() == STREAM)
					return;
				
			}
			
		}
		
		return;
		
	}*/
	
}
