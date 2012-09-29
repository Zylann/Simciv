package backend.ui;

public interface INotificationListener
{
	public void notify(byte type, String message);
	public void notify(byte type, String message, int timeVisible);
	
}

