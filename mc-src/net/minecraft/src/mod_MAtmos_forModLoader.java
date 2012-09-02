package net.minecraft.src;

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

public class mod_MAtmos_forModLoader extends HaddonBridgeModLoader
{
	public mod_MAtmos_forModLoader()
	{
		super(new MAtMod());
		
	}
	
	@Override
	public String getVersion()
	{
		return "r16 for 1.3.2"; // Remember to change the thing on MAtMod
		
	}
	
}