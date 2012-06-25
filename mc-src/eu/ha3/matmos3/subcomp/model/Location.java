package eu.ha3.matmos3.subcomp.model;

public interface Location
{
	public float getX();
	
	public float getY();
	
	public float getZ();
	
	public float distanceTo(Location location);
	
	public float distanceSquared(Location location);
	
}
