package net.minecraft.src;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;

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

public class NavstrateGatherer extends Thread
{
	NavstrateData navData;
	ArrayList<NavstrateCoord> stockCoords;
	
	mod_Navstrate caller;
	int limit;
	
	int sleepTime = 100;
	
	public void setCaller(mod_Navstrate in)
	{
		caller = in;
		
	}
	
	public void setSleepTime(int in)
	{
		sleepTime = in;
		
	}
	
	public void prepareAnalysis(NavstrateData data, int xPos, int yPos, int zPos, int stepLimit)
	{
		navData = data;
		data.setPos(xPos, yPos, zPos);
		data.emptyExploration();
		
		limit = stepLimit;
		
		stockCoords = new ArrayList<NavstrateCoord>();
		
	}
	
	NavstrateCoord obtainCoord(int x, int y, int z)
	{
		NavstrateCoord ret;
		if (stockCoords.isEmpty())
		{
			ret = new NavstrateCoord(x, y, z);
		}
		else
		{
			ret = stockCoords.remove(0);
			ret.set(x, y, z);
		}
		return ret;
		
	}
	
	void returnCoord(NavstrateCoord coord)
	{
		stockCoords.add(coord);
		
	}
	
	@Override
	public void run()
	{
		if (caller == null)
			return;
		
		if (navData == null)
			return;
		
		Minecraft mc = ModLoader.getMinecraftInstance();
		
		int worldXcorner = navData.xPos - navData.xSize / 2;
		int worldYcorner = navData.yPos - navData.ySize / 2;
		int worldZcorner = navData.zPos - navData.zSize / 2;
		
		ArrayList<NavstrateCoord> toFind = new ArrayList<NavstrateCoord>();
		ArrayList<NavstrateCoord> toFindNext = new ArrayList<NavstrateCoord>();
		ArrayList<NavstrateCoord> coordSwap;
		
		toFindNext.add(obtainCoord(navData.xSize / 2, navData.ySize / 2, navData.zSize / 2));
		
		int step = 0;
		while (!toFindNext.isEmpty() && step < limit)
		{
			coordSwap = toFind;
			toFind = toFindNext;
			toFindNext = coordSwap;
			step++;
			
			try
			{
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException e)
			{
				//e.printStackTrace();
			}
			
			while (!toFind.isEmpty())
			{
				NavstrateCoord coord = toFind.remove(0);
				int i = coord.i;
				int j = coord.j;
				int k = coord.k;
				returnCoord(coord);
				
				int iot = i + worldXcorner;
				int jot = j + worldYcorner;
				int kot = k + worldZcorner;
				
				//System.out.println(iot + " " + kot + " " + navData.xPos + " " + navData.zPos);
				
				//navData.explored[i][j][k] = true;
				
				//TODO : Exceptions for custom height limit
				if (jot >= 0 && jot < 256
						&& !mc.theWorld.isBlockOpaqueCube(iot, jot, kot))
				{
					navData.data[i][j][k] = step;
					
					if (navData.isValidPos(i - 1, j, k) && !navData.isExplored(i - 1, j, k))
					{
						toFindNext.add(obtainCoord(i - 1, j, k));
						navData.setExplored(i - 1, j, k);
					}
					if (navData.isValidPos(i + 1, j, k) && !navData.isExplored(i + 1, j, k))
					{
						toFindNext.add(obtainCoord(i + 1, j, k));
						navData.setExplored(i + 1, j, k);
					}
					
					if (navData.isValidPos(i, j - 1, k) && !navData.isExplored(i, j - 1, k))
					{
						toFindNext.add(obtainCoord(i, j - 1, k));
						navData.setExplored(i, j - 1, k);
					}
					if (navData.isValidPos(i, j + 1, k) && !navData.isExplored(i, j + 1, k))
					{
						toFindNext.add(obtainCoord(i, j + 1, k));
						navData.setExplored(i, j + 1, k);
					}
					
					if (navData.isValidPos(i, j, k - 1) && !navData.isExplored(i, j, k - 1))
					{
						toFindNext.add(obtainCoord(i, j, k - 1));
						navData.setExplored(i, j, k - 1);
					}
					if (navData.isValidPos(i, j, k + 1) && !navData.isExplored(i, j, k + 1))
					{
						toFindNext.add(obtainCoord(i, j, k + 1));
						navData.setExplored(i, j, k + 1);
					}
					
				}
				else
				{
					navData.data[i][j][k] = -step;
					
				}
				
			}
			
		}
		
		caller.finishSnapshot();
		
	}
	
}
