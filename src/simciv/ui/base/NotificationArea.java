package simciv.ui.base;

import org.newdawn.slick.SlickException;

/**
 * Notifications container
 * @author Marc
 *
 */
public class NotificationArea extends WidgetContainer implements INotificationListener
{
	private static int MAX_NOTIFICATIONS = 8;
	
	private Notification notifications[];
	
	public NotificationArea(Widget parent, int x, int y, int width)
	{
		super(parent, x, y, width, MAX_NOTIFICATIONS * Notification.HEIGHT);
		notifications = new Notification[MAX_NOTIFICATIONS];
	}

	/**
	 * Better use add(notification) to prevent mistakes
	 */
	@Override @Deprecated
	public void add(Widget child) throws SlickException
	{
		if(!Notification.class.isInstance(child))
			throw new SlickException("Cannot add a non-Notification widget to a NotificationArea");
		add((Notification)child);
	}
	
	/**
	 * Adds a new notification to the area.
	 * If the new notification is too large, it is resized.
	 * @param n : new notification
	 */
	public void add(Notification n)
	{
		// Resize if needed
		if(n.getWidth() > getWidth())
			n.setSize(getWidth(), n.getHeight());
				
		// Stacking
		int i = 0;
		for(; i < notifications.length; i++)
		{
			if(notifications[i] == null)
			{
				notifications[i] = n;
				break;
			}
		}
		if(i == notifications.length)
			return; // No room for the new notification
		
		// Positionning
		n.setPosition(0, i * Notification.HEIGHT);
		n.layout();
				
		try
		{
			super.add(n);
		} catch (SlickException e)
		{
			e.printStackTrace();
		}
	}
		
	/**
	 * Updates notifications (they can disappear after a certain time) 
	 * @param delta : time elapsed
	 */
	public void update(int delta)
	{
		for(int i = 0; i < notifications.length; i++)
		{
			if(notifications[i] != null)
			{
				notifications[i].update(delta);
				if(notifications[i].isTimeVisibleEnded())
				{
					this.remove(notifications[i]);
					notifications[i] = null;
				}
			}
		}
	}

	@Override
	public void notify(byte type, String message)
	{
		Notification n = new Notification(this, type, message);
		add(n);
	}

	@Override
	public void notify(byte type, String message, int timeVisible)
	{
		Notification n = new Notification(this, type, message);
		n.setVisibleTime(timeVisible);
		add(n);
	}

}



