package net.minecraft.src;

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

public class ATSoundCumulation extends SoundPoolEntry implements ATSoundWrapper
{
	private SoundPoolEntry generated;
	
	public ATSoundCumulation(SoundPoolEntry generated)
	{
		super(generated.soundName, generated.soundUrl);
		
		// Why is there a random bullcrap here?
		// For some reason, sometimes sounds get somehow cached somewhere I'm
		// not sure of. This random bullcrap prevents cached sounds of previous
		// loading sequences to replace this one
		this.soundName = new Random().nextInt(99999) + this.soundName;
		
		this.generated = generated;
	}
	
	public SoundPoolEntry getGenerated()
	{
		if (this.generated instanceof ATSoundCumulation)
		{
			System.out.println("(ATSO) Nesting occured with "
				+ this.soundUrl.toString() + " / " + this.generated.soundUrl.toString() + " !");
			return ((ATSoundCumulation) this.generated).getGenerated();
		}
		return this.generated;
		
	}
	
	@Override
	public SoundPoolEntry getSource()
	{
		return getGenerated();
	}
	
}
