package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
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

public class MAtProcessorRelaxed extends MAtProcessorModel
{
	private Map<String, Integer> biomeHash;
	private Random random;
	
	MAtProcessorRelaxed(MAtMod modIn, MAtmosData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		
		this.biomeHash = new HashMap<String, Integer>();
		//biomeHash.put("Rainforest", 1);
		this.biomeHash.put("Swampland", 2);
		//biomeHash.put("Seasonal Forest", 3);
		this.biomeHash.put("Forest", 4);
		//biomeHash.put("Savanna", 5);
		//biomeHash.put("Shrubland", 6);
		this.biomeHash.put("Taiga", 7);
		this.biomeHash.put("Desert", 8);
		this.biomeHash.put("Plains", 9);
		//biomeHash.put("Ice Desert", 10);
		//biomeHash.put("Tundra", 11);
		this.biomeHash.put("Hell", 12);
		this.biomeHash.put("Sky", 13);
		this.biomeHash.put("Ocean", 14);
		this.biomeHash.put("Extreme Hills", 15);
		this.biomeHash.put("River", 16);
		this.biomeHash.put("FrozenOcean", 17);
		this.biomeHash.put("FrozenRiver", 18);
		this.biomeHash.put("Ice Plains", 19);
		this.biomeHash.put("Ice Mountains", 20);
		this.biomeHash.put("MushroomIsland", 21);
		this.biomeHash.put("MushroomIslandShore", 22);
		this.biomeHash.put("Beach", 23);
		this.biomeHash.put("DesertHills", 24);
		this.biomeHash.put("ForestHills", 25);
		this.biomeHash.put("TaigaHills", 26);
		this.biomeHash.put("Extreme Hills Edge", 27);
		this.biomeHash.put("Jungle", 28);
		this.biomeHash.put("JungleHills", 29);
		
		this.random = new Random(System.nanoTime());
		
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
		
		Integer biomeInt = this.biomeHash.get(w.getWorldChunkManager().getBiomeGenAt(x, z).biomeName);
		
		if (biomeInt == null)
		{
			biomeInt = -1;
		}
		
		setValue(5, worldinfo.getDimension());
		
		setValue(12, w.isRemote ? 1 : 0);
		setValue(13, 1 + this.random.nextInt(100)); // DICE A
		setValue(14, 1 + this.random.nextInt(100)); // DICE B
		setValue(15, 1 + this.random.nextInt(100)); // DICE C
		setValue(16, 1 + this.random.nextInt(100)); // DICE D
		setValue(17, 1 + this.random.nextInt(100)); // DICE E
		setValue(18, 1 + this.random.nextInt(100)); // DICE F
		
		setValue(29, biomeInt);
		setValue(30, (int) (w.getSeed() >> 32));
		setValue(31, (int) (w.getSeed() & 0xFFFFFFFF));
		
	}
	
}
