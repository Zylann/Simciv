package simciv.persistence;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import simciv.Map;

public class GameLoaderThread extends GameSaveIOThread
{
	public GameLoaderThread(GameSaveData data)
	{
		super(data);
	}

	@Override
	public void run()
	{
//		super.run();
		load();
	}
	
	public void load()
	{
		success = false;
		
		try
		{
			FileInputStream fis = new FileInputStream("saves/" + data.saveName + ".ssg");
			DataInputStream dis = new DataInputStream(fis);
			
			data.map = new Map(1, 1);
			data.map.readFromSave(dis);
			
			success = true;
			
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		finished = true;
	}

}


