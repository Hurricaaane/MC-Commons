package eu.ha3.mc.convenience;

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

/**
 * Simplistic edge triggered device.
 * 
 * @author Hurry
 * 
 */
public class Ha3EdgeTrigger
{
	private boolean currentState;
	private Ha3EdgeModel triggerModel;
	
	/**
	 * Creates an edge trigger with an initial state of false.
	 * 
	 * @param model
	 */
	public Ha3EdgeTrigger(Ha3EdgeModel model)
	{
		this(model, false);
		
	}
	
	/**
	 * Create an edge trigger.
	 * 
	 * @param model
	 */
	public Ha3EdgeTrigger(Ha3EdgeModel model, boolean initialState)
	{
		triggerModel = model;
		currentState = initialState;
		
	}
	
	public boolean getCurrentState()
	{
		return currentState;
		
	}
	
	/**
	 * Tell the edge trigger to evaluate with a new current state.
	 * 
	 * @param state
	 * @return
	 */
	public boolean signalState(boolean state)
	{
		if (state != currentState)
		{
			currentState = state;
			if (state)
				triggerModel.onTrueEdge();
			else
				triggerModel.onFalseEdge();
			
			return true;
			
		}
		
		return false;
		
	}
	
}
