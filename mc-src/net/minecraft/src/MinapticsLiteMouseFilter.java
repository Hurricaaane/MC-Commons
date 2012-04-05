package net.minecraft.src;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

public class MinapticsLiteMouseFilter extends MouseFilter
{
	private float field_22388_a;
	private float field_22387_b;
	private float field_22389_c;
	
	private boolean forceMode;
	private float forceValue;
	
	public MinapticsLiteMouseFilter()
	{
		forceMode = false;
		forceValue = 0F;
	}
	
	public void force(float value)
	{
		forceMode = true;
		forceValue = value;
		
	}
	
	public void let()
	{
		forceMode = false;
		
	}
	
	@Override
	public float func_22386_a(float f, float f1)
	{
		if (forceMode)
			f1 = forceValue;
		
		field_22388_a += f;
		f = (field_22388_a - field_22387_b) * f1;
		field_22389_c = field_22389_c + (f - field_22389_c) * 0.5F;
		if (f > 0.0F && f > field_22389_c || f < 0.0F && f < field_22389_c)
		{
			f = field_22389_c;
		}
		field_22387_b += f;
		return f;
		
	}
}