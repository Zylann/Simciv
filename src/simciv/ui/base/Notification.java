package simciv.ui.base;

import org.newdawn.slick.SlickException;

public class Notification extends Panel
{
	public static int HEIGHT = 16;
	private static int VISIBLE_TIME = 8000; // in milliseconds
	
	private int timeVisible;
	
	public Notification(NotificationArea parent, int width)
	{
		super(parent, 0, 0, width, HEIGHT);
		timeVisible = VISIBLE_TIME;
		setAlignX(Widget.ALIGN_CENTER);
	}
	
	public Notification(NotificationArea parent, int width, String text)
	{
		this(parent, width);
		try
		{
			add(new Label(this, 4, 2, text));
		} catch (SlickException e)
		{
			e.printStackTrace();
		}
	}

	public void update(int delta)
	{
		if(timeVisible > 0)
		{
			timeVisible -= delta;
			if(timeVisible < 0)
				timeVisible = 0;
		}
	}
	
	public boolean isTimeVisibleEnded()
	{
		return timeVisible == 0;
	}

}


