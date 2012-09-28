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
	public static final byte ALIGN_LEFT = 1;
	public static final byte ALIGN_RIGHT = 2;
	public static final byte ALIGN_TOP = 3;
	public static final byte ALIGN_BOTTOM = 4;
	public static final byte ALIGN_CENTER = 5;
	
	// Relative position towards its parent
	protected int posX;
	protected int posY;
	// Size (all widgets are rectangular)
	protected int width;
	protected int height;
	
	protected boolean visible;
	protected Widget parent;
	protected byte alignX;
	protected byte alignY;
	protected int marginX;
	protected int marginY;
	
	public Widget(Widget parent, int x, int y, int w, int h)
	{
		this.parent = parent;
		visible = true;
		alignX = ALIGN_NONE;
		alignY = ALIGN_NONE;
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
	
	public void setMargins(int mx, int my)
	{
		marginX = mx;
		marginY = my;
		layout();
	}
	
	/**
	 * Updates widget's positionning towards its parent.
	 * Called when the parent widget is resized.
	 */
	public void layout()
	{
		if(parent == null)
			return;
		
		switch(alignX)
		{
		case ALIGN_LEFT :	posX = marginX;	break;
		case ALIGN_CENTER :	posX = (parent.getWidth() - getWidth()) / 2; break;
		case ALIGN_RIGHT :  posX = parent.getWidth() - getWidth() - marginX; break;
		default : break;
		}
		
		switch(alignY)
		{
		case ALIGN_TOP :	posY = marginY; break;
		case ALIGN_CENTER : posY = (parent.getHeight() - getHeight()) / 2; break;
		case ALIGN_BOTTOM : posY = parent.getHeight() - getHeight() - marginY; break;
		default : break;
		}		
	}
	
	public void setAlign(byte alignX, byte alignY)
	{
		this.alignX = alignX;
		this.alignY = alignY;
		layout();
	}
	
	public void setAlignX(byte alignX)
	{
		this.alignX = alignX;
		layout();
	}
	
	public void setAlignY(byte alignY)
	{
		this.alignY = alignY;
		layout();
	}
	
	public void alignToCenter()
	{
		setAlign(Widget.ALIGN_CENTER, Widget.ALIGN_CENTER);
	}
	
	/**
	 * If true, the widget will intercept events if the mouse cursor is contained in.
	 * Example : if there is two containers and one receives an event, the second one will not
	 * be notified of this event. In another case, a window hidden over another will not receive mouse clicks.
	 * (this is useful mainly for containers, despite been present in Widgets)
	 * @return
	 */
	public boolean isOpaqueContainer()
	{
		return false;
	}
	
	/**
	 * Brings the widget to foreground
	 * Warning : calling while iterating is not supported
	 */
	public void popup()
	{
		if(WidgetContainer.class.isInstance(parent))
			((WidgetContainer)parent).popupChild(this);
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




