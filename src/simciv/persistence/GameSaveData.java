package simciv.persistence;

import simciv.Map;

/**
 * All saveable game data to be send to the game saver/loader.
 * @author Marc
 *
 */
public class GameSaveData
{
	public Map map;
//	public Date date;
	public String saveName;
	
	public GameSaveData(String saveName)
	{
		this.saveName = saveName;
	}
	
}

