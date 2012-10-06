package simciv.ui;

import backend.ui.INotificationListener;

public interface IMapNotificationListener extends INotificationListener
{
	public void notify(byte type, String message, int x, int y);
	public void notify(byte type, String message, int x, int y, int timeVisible);

}
