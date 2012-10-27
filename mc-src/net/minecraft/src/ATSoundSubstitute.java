package net.minecraft.src;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Random;

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
				// Why is there a random bullcrap here?
				// For some reason, sometimes sounds get somehow cached somewhere I'm
				// not sure of. This random bullcrap prevents cached sounds of previous
				// loading sequences to replace this one
				this.soundName = new Random().nextInt(99999) + newSoundName;
				this.soundUrl = substituant.toURI().toURL();
			}
			else
			{
				System.out.println("(ATS) Tried to substitute "
					+ original.soundName + " but the file " + substituant.toString() + " does not exist!");
			}
		}
		catch (MalformedURLException e)
		{
		}
		
	}
	
	public SoundPoolEntry getOriginal()
	{
		if (this.original instanceof ATSoundSubstitute)
		{
			System.out.println("(ATSS) Nesting occured with "
				+ this.soundUrl.toString() + " / " + this.original.soundUrl.toString() + " !");
			return ((ATSoundSubstitute) this.original).getOriginal();
			
		}
		return this.original;
	}
	
}
