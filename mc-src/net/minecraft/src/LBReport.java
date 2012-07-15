package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

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

public class LBReport
{
	private boolean valid;
	
	private int page;
	private int pageCount;
	private int changedTotal;
	
	private List<LBChange> changes;
	
	public LBReport()
	{
		this.valid = false;
		this.page = 0;
		this.pageCount = 0;
		this.changedTotal = -1;
		this.changes = new ArrayList<LBChange>();
		
	}
	
	public void setPageData(int page, int pageCount)
	{
		this.page = page;
		this.pageCount = pageCount;
		validate();
		
	}
	
	public void setChangedTotal(int changedTotal)
	{
		this.changedTotal = changedTotal;
		validate();
		
	}
	
	public int getChangedTotal()
	{
		return this.changedTotal;
		
	}
	
	public int getPage()
	{
		return this.page;
		
	}
	
	public int getPageCount()
	{
		return this.pageCount;
		
	}
	
	public boolean isValid()
	{
		return this.valid;
		
	}
	
	public List<LBChange> getStoredChanges()
	{
		return this.changes;
		
	}
	
	public void addChange(LBChange change)
	{
		this.changes.add(change);
		
	}
	
	private void validate()
	{
		this.valid = this.page != 0 && this.page < this.pageCount && this.changedTotal >= 0;
		
	}
	
}
