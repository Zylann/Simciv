package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.newdawn.slick.Sound;

/**
 * Sound manager. Allows to limit the number of sounds played at the same time.
 * @author Marc
 *
 */
public class SoundEngine
{
	private static SoundEngine instance;
	
	private HashMap<Sound, Integer> playingSounds;
	private int minimalDelayPerSound;

	public static SoundEngine instance()
	{
		if(instance == null)
			instance = new SoundEngine();
		return instance;
	}
	
	private SoundEngine()
	{
		playingSounds = new HashMap<Sound, Integer>();
		minimalDelayPerSound = 50;
	}
	
	public void update(int delta)
	{
		List<Sound> endedSounds = new ArrayList<Sound>();
		for(Entry<Sound, Integer> e : playingSounds.entrySet())
		{
			if(!e.getKey().playing())
				endedSounds.add(e.getKey());
			else
			{
				Integer delay = e.getValue();
				delay += delta;
			}
		}
		
		for(Sound s : endedSounds)
			playingSounds.remove(s);
	}
	
	public void play(Sound s, float pitch, float volume)
	{
		playAt(s, pitch, volume, 0, 0, 0);
	}
	
	public void playAt(Sound s, float pitch, float volume, float x, float y, float z)
	{
		if(playingSounds.containsKey(s))
		{
			Integer t = playingSounds.get(s);
			if(t >= minimalDelayPerSound)
				t = 0;
			else
				return;
		}
		s.play(pitch, volume);
		playingSounds.put(s, 0);
	}
	
}



