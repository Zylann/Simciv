package simciv.ui.base;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class WidgetContainer extends Widget
{
	protected ArrayList<Widget> children;

	public WidgetContainer(WidgetContainer parent, int x, int y, int width, int height)
	{
		super(parent, x, y, width, height);
		children = new ArrayList<Widget>();
	}
	
	public void add(Widget child) throws SlickException
	{
		if(child == null)
			throw new SlickException("Cannot add a null child widget");
		children.add(child);
	}
	
	/**
	 * Adapts the size of the container to its children
	 */
	public void adaptSize()
	{
		int newWidth = 0;
		int newHeight = 0;
		
		for(Widget child : children)
		{
			int x = child.getX() + child.getWidth();
			if(x > newWidth)
				newWidth = x;
			int y = child.getY() + child.getHeight();
			if(y > newHeight)
				newHeight = y;
		}
		
		setSize(newWidth, newHeight);
	}
	
	@Override
	public void setSize(int x, int y)
	{
		super.setSize(x, y);
		layout();
	}
	
	@Override
	public void layout()
	{
		super.layout(); // Widget.layout
		for(Widget w : children)
			w.layout();
	}

	@Override
	public boolean mouseMoved(int oldX, int oldY, int newX, int newY)
	{
		if(!visible)
			return false;
		for(Widget child : children)
		{
			if(child.visible && child.mouseMoved(oldX, oldY, newX, newY))
				return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(int oldX, int oldY, int newX, int newY)
	{
		if(!visible)
			return false;
		for(Widget child : children)
		{
			if(child.visible && child.mouseDragged(oldX, oldY, newX, newY))
				return true;
		}
		return false;
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		if(!visible)
			return false;
		boolean res = false;
		for(Widget child : children)
		{
			if(child.visible && child.mousePressed(button, x, y))
			{
				if(!res)
					res = true;
			}
		}
		return res;
	}

	@Override
	public boolean mouseReleased(int button, int x, int y)
	{
		if(!visible)
			return false;
		for(Widget child : children)
		{
			if(child.visible && child.mouseReleased(button, x, y))
				return true;
		}
		return false;
	}

	@Override
	public boolean mouseClicked(int button, int x, int y, int clickCount)
	{
		if(!visible)
			return false;
		for(Widget child : children)
		{
			if(child.visible && child.mouseClicked(button, x, y, clickCount))
				return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int key, char c)
	{
		if(!visible)
			return false;
		for(Widget child : children)
		{
			if(child.visible && child.keyPressed(key, c))
				return true;
		}
		return false;
	}

	@Override
	public boolean keyReleased(int key, char c)
	{
		if(!visible)
			return false;
		for(Widget child : children)
		{
			if(child.visible && child.keyReleased(key, c))
				return true;
		}
		return false;
	}
	
	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		if(!visible)
			return;
		for(int i = children.size() - 1; i >= 0; i--)
		{
			if(children.get(i).isVisible())
				children.get(i).render(gc, gfx);
		}
	}

	@Override
	public boolean mouseWheelMoved(int change)
	{
		if(!visible)
			return false;
		for(Widget child : children)
		{
			if(child.visible && child.mouseWheelMoved(change))
				return true;
		}
		return false;
	}

	public void onScreenResize(int width, int height)
	{
	}

}


