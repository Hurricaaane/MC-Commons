package eu.ha3.matmos.engine;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

public class MAtmosCondition extends MAtmosSwitchable
{
	String sheet;
	int key;
	String dynamicKey;
	int conditionType;
	int constant;
	String list;
	
	boolean isTrueEvaluated;
	
	//MAtmosCondition(MAtmosKnowledge knowledgeIn, String sheetIn, int keyIn, String symbolIn, float constantIn)
	MAtmosCondition(MAtmosKnowledge knowledgeIn)
	{
		super(knowledgeIn);
		
		//sheet = sheetIn;
		//key = keyIn;
		
		//dynamicKey = "";
		
		//setSymbol(symbolIn);
		//constant = constantIn;
		
		sheet = "";
		key = 0;
		dynamicKey = "";
		conditionType = 0;
		list = "";
		
	}
	
	/*MAtmosCondition(MAtmosKnowledge knowledgeIn, String dynamic, String symbolIn, float constantIn)
	{
		super(knowledgeIn);
		
		sheet = "";
		key = 0;
		
		setDynamic(dynamic);
		
		setSymbol(symbolIn);
		constant = constantIn;
		
	}*/
	
	public void setSheet(String sheetIn)
	{
		sheet = sheetIn;
		flagNeedsTesting();
		
	}
	
	public void setKey(int keyIn)
	{
		key = keyIn;
		flagNeedsTesting();
		
	}
	
	public void setDynamic(String dynamicKeyIn)
	{
		key = -1;
		dynamicKey = dynamicKeyIn;
		sheet = "";
		flagNeedsTesting();
		
	}
	
	public void setSymbol(String symbol)
	{
		conditionType = -1;
		
		if (symbol.equals("!="))
			conditionType = 0;
		
		else if (symbol.equals("=="))
			conditionType = 1;
		
		else if (symbol.equals(">"))
			conditionType = 2;
		
		else if (symbol.equals(">="))
			conditionType = 3;
		
		else if (symbol.equals("<"))
			conditionType = 4;
		
		else if (symbol.equals("<="))
			conditionType = 5;
		
		else if (symbol.equals("in"))
			conditionType = 6;
		
		else if (symbol.equals("!in"))
			conditionType = 7;
		
		flagNeedsTesting();
		
	}
	
	public void setConstant(int constantIn)
	{
		constant = constantIn;
		flagNeedsTesting(); // Not required.
		
	}
	
	public void setList(String listIn)
	{
		list = listIn;
		flagNeedsTesting(); // Required.
		
	}
	
	public boolean isDynamic()
	{
		return key == -1;
		
	}
	
	public String getSheet()
	{
		return sheet;
		
	}
	
	public int getKey()
	{
		return key;
		
	}
	
	public String getDynamic()
	{
		return dynamicKey;
		
	}
	
	public String getList()
	{
		return list;
		
	}
	
	public int getConditionType()
	{
		return conditionType;
		
	}
	
	public int getConstant()
	{
		return constant;
		
	}
	
	@Override
	protected boolean testIfValid()
	{
		if (conditionType == -1)
			return false;
		
		boolean valid = false;
		if (!isDynamic())
		{
			if (knowledge.data.sheets.containsKey(sheet))
			{
				if ((key >= 0) && (key < knowledge.data.sheets.get(sheet).size()))
				{
					valid = true;
					
				}
				
			}
			
		}
		else
		{
			if (knowledge.dynamics.containsKey(dynamicKey))
			{
				valid = true;
				
			}
			
		}
		if (valid && ((conditionType == 6) || (conditionType == 7)))
		{
			valid = knowledge.lists.containsKey(list);
			
		}
		
		return valid;
		
	}
	
	public boolean evaluate()
	{
		if (!isValid())
			return false;
		
		boolean pre = isTrueEvaluated;
		isTrueEvaluated = testIfTrue();
		
		if (pre != isTrueEvaluated)
		{
			//MAtmosEngine.logger; //TODO Logger
			MAtmosLogger.LOGGER.finer(new StringBuilder("C:").append(
					nickname)
					.append(isTrueEvaluated ? " now On." : " now Off.")
					.toString());
			
		}
		
		return isTrueEvaluated;
		
	}
	
	@Override
	public boolean isActive()
	{
		return isTrue();
		
	}
	
	public boolean isTrue()
	{
		return isTrueEvaluated;
		
	}
	
	public boolean testIfTrue()
	{
		if (!isValid())
			return false;
		
		int gotValue;
		
		if (!isDynamic()) //Is Not Dynamic
			gotValue = knowledge.data.sheets.get(sheet).get(key);
		
		else
			gotValue = knowledge.dynamics.get(dynamicKey).value;
		
		if (conditionType == 0)
			return gotValue != constant;
		
		else if (conditionType == 1)
			return gotValue == constant;
		
		else if (conditionType == 2)
			return gotValue > constant;
			
			else if (conditionType == 3)
				return gotValue >= constant;
				
				else if (conditionType == 4)
					return gotValue < constant;
		
				else if (conditionType == 5)
					return gotValue <= constant;
		
				else if (conditionType == 6)
					return knowledge.lists.get(list).contains(gotValue);
		
				else if (conditionType == 7)
					return !knowledge.lists.get(list).contains(gotValue);
		
				else
					return false;
		
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);
		
		if (!isDynamic())
		{
			createNode(eventWriter, "sheet", sheet);
			createNode(eventWriter, "key", "" + key);
			
		}
		else
		{
			createNode(eventWriter, "key", "" + key);
			createNode(eventWriter, "dynamickey", dynamicKey);
			
		}
		
		if (conditionType == 0)
			createNode(eventWriter, "symbol", "!=");
		
		else if (conditionType == 1)
			createNode(eventWriter, "symbol", "==");
		
		else if (conditionType == 2)
			createNode(eventWriter, "symbol", ">");
		
		else if (conditionType == 3)
			createNode(eventWriter, "symbol", ">=");
		
		else if (conditionType == 4)
			createNode(eventWriter, "symbol", "<");
		
		else if (conditionType == 5)
			createNode(eventWriter, "symbol", "<=");
		
		else if (conditionType == 6)
			createNode(eventWriter, "symbol", "in");
		
		else if (conditionType == 7)
			createNode(eventWriter, "symbol", "!in");
		
		else
			createNode(eventWriter, "symbol", "><"); // TODO Exceptions?
		
		createNode(eventWriter, "constant", "" + constant);
		createNode(eventWriter, "list", "" + list);
		
		return "";
	}
	
	public void replaceDynamicName(String name, String newName)
	{
		if (!isDynamic())
			return;
		
		if (dynamicKey.equals(name))
			dynamicKey = newName;
		
		flagNeedsTesting();
		
	}
	
	public void replaceListName(String name, String newName)
	{
		if (list.equals(name))
			list = newName;
		
		flagNeedsTesting();
		
	}
	
}
