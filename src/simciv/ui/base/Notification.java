package simciv.ui.base;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Notification extends BasicWidget
{
	public static int HEIGHT = 16;
	private static int VISIBLE_TIME = 8000; // in milliseconds
	
	private int timeVisible;
	private Image icon;
	private String text;
	private ArrayList<IActionListener> listeners;
	
	public Notification(NotificationArea parent, int width)
	{
		super(parent, 0, 0, width, HEIGHT);
		timeVisible = VISIBLE_TIME;
		setAlignX(Widget.ALIGN_CENTER);
		listeners = new ArrayList<IActionListener>();
	}
	
	public Notification(NotificationArea parent, int width, String text)
	{
		this(parent, width);
		this.text = text;
	}

	public Notification(NotificationArea parent, int width, Image icon, String text)
	{
		this(parent, width);
		this.icon = icon;
		this.text = text;
	}
	
	public void addClickListener(IActionListener l)
	{
		listeners.add(l);
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		if(contains(x, y))
		{
			for(IActionListener l : listeners)
				l.actionPerformed(this);
			return true;
		}
		return false;
	}

	public Image getIcon()
	{
		return icon;
	}
	
	public String getText()
	{
		return text;
	}
	
	@Override
	public boolean isOpaqueContainer()
	{
		return true;
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
	
	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderNotification(gfx, this);
	}

	public boolean isTimeVisibleEnded()
	{
		return timeVisible == 0;
	}

}


