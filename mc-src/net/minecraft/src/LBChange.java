package net.minecraft.src;

public class LBChange
{
	public int getInformativeRank()
	{
		return informativeRank;
	}
	
	public void setInformativeRank(int informativeRank)
	{
		this.informativeRank = informativeRank;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public void setAuthor(String author)
	{
		this.author = author;
	}
	
	public String getRawDate()
	{
		return date;
	}
	
	public void setRawDate(String date)
	{
		this.date = date;
	}

	public String getAction()
	{
		return action;
	}
	
	public void setAction(String action)
	{
		this.action = action;
	}
	
	public int getX()
	{
		return x;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public void setZ(int z)
	{
		this.z = z;
	}
	
	private int informativeRank;
	private String date;
	private String author;
	private String action;
	private int x;
	private int y;
	private int z;
	
	public LBChange()
	{
		informativeRank = -1;
		author = "";
		action = "";
		date = "";
		
	}
	
	@Override
	public String toString()
	{
		return "(" + informativeRank + ") " + author + " " + action + " at "
				+ x + ":" + y + ":" + z;
		
	}
	
}
