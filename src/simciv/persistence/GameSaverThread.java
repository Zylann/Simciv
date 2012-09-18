package simciv.persistence;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.newdawn.slick.util.Log;

public class GameSaverThread extends GameSaveIOThread
{
	public GameSaverThread(GameSaveData data)
	{
		super(data);
	}
	
	@Override
	public void run()
	{
//		super.run();
		save();
	}
		
	public void save()
	{
		success = false;
		
		try
		{
			FileOutputStream fos = new FileOutputStream("saves/" + data.saveName + ".ssg");
			DataOutputStream dos = new DataOutputStream(fos);
			
			data.map.writeToSave(dos);
			
			success = true;
			Log.info("Game saved.");
			
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		finished = true;
	}
	
}




