package eu.ha3.mc.convenience;

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
