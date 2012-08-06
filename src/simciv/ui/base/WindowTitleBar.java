package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class WindowTitleBar extends Widget
{
	public static final int height = 16;
	
	private String text;
	private boolean pressed;
	private boolean dragEnabled;
	
	public WindowTitleBar(Window parent, String title)
	{
		super(parent, 0, 0, parent.width, height);
		text = title;
		pressed = false;
		dragEnabled = true;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void setDraggable(boolean enable)
	{
		dragEnabled = enable;
	}

	@Override
	public boolean mouseMoved(int oldX, int oldY, int newX, int newY)
	{
		return false;
	}

	@Override
	public boolean mouseDragged(int oldX, int oldY, int newX, int newY)
	{
		if(dragEnabled && pressed)
		{
			parent.setPosition(parent.posX + newX - oldX, parent.posY + newY - oldY);
			return true;
		}
		return false;
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		if(contains(x, y))
		{
			pressed = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(int button, int x, int y)
	{
		pressed = false;
		return contains(x, y);
	}

	@Override
	public boolean mouseClicked(int button, int x, int y, int clickCount)
	{
		return false;
	}

	@Override
	public boolean mouseWheelMoved(int change)
	{
		return false;
	}

	@Override
	public boolean keyPressed(int key, char c)
	{
		return false;
	}

	@Override
	public boolean keyReleased(int key, char c)
	{
		return false;
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderWindowTitleBar(gfx, this);
	}

}


