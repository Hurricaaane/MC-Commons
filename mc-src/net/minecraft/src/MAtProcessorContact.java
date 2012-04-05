package net.minecraft.src;

import net.minecraft.client.Minecraft;
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

public class MAtProcessorContact extends MAtProcessorModel
{
	private int contactSum[];
	
	MAtProcessorContact(MAtMod modIn, MAtmosData dataIn, String normalNameIn,
			String deltaNameIn)
			{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		contactSum = new int[MAtDataGatherer.COUNT_WORLD_BLOCKS];
		
			}
	
	private void emptyContact()
	{
		for (int i = 0; i < contactSum.length; i++)
			contactSum[i] = 0;
		
	}
	
	@Override
	void doProcess()
	{
		Minecraft mc = mod().manager().getMinecraft();
		int x = (int) Math.floor(mc.thePlayer.posX);
		int y = (int) Math.floor(mc.thePlayer.posY) - 1; //FIXME: Player position is different from Half Life, this is fixed with a -1
		int z = (int) Math.floor(mc.thePlayer.posZ);
		
		int nx;
		int ny;
		int nz;
		
		emptyContact();
		
		for (int k = 0; k < 12; k++)
		{
			ny = y + (k > 7 ? k - 9 : k % 2);
			if (ny >= 0 && ny < mod().corn().util().getWorldHeight())
			{
				nx = x + (k < 4 ? (k < 2 ? -1 : 1) : 0);
				nz = z + ((k > 3) && (k < 8) ? (k < 6 ? -1 : 1) : 0);
				
				int id = mod().manager().getMinecraft().theWorld.getBlockId(nx,
						ny, nz);
				if ((id < contactSum.length) || (id >= 0))
					contactSum[id] = contactSum[id] + 1;
				
			}
			
		}
		
		for (int i = 0; i < contactSum.length; i++)
			setValue(i, contactSum[i]);
		
	}
	
}
