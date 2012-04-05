package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

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

public class MAtDataGatherer
{
	final static String INSTANTS = "Instants";
	final static String DELTAS = "Deltas";
	final static String LARGESCAN = "LargeScan";
	final static String SMALLSCAN = "SmallScan";
	final static String LARGESCAN_THOUSAND = "LargeScanPerMil";
	final static String SMALLSCAN_THOUSAND = "SmallScanPerMil";
	final static String SPECIAL_LARGE = "SpecialLarge";
	final static String SPECIAL_SMALL = "SpecialSmall";
	final static String CONTACTSCAN = "ContactScan";
	
	final static int COUNT_WORLD_BLOCKS = 4096;
	final static int COUNT_INSTANTS = 256;
	
	final static int MAX_LARGESCAN_PASS = 10;
	
	private MAtMod mod;
	
	private MAtScanVolumetricModel largeScanner;
	private MAtScanVolumetricModel smallScanner;
	
	private MAtScanCoordsPipeline largePipeline;
	private MAtScanCoordsPipeline smallPipeline;
	
	private MAtProcessorModel relaxedProcessor;
	private MAtProcessorModel frequentProcessor;
	private MAtProcessorModel contactProcessor;
	
	private List<MAtProcessorModel> additionalRelaxedProcessors;
	private List<MAtProcessorModel> additionalFrequentProcessors;
	
	private MAtmosData data;
	
	private int cyclicTick;
	
	private long lastLargeScanX;
	private long lastLargeScanY;
	private long lastLargeScanZ;
	private int lastLargeScanPassed;
	
	MAtDataGatherer(MAtMod modIn)
	{
		mod = modIn;
		
	}
	
	void resetRegulators()
	{
		lastLargeScanPassed = MAX_LARGESCAN_PASS;
		cyclicTick = 0;
	}
	
	void load()
	{
		resetRegulators();
		
		largeScanner = new MAtScanVolumetricModel(mod);
		smallScanner = new MAtScanVolumetricModel(mod);
		
		additionalRelaxedProcessors = new ArrayList<MAtProcessorModel>();
		additionalFrequentProcessors = new ArrayList<MAtProcessorModel>();
		
		data = new MAtmosData();
		prepareSheets();
		
		largePipeline = new MAtPipelineIDAccumulator(mod, data, LARGESCAN,
				LARGESCAN_THOUSAND, 1000);
		smallPipeline = new MAtPipelineIDAccumulator(mod, data, SMALLSCAN,
				SMALLSCAN_THOUSAND, 1000);
		
		largeScanner.setPipeline(largePipeline);
		smallScanner.setPipeline(smallPipeline);
		
		relaxedProcessor = new MAtProcessorRelaxed(mod, data, INSTANTS, DELTAS);
		frequentProcessor = new MAtProcessorFrequent(mod, data, INSTANTS,
				DELTAS);
		contactProcessor = new MAtProcessorContact(mod, data, CONTACTSCAN, null);
		
	}
	
	public MAtmosData getData()
	{
		return data;
		
	}
	
	void tickRoutine()
	{
		if (cyclicTick % 64 == 0)
		{
			EntityPlayer player = mod.manager().getMinecraft().thePlayer;
			long x = (long) Math.floor(player.posX);
			long y = (long) Math.floor(player.posY);
			long z = (long) Math.floor(player.posZ);
			
			if (cyclicTick == 0)
			{
				if ((lastLargeScanPassed >= MAX_LARGESCAN_PASS)
						|| (Math.abs(x - lastLargeScanX) > 16)
						|| (Math.abs(y - lastLargeScanY) > 8)
						|| (Math.abs(z - lastLargeScanZ) > 16))
				{
					lastLargeScanX = x;
					lastLargeScanY = y;
					lastLargeScanZ = z;
					lastLargeScanPassed = 0;
					largeScanner.startScan(x, y, z, 64, 32, 64, 8192, null);
					
				}
				else
				{
					lastLargeScanPassed++;
					
				}
				
				
			}
			smallScanner.startScan(x, y, z, 16, 8, 16, 2048, null);
			relaxedProcessor.process();
			
			for (MAtProcessorModel processor : additionalRelaxedProcessors)
				processor.process();
			
			data.flagUpdate();
			
		}
		if (cyclicTick % 1 == 0) // XXX
		{
			contactProcessor.process();
			frequentProcessor.process();
			
			for (MAtProcessorModel processor : additionalFrequentProcessors)
				processor.process();
			
			data.flagUpdate();
			
		}
		
		largeScanner.routine();
		smallScanner.routine();
		
		cyclicTick = (cyclicTick + 1) % 256;
		
	}
	
	void prepareSheets()
	{
		createSheet(LARGESCAN, COUNT_WORLD_BLOCKS);
		createSheet(LARGESCAN_THOUSAND, COUNT_WORLD_BLOCKS);
		
		createSheet(SMALLSCAN, COUNT_WORLD_BLOCKS);
		createSheet(SMALLSCAN_THOUSAND, COUNT_WORLD_BLOCKS);
		
		createSheet(CONTACTSCAN, COUNT_WORLD_BLOCKS);
		
		createSheet(INSTANTS, COUNT_INSTANTS);
		createSheet(DELTAS, COUNT_INSTANTS);
		
		createSheet(SPECIAL_LARGE, 2);
		createSheet(SPECIAL_SMALL, 1);
		
	}
	
	void createSheet(String name, int count)
	{
		ArrayList<Integer> array = new ArrayList<Integer>();
		data.sheets.put(name, array);
		for (int i = 0; i < count; i++)
		{
			array.add(0);
			
		}
		
	}
	
}
