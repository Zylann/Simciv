package simciv.ui;

import backend.IView;
import backend.ui.IActionListener;
import backend.ui.Notification;
import backend.ui.NotificationArea;
import backend.ui.Widget;
import backend.ui.WidgetContainer;

public class MapNotificationArea extends NotificationArea implements IMapNotificationListener
{
	private IView mapViewRef;
	
	public MapNotificationArea(WidgetContainer parent, int x, int y, int width)
	{
		super(parent, x, y, width);
	}
	
	public void setMapView(IView view)
	{
		mapViewRef = view;
	}

	@Override
	public void notify(byte type, String message, int x, int y)
	{
		Notification n = new Notification(this, type, message);
		n.addClickListener(new WarpViewAction(x, y));
		add(n);
	}

	@Override
	public void notify(byte type, String message, int x, int y, int timeVisible)
	{
		Notification n = new Notification(this, type, message);
		n.addClickListener(new WarpViewAction(x, y));
		n.setVisibleTime(timeVisible);
		add(n);
	}
	
	private class WarpViewAction implements IActionListener
	{
		int x, y;
		
		public WarpViewAction(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public void actionPerformed(Widget sender) {
			mapViewRef.setCenter(x, y);
		}
	}

}
