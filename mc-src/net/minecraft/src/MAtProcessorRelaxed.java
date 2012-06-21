package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

public class MAtProcessorRelaxed extends MAtProcessorModel
{
	private Map<String, Integer> biomeHash;
	private Random random;
	
	MAtProcessorRelaxed(MAtMod modIn, MAtmosData dataIn,
			String normalNameIn,
			String deltaNameIn)
			{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		
		biomeHash = new HashMap<String, Integer>();
		//biomeHash.put("Rainforest", 1);
		biomeHash.put("Swampland", 2);
		//biomeHash.put("Seasonal Forest", 3);
		biomeHash.put("Forest", 4);
		//biomeHash.put("Savanna", 5);
		//biomeHash.put("Shrubland", 6);
		biomeHash.put("Taiga", 7);
		biomeHash.put("Desert", 8);
		biomeHash.put("Plains", 9);
		//biomeHash.put("Ice Desert", 10);
		//biomeHash.put("Tundra", 11);
		biomeHash.put("Hell", 12);
		biomeHash.put("Sky", 13);
		biomeHash.put("Ocean", 14);
		biomeHash.put("Extreme Hills", 15);
		biomeHash.put("River", 16);
		biomeHash.put("FrozenOcean", 17);
		biomeHash.put("FrozenRiver", 18);
		biomeHash.put("Ice Plains", 19);
		biomeHash.put("Ice Mountains", 20);
		biomeHash.put("MushroomIsland", 21);
		biomeHash.put("MushroomIslandShore", 22);
		biomeHash.put("Beach", 23);
		biomeHash.put("DesertHills", 24);
		biomeHash.put("ForestHills", 25);
		biomeHash.put("TaigaHills", 26);
		biomeHash.put("Extreme Hills Edge", 27);
		biomeHash.put("Jungle", 28);
		biomeHash.put("JungleHills", 29);
		
		random = new Random(System.nanoTime());
		
			}
	
	@Override
	void doProcess()
	{
		Minecraft mc = mod().manager().getMinecraft();
		World w = mc.theWorld;
		WorldInfo worldinfo = w.worldInfo;
		EntityPlayerSP player = mc.thePlayer;
		
		int x = (int) Math.floor(player.posX);
		int z = (int) Math.floor(player.posZ);
		
		Integer biomeInt = biomeHash.get(w.getWorldChunkManager()
				.getBiomeGenAt(x, z).biomeName);
		
		if (biomeInt == null)
			biomeInt = -1;
		
		setValue(5, worldinfo.getDimension());
		
		setValue(12, (w.isRemote ? 1 : 0));
		setValue(13, 1 + random.nextInt(100)); // DICE A
		setValue(14, 1 + random.nextInt(100)); // DICE B
		setValue(15, 1 + random.nextInt(100)); // DICE C
		setValue(16, 1 + random.nextInt(100)); // DICE D
		setValue(17, 1 + random.nextInt(100)); // DICE E
		setValue(18, 1 + random.nextInt(100)); // DICE F
		
		setValue(29, biomeInt);
		setValue(30, (int) (w.getSeed() >> 32));
		setValue(31, (int) (w.getSeed() & 0xFFFFFFFF));
		
	}
	
}
