package simciv.ui.base;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

/**
 * A widget that can contain another widgets.
 * @author Marc
 *
 */
public class WidgetContainer extends Widget
{
	protected ArrayList<Widget> children; // ordered as event receiving

	public WidgetContainer(Widget parent, int x, int y, int width, int height)
	{
		super(parent, x, y, width, height);
		children = new ArrayList<Widget>();
	}
	
	// TODO don't throw exceptions, use messages
	public void add(Widget child) throws SlickException
	{
		if(child == null)
			throw new SlickException("Cannot add a null child widget");
		if(children.contains(child))
			throw new SlickException("A child has been added twice");
		children.add(child);
	}
	
	public void remove(Widget child)
	{
		children.remove(child);
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
	
	/**
	 * Creates and returns a list filled with all visible child widgets.
	 * @return
	 */
	private List<Widget> getVisibleWidgets()
	{
		ArrayList<Widget> visibleWidgets = new ArrayList<Widget>();
		for(Widget child : children)
		{
			if(child.isVisible())
				visibleWidgets.add(child);
		}
		return visibleWidgets;
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
		List<Widget> visibleWidgets = getVisibleWidgets();
		for(Widget child : visibleWidgets)
		{
			if(child.mousePressed(button, x, y))
			{
				if(!res)
					res = true;
			}
			if(child.isOpaqueContainer() && child.contains(x, y))
				return true;
		}
		return res;
	}

	@Override
	public boolean mouseReleased(int button, int x, int y)
	{
		if(!visible)
			return false;
		List<Widget> visibleWidgets = getVisibleWidgets();
		for(Widget child : visibleWidgets)
		{
			if(child.mouseReleased(button, x, y))
				return true;
		}
		return false;
	}

	@Override
	public boolean mouseClicked(int button, int x, int y, int clickCount)
	{
		if(!visible)
			return false;
		List<Widget> visibleWidgets = getVisibleWidgets();
		for(Widget child : visibleWidgets)
		{
			if(child.mouseClicked(button, x, y, clickCount))
				return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int key, char c)
	{
		if(!visible)
			return false;
		List<Widget> visibleWidgets = getVisibleWidgets();
		for(Widget child : visibleWidgets)
		{
			if(child.keyPressed(key, c))
				return true;
		}
		return false;
	}

	@Override
	public boolean keyReleased(int key, char c)
	{
		if(!visible)
			return false;
		List<Widget> visibleWidgets = getVisibleWidgets();
		for(Widget child : visibleWidgets)
		{
			if(child.keyReleased(key, c))
				return true;
		}
		return false;
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

	public void onScreenResize(int width, int height)
	{
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		if(!this.visible && visible)
		{
			this.visible = true;
			onShow();
			for(Widget w : children)
			{
				if(w.isVisible())
					w.onShow();
			}
		}
		else if(this.visible && !visible)
		{
			this.visible = false;
			onHide();
			for(Widget w : children)
			{
				if(w.isVisible())
					w.onHide();
			}
		}
	}

	/**
	 * Brings a widget to foreground
	 * Warning : calling while iterating is not supported
	 * @param widget
	 */
	public void popupChild(Widget widget)
	{
		if(children.remove(widget))
			children.add(0, widget);
		else
			Log.warn("popupChild : widget not found");
	}

}


