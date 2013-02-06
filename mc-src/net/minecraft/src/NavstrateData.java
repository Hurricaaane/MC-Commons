package net.minecraft.src;

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
	public int xSize;
	public int ySize;
	public int zSize;
	
	public int xPos;
	public int yPos;
	public int zPos;
	
	public int data[][][];
	private boolean explored[][][];
	
	NavstrateData(int xS, int yS, int zS)
	{
		this.xSize = xS;
		this.ySize = yS;
		this.zSize = zS;
		
		this.xPos = 0;
		this.yPos = 0;
		this.zPos = 0;
		
		emptyMemory();
	}
	
	void emptyMemory()
	{
		this.data = new int[this.xSize][this.ySize][this.zSize];
		emptyExploration();
	}
	
	void shiftData(int iShift, int jShift, int kShift)
	{
		int newData[][][] = new int[this.xSize][this.ySize][this.zSize];
		for (int i = 0; i < this.xSize; i++)
		{
			for (int j = 0; j < this.ySize; j++)
			{
				for (int k = 0; k < this.zSize; k++)
				{
					if (isValidPos(i + iShift, j + jShift, k + kShift))
					{
						newData[i][j][k] = getData(i + iShift, j + jShift, k + kShift);
					}
					
				}
			}
		}
		this.data = newData;
		
	}
	
	void emptyExploration()
	{
		this.explored = new boolean[this.xSize][this.ySize][this.zSize];
		
	}
	
	void setPos(int x, int y, int z)
	{
		this.xPos = x;
		this.yPos = y;
		this.zPos = z;
		
	}
	
	boolean isValidPos(int x, int y, int z)
	{
		return x >= 0 && x < this.xSize && y >= 0 && y < this.ySize && z >= 0 && z < this.zSize;
		
	}
	
	boolean isExplored(int x, int y, int z)
	{
		//Unsafe call
		return this.explored[x][y][z];
		
	}
	
	void setExplored(int x, int y, int z)
	{
		//Unsafe call
		this.explored[x][y][z] = true;
		
	}
	
	int getData(int x, int y, int z)
	{
		//Unsafe call
		return this.data[x][y][z];
		
	}
	
}
