package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ListIterator;

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

public class NavstrateData
{
	int xSize;
	int ySize;
	int zSize;
	
	int xPos;
	int yPos;
	int zPos;
	
	public int data[][][];
	public boolean explored[][][];
	
	public ArrayList<Integer> bufferedImageList;
	public ArrayList<Integer[]> intArrayList;
	public Integer[] intArraySwapper;
	
	NavstrateData(int xS, int yS, int zS)
	{
		Minecraft mc = ModLoader.getMinecraftInstance();
		
		xSize = xS;
		ySize = yS;
		zSize = zS;
		
		xPos = 0;
		yPos = 0;
		zPos = 0;
		
		emptyMemory();
		bufferedImageList = new ArrayList<Integer>();
		intArrayList = new ArrayList<Integer[]>();
		
		for (int r = 0; r < 3; r++)
		{
			bufferedImageList.add(mc.renderEngine.allocateAndSetupTexture(new BufferedImage(xSize, zSize, 2)));
			intArrayList.add(new Integer[xSize * zSize]);
			
		}
		intArraySwapper = new Integer[xSize * zSize];
		
	}
	
	void emptyMemory()
	{
		data = new int[xSize][ySize][zSize];
		emptyExploration();
		
	}
	
	void shiftData(int iShift, int jShift, int kShift)
	{
		int newData[][][] = new int[xSize][ySize][zSize];
		for (int i = 0; i < xSize; i++)
			for (int j = 0; j < ySize; j++)
				for (int k = 0; k < zSize; k++)
				{
					if (isValidPos(i + iShift, j + jShift, k + kShift))
						newData[i][j][k] = getData(i + iShift, j + jShift, k + kShift);
					
				}
		data = newData;
		
		shiftImageBuffers(iShift, kShift);
		
	}
	void emptyExploration()
	{
		explored = new boolean[xSize][ySize][zSize];
		
	}
	
	void setPos(int x, int y, int z)
	{
		xPos = x;
		yPos = y;
		zPos = z;
		
	}
	
	boolean isValidPos(int x, int y, int z)
	{
		return (x >= 0) && (x < xSize) && (y >= 0) && (y < ySize) && (z >= 0) && (z < zSize);
		
	}
	
	boolean isExplored(int x, int y, int z)
	{
		//Unsafe call
		return explored[x][y][z];
		
	}
	
	void setExplored(int x, int y, int z)
	{
		//Unsafe call
		explored[x][y][z] = true;
		
	}
	
	int getData(int x, int y, int z)
	{
		//Unsafe call
		return data[x][y][z];
		
	}
	
	private void shiftImageBuffers(int iShift, int kShift)
	{
		for (ListIterator<Integer[]> iter = intArrayList.listIterator(); iter.hasNext();)
		{
			Integer[] oldArray = iter.next();
			Integer[] newArray = intArraySwapper;
			iter.set(newArray);
			intArraySwapper = oldArray;
			
			try
			{
				for (int k = 0; k < zSize; k++)
				{
					if ((k < kShift) || ((kShift + k) > zSize))
						for (int i = 0; i < xSize; i++)
							newArray[k * xSize + i] = 0;
					else
						for (int i = 0; i < xSize; i++)
						{
							if ((i < iShift) || ((iShift + i) > xSize))
								newArray[k * xSize + i] = 0;
							
							else
							{
								newArray[k * xSize + i] = oldArray[(k - kShift)
								                                   * xSize + (i - iShift)];
								
							}
							
						}
					
				}
			}
			catch (Exception e)
			{
				//System.out.println(iShift);
				//System.out.println(kShift);
			}
			
		}
		
	}
	
}
