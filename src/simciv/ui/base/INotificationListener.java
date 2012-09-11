package simciv.ui.base;

public interface INotificationListener
{
	public void notify(byte type, String message);
	public void notify(byte type, String message, int timeVisible);
	
}

