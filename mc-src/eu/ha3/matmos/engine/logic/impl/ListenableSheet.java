package eu.ha3.matmos.engine.logic.impl;

import java.util.Set;

import eu.ha3.matmos.engine.logic.Sheet;
import eu.ha3.matmos.engine.logic.UpdateListener;

public abstract class ListenableSheet implements Sheet
{
	protected Set<UpdateListener> updateListeners;
	
	@Override
	public void signalUpdate()
	{
		for (UpdateListener listener : updateListeners)
			listener.updateEvent();
		
	}
	
	@Override
	public void addUpdateListener(UpdateListener listener)
	{
		updateListeners.add(listener);
		
	}
	
	@Override
	public void removeUpdateListener(UpdateListener listener)
	{
		updateListeners.remove(listener);
		
	}
	
}
