package simciv.persistence;

public abstract class GameSaveIOThread extends Thread
{
	protected GameSaveData data;
	protected boolean finished;
	protected boolean success;
	
	public GameSaveIOThread(GameSaveData data)
	{
		this.data = data;
	}

	public boolean isSuccess()
	{
		return success;
	}
	
	public boolean isFinished()
	{
		return finished;
	}

}
