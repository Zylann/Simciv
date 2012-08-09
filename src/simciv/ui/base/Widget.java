package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * Base class for all GUI elements (graphical user interface)
 * @author Marc
 *
 */
public abstract class Widget
{
	public static final byte ALIGN_NONE = 0;
	public static final byte ALIGN_CENTER = 1;
	public static final byte ALIGN_CENTER_X = 2;
	public static final byte ALIGN_CENTER_Y = 3;
	
	// Relative position towards its parent
	protected int posX;
	protected int posY;
	// Size (all widgets are rectangular)
	protected int width;
	protected int height;
	
	protected boolean visible;
	protected WidgetContainer parent;
	protected byte align;
	
	public Widget(WidgetContainer parent, int x, int y, int w, int h)
	{
		this.parent = parent;
		visible = true;
		align = ALIGN_NONE;
		posX = x;
		posY = y;
		width = w > 0 ? w : 0;
		height = h > 0 ? h : 0;
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
		this.posX = x;
		this.posY = y;
		setSize(width, height);
	}
	
	public void setSize(int x, int y)
	{
		width = x > 0 ? x : 0;
		height = y > 0 ? y : 0;
	}
	
	public void setPosition(int x, int y)
	{
		this.posX = x;
		this.posY = y;
	}
	
	public void setX(int x)
	{
		this.posX = x;
	}
	
	public void setY(int y)
	{
		this.posY = y;
	}
	
	public int getX()
	{
		return posX;
	}

	public int getY()
	{
		return posY;
	}
	
	public int getAbsoluteX()
	{
		if(parent != null)
			return posX + parent.getAbsoluteX();
		return posX;
	}

	public int getAbsoluteY()
	{
		if(parent != null)
			return posY + parent.getAbsoluteY();
		return posY;
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
	
	/**
	 * Called when the parent widget is resized
	 */
	public void layout()
	{
		switch(align)
		{
		case ALIGN_CENTER : alignToCenter(true, true); break;
		case ALIGN_CENTER_X : alignToCenter(true, false); break;
		case ALIGN_CENTER_Y : alignToCenter(false, true); break;
		default : break;
		}
	}
	
	public void setAlign(byte a)
	{
		align = a;
		layout();
	}
	
	private void alignToCenter(boolean onX, boolean onY)
	{
		if(parent == null)
			return;
		if(onX)
			posX = (parent.getWidth() - getWidth()) / 2;
		if(onY)
			posY = (parent.getHeight() - getHeight()) / 2;
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
	
	public abstract void render(GameContainer gc, Graphics gfx);
	
}




