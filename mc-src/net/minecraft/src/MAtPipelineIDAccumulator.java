package net.minecraft.src;

import java.util.ArrayList;

import eu.ha3.matmos.engine.MAtmosData;

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

public class MAtPipelineIDAccumulator extends MAtScanCoordsPipeline
{
	private int[] tempnormal;
	private int count;
	
	private String normalName;
	private String proportionnalName;
	
	private int proportionnalTotal;
	
	MAtPipelineIDAccumulator(MAtMod mod, MAtmosData dataIn,
			String normalNameIn, String proportionnalNameIn,
			int proportionnalTotalIn)
			{
		super(mod, dataIn);
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
