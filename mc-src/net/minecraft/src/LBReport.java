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
		valid = false;
		page = 0;
		pageCount = 0;
		changedTotal = -1;
		changes = new ArrayList<LBChange>();
		
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
		return changedTotal;
		
	}
	
	public int getPage()
	{
		return page;
		
	}
	
	public int getPageCount()
	{
		return pageCount;
		
	}
	
	public boolean isValid()
	{
		return valid;
		
	}
	
	public List<LBChange> getStoredChanges()
	{
		return changes;
		
	}
	
	public void addChange(LBChange change)
	{
		changes.add(change);
		
	}
	
	private void validate()
	{
		valid = ((page != 0) && page < pageCount) && (changedTotal >= 0);
		
	}

}
