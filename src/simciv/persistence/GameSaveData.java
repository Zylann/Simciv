package simciv.persistence;

import java.io.File;

import simciv.Map;

/**
 * All saveable game data to be send to the game saver/loader.
 * @author Marc
 *
 */
public class GameSaveData
{
	public static final String SAVES_DIR = "saves";
	public static final String SAVES_EXT = ".ssg";
	
	public Map map;
//	public Date date;
	public String saveName;
	
	public GameSaveData(String saveName)
	{
		this.saveName = saveName;
	}
	
	/**
	 * Checks if there is save files in the saves directory
	 * @return
	 */
	public static boolean isSaveFiles()
	{
		boolean isFiles = false;
		
		File dir = new File(SAVES_DIR);
		
		String files[] = dir.list();
		
		for(String file : files)
		{
			if(file.endsWith(SAVES_EXT))
			{
				isFiles = true;
				break;
			}
		}
		
		return isFiles;
	}
	
}

