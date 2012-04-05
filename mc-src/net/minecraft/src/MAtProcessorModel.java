package net.minecraft.src;

import java.util.ArrayList;

import eu.ha3.matmos.engine.MAtmosData;


/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

public abstract class MAtProcessorModel
{
	private MAtMod mod;
	private MAtmosData data;
	
	private String normalName;
	private String deltaName;
	
	private ArrayList<Integer> normalSheet;
	private ArrayList<Integer> deltaSheet;
	
	MAtProcessorModel(MAtMod modIn, MAtmosData dataIn, String normalNameIn,
			String deltaNameIn)
			{
		mod = modIn;
		data = dataIn;
		normalName = normalNameIn;
		deltaName = deltaNameIn;
		
			}
	
	public MAtMod mod()
	{
		return mod;
		
	}
	
	public MAtmosData data()
	{
		return data;
		
	}
	
	abstract void doProcess();
	
	public void process()
	{
		normalSheet = data().sheets.get(normalName);
		if (deltaName != null)
			deltaSheet = data().sheets.get(deltaName);
		
		doProcess();
		
	}
	
	void setValue(int index, int newValue)
	{
		int previousValue = normalSheet.get(index);
		normalSheet.set(index, newValue);
		
		if (deltaName != null)
			deltaSheet.set(index, newValue - previousValue);
		
	}
	
}
