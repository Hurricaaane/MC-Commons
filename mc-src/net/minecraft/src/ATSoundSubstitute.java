package net.minecraft.src;

import java.io.File;
import java.net.MalformedURLException;

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

public class ATSoundSubstitute extends SoundPoolEntry
{
	private SoundPoolEntry original;
	
	public ATSoundSubstitute(SoundPoolEntry original, String newSoundName, File substituant)
	{
		super(original.soundName, original.soundUrl);
		
		this.original = original;
		
		try
		{
			if (substituant.exists())
			{
				this.soundName = newSoundName;
				this.soundUrl = substituant.toURI().toURL();
			}
			else
			{
				System.out.println("Tried to substitute "
					+ original.soundName + " but the file " + substituant.toString() + " does not exist!");
			}
		}
		catch (MalformedURLException e)
		{
		}
		
	}
	
	public SoundPoolEntry getOriginal()
	{
		return this.original;
	}
	
}
