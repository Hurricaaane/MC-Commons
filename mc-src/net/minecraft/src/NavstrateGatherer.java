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
		this.caller = in;
		
	}
	
	public void setSleepTime(int in)
	{
		this.sleepTime = in;
		
	}
	
	public void prepareAnalysis(NavstrateData data, int xPos, int yPos, int zPos, int stepLimit)
	{
		this.navData = data;
		data.setPos(xPos, yPos, zPos);
		data.emptyExploration();
		
		this.limit = stepLimit;
		
		this.stockCoords = new ArrayList<NavstrateCoord>();
		
	}
	
	NavstrateCoord obtainCoord(int x, int y, int z)
	{
		NavstrateCoord ret;
		if (this.stockCoords.isEmpty())
		{
			ret = new NavstrateCoord(x, y, z);
		}
		else
		{
			ret = this.stockCoords.remove(0);
			ret.set(x, y, z);
		}
		return ret;
		
	}
	
	void returnCoord(NavstrateCoord coord)
	{
		this.stockCoords.add(coord);
		
	}
	
	@Override
	public void run()
	{
		if (this.caller == null)
			return;
		
		if (this.navData == null)
			return;
		
		Minecraft mc = ModLoader.getMinecraftInstance();
		
		int worldXcorner = this.navData.xPos - this.navData.xSize / 2;
		int worldYcorner = this.navData.yPos - this.navData.ySize / 2;
		int worldZcorner = this.navData.zPos - this.navData.zSize / 2;
		
		ArrayList<NavstrateCoord> toFind = new ArrayList<NavstrateCoord>();
		ArrayList<NavstrateCoord> toFindNext = new ArrayList<NavstrateCoord>();
		ArrayList<NavstrateCoord> coordSwap;
		
		toFindNext.add(obtainCoord(this.navData.xSize / 2, this.navData.ySize / 2, this.navData.zSize / 2));
		
		int step = 0;
		while (!toFindNext.isEmpty() && step < this.limit)
		{
			coordSwap = toFind;
			toFind = toFindNext;
			toFindNext = coordSwap;
			step++;
			
			try
			{
				Thread.sleep(this.sleepTime);
			}
			catch (InterruptedException e)
			{
				//e.printStackTrace();
			}
			
			for (NavstrateCoord coord : toFind)
			{
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
				if (jot >= 0 && jot < 256 && !mc.theWorld.isBlockOpaqueCube(iot, jot, kot))
				{
					this.navData.data[i][j][k] = step;
					
					if (this.navData.isValidPos(i - 1, j, k) && !this.navData.isExplored(i - 1, j, k))
					{
						toFindNext.add(obtainCoord(i - 1, j, k));
						this.navData.setExplored(i - 1, j, k);
					}
					if (this.navData.isValidPos(i + 1, j, k) && !this.navData.isExplored(i + 1, j, k))
					{
						toFindNext.add(obtainCoord(i + 1, j, k));
						this.navData.setExplored(i + 1, j, k);
					}
					
					if (this.navData.isValidPos(i, j - 1, k) && !this.navData.isExplored(i, j - 1, k))
					{
						toFindNext.add(obtainCoord(i, j - 1, k));
						this.navData.setExplored(i, j - 1, k);
					}
					if (this.navData.isValidPos(i, j + 1, k) && !this.navData.isExplored(i, j + 1, k))
					{
						toFindNext.add(obtainCoord(i, j + 1, k));
						this.navData.setExplored(i, j + 1, k);
					}
					
					if (this.navData.isValidPos(i, j, k - 1) && !this.navData.isExplored(i, j, k - 1))
					{
						toFindNext.add(obtainCoord(i, j, k - 1));
						this.navData.setExplored(i, j, k - 1);
					}
					if (this.navData.isValidPos(i, j, k + 1) && !this.navData.isExplored(i, j, k + 1))
					{
						toFindNext.add(obtainCoord(i, j, k + 1));
						this.navData.setExplored(i, j, k + 1);
					}
					
				}
				else
				{
					this.navData.data[i][j][k] = -step;
					
				}
				
			}
			toFind.clear();
			
		}
		
		this.caller.finishSnapshot();
		
	}
	
}
