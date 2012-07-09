package simciv.ui;

import org.newdawn.slick.Graphics;

/**
 * Base class for all GUI elements (graphical user interface)
 * @author Marc
 *
 */
public abstract class Widget
{
	// Relative position towards its parent
	protected int x;
	protected int y;
	// Size (all widgets are rectangular)
	protected int width;
	protected int height;
	
	protected boolean visible;
	protected WidgetContainer parent;
	
	public Widget(WidgetContainer parent, int x, int y, int width, int height)
	{
		this.parent = parent;
		visible = true;
		setGeometry(x, y, width, height);
	}
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	public void setGeometry(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width <= 0 ? 1 : width;
		this.height = height <= 0 ? 1 : height;
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}
	
	public int getAbsoluteX()
	{
		if(parent != null)
			return x + parent.getAbsoluteX();
		return x;
	}

	public int getAbsoluteY()
	{
		if(parent != null)
			return y + parent.getAbsoluteY();
		return y;
	}
	
	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public WidgetContainer getParent()
	{
		return parent;
	}
	
	public WidgetContainer getRoot()
	{
		WidgetContainer root = getParent();
		while(root.getParent() != null)
			root = root.getParent();
		return root;
	}

	public boolean contains(int x, int y)
	{
		int selfX = getAbsoluteX();
		int selfY = getAbsoluteY();
		
		return x >= selfX &&
			y >= selfY &&
			x < selfX + this.width &&
			y < selfY + this.height ;
	}
	
	// Each of these methods below return a boolean.
	// If true, the event will be consumed by the GUI.
	// If false, it will be forwarded to the game.
	
	public abstract boolean mouseMoved(int oldX, int oldY, int newX, int newY);
	public abstract boolean mouseDragged(int oldX, int oldY, int newX, int newY);
	public abstract boolean mousePressed(int button, int x, int y);
	public abstract boolean mouseReleased(int button, int x, int y);
	public abstract boolean mouseClicked(int button, int x, int y, int clickCount);
	public abstract boolean mouseWheelMoved(int change);
	public abstract boolean keyPressed(int key, char c);
	public abstract boolean keyReleased(int key, char c);
	
	public abstract void render(Graphics gfx);
}
