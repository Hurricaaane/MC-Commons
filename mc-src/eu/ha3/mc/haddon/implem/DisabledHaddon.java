package eu.ha3.mc.haddon.implem;

/* x-placeholder-wtfplv2 */

public class DisabledHaddon extends HaddonImpl
{
	private String name = "DisabledHaddon";
	private String version = "(Disabled)";
	
	public DisabledHaddon()
	{
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		if (stack.length > 0)
		{
			this.name = stack[stack.length - 1].getClassName() + " " + stack[stack.length - 1].getMethodName();
		}
	}
	
	@Override
	public void onLoad()
	{
	}
	
	@Override
	public String getHaddonName()
	{
		return this.name;
	}
	
	@Override
	public String getHaddonVersion()
	{
		return this.version;
	}
	
}
