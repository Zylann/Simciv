package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * Base class for all GUI elements (graphical user interface).
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
	protected Widget parent;
	protected byte align;
	
	public Widget(Widget parent, int x, int y, int w, int h)
	{
		this.parent = parent;
		visible = true;
		align = ALIGN_NONE;
		posX = x;
		posY = y;
		width = w > 0 ? w : 0;
		height = h > 0 ? h : 0;
	}
	
	/**
	 * Changes the state of the visible flag.
	 * The widget should'nt be drawn and intercept events if visible is false.
	 * This method will also call onShow or onHide (for use in subclasses).
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		if(!this.visible && visible)
		{
			this.visible = true;
			onShow();
		}
		else if(this.visible && !visible)
		{
			this.visible = false;
			onHide();
		}
	}
	
	/**
	 * Called when the widget becomes visible.
	 */
	protected void onShow()
	{
	}
	
	/**
	 * Called when the widget becomes hidden.
	 */
	protected void onHide()
	{
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
	
	/**
	 * Sets parent-relative position
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y)
	{
		this.posX = x;
		this.posY = y;
	}
	
	/**
	 * Sets X parent-relative coordinate
	 * @param x
	 */
	public void setX(int x)
	{
		this.posX = x;
	}
	
	/**
	 * Sets Y parent-relative coordinate
	 * @param y
	 */
	public void setY(int y)
	{
		this.posY = y;
	}
	
	/**
	 * Gets X parent-relative coordinate
	 * @return
	 */
	public int getX()
	{
		return posX;
	}

	/**
	 * Gets Y parent-relative coordinate
	 * @return
	 */
	public int getY()
	{
		return posY;
	}
	
	/**
	 * Gets X screen-relative coordinate
	 * @return
	 */
	public int getAbsoluteX()
	{
		if(parent != null)
			return posX + parent.getAbsoluteX();
		return posX;
	}

	/**
	 * Gets Y screen-relative coordinate
	 * @return
	 */
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

	/**
	 * @return widget's parent. Can be null.
	 */
	public Widget getParent()
	{
		return parent;
	}
	
	/**
	 * Searches the root parent of the widget and returns it.
	 * Note : the returned widget has no parent.
	 * @return upper widget's parent.
	 */
	public Widget getRoot()
	{
		Widget root = getParent();
		while(root.getParent() != null)
			root = root.getParent();
		return root;
	}
	
	/**
	 * Tests if a point is contained in the widget.
	 * @param x : point X screen-relative coordinate
	 * @param y : point Y screen-relative coordinate
	 * @return true if contained, false if not.
	 */
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
	
	/**
	 * Sets and updates widget's alignment.
	 * @param a
	 */
	public void setAlign(byte a)
	{
		align = a;
		layout();
	}
	
	/**
	 * If true, the widget will intercept events if the mouse cursor
	 * is contained in.
	 * @return
	 */
	public boolean isOpaqueContainer()
	{
		return false;
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
	// Each coordinate is screen-relative.
	
	public abstract boolean mouseMoved(int oldX, int oldY, int newX, int newY);
	public abstract boolean mouseDragged(int oldX, int oldY, int newX, int newY);
	public abstract boolean mousePressed(int button, int x, int y);
	public abstract boolean mouseReleased(int button, int x, int y);
	public abstract boolean mouseClicked(int button, int x, int y, int clickCount);
	public abstract boolean mouseWheelMoved(int change);
	public abstract boolean keyPressed(int key, char c);
	public abstract boolean keyReleased(int key, char c);
	
	/**
	 * Draws the widget on the screen.
	 * @param gc : game container
	 * @param gfx : graphics context
	 */
	public abstract void render(GameContainer gc, Graphics gfx);
	
}




