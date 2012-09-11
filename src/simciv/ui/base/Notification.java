package simciv.ui.base;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import simciv.content.Content;

public class Notification extends BasicWidget
{
	public static final byte TYPE_INFO = 0;
	public static final byte TYPE_CHECK = 1;
	public static final byte TYPE_WARNING = 2;
	public static final byte TYPE_ERROR = 3;
	
	public static int HEIGHT = 16;
	public static int DEFAULT_VISIBLE_TIME = 6000; // in milliseconds
	
	private int timeVisible;
	private byte type;
	private String text;
	private ArrayList<IActionListener> listeners;
	
	public Notification(NotificationArea parent, byte type, String text)
	{
		super(parent, 0, 0, parent.getWidth(), HEIGHT);
		timeVisible = DEFAULT_VISIBLE_TIME;
		this.type = type;
		this.text = text;
		listeners = new ArrayList<IActionListener>();
	}
		
	public byte getType()
	{
		return type;
	}
	
	public void setVisibleTime(int t)
	{
		timeVisible = t;
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
		switch(getType())
		{
		case Notification.TYPE_INFO : return Content.sprites.uiIconInfo;
		case Notification.TYPE_CHECK : return Content.sprites.uiIconCheck;
		case Notification.TYPE_WARNING : return Content.sprites.uiIconWarning;
		case Notification.TYPE_ERROR : return Content.sprites.uiIconError;
		default : return null;
		}
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


