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

public class MAtPipelineIDAccumulator extends MAtScanCoordsPipeline
{
	private int[] tempnormal;
	private int count;
	
	private String normalName;
	private String proportionnalName;
	
	private int proportionnalTotal;
	
	MAtPipelineIDAccumulator(MAtMod modIn, MAtmosData dataIn,
			String normalNameIn, String proportionnalNameIn,
			int proportionnalTotalIn)
			{
		super(modIn, dataIn);
		tempnormal = new int[MAtDataGatherer.COUNT_WORLD_BLOCKS];
		
		normalName = normalNameIn;
		proportionnalName = proportionnalNameIn;
		proportionnalTotal = proportionnalTotalIn;
		
			}
	
	@Override
	void doBegin()
	{
		count = 0;
		for (int i = 0; i < tempnormal.length; i++)
		{
			tempnormal[i] = 0;
			
		}
		
	}
	
	@Override
	void doInput(long x, long y, long z)
	{
		int id = mod().manager().getMinecraft().theWorld.getBlockId((int) x,
				(int) y, (int) z);
		if ((id >= tempnormal.length) || (id < 0))
			return; /// Do not count
			
		tempnormal[id] = tempnormal[id] + 1;

		count++;
		
	}
	
	@Override
	void doFinish()
	{
		ArrayList<Integer> normal = null;
		ArrayList<Integer> proportionnal = null;
		
		normal = data().sheets.get(normalName);
		
		if (proportionnalName != null)
			proportionnal = data().sheets.get(proportionnalName);
		
		for (int i = 0; i < tempnormal.length; i++)
		{
			normal.set(i, tempnormal[i]);
			
			if (proportionnalName != null)
				proportionnal
				.set(i,
						(int) (proportionnalTotal * tempnormal[i] / ((float) count)));
			
		}
		
		//data().flagUpdate(); // TODO Is this a good place to do it ?
		
	}
	
}
