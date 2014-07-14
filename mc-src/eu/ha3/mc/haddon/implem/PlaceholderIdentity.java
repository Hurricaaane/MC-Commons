package eu.ha3.mc.haddon.implem;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Identity;

/*
--filenotes-placeholder
*/

public class PlaceholderIdentity extends HaddonIdentity implements Identity
{
	public PlaceholderIdentity(Haddon haddon)
	{
		super(haddon.getClass().getSimpleName(), 0, "?.?.?", "http://example.org/");
	}
}
