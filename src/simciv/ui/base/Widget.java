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
	
	/** Relative position X towards its parent **/
	protected int posX;
	/** Relative position Y towards its parent **/	
	protected int posY;
	
	/** Width (all widgets are rectangular) **/
	protected int width;
	/** Height (all widgets are rectangular) **/
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
		posX = x;
		posY = y;
		width = w > 0 ? w : 0;
		height = h > 0 ? h : 0;
	}
	
	public Widget(Widget parent, int w, int h)
	{
		this(parent, 0, 0, w, h);
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
	
	/**
	 * Sets the new size of the widget, and recomputes its layout
	 * @param x
	 * @param y
	 */
	public void setSize(int x, int y)
	{
		setSizeNoLayout(x, y);
		
		WidgetContainer p = getParentContainer();
		if(p != null && p.getLayout() != null)
			p.layout();
		else
			layout();
	}
	
	public void setSizeNoLayout(int x, int y)
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
	 * @return widget's parent. Can be null if the widget is a root.
	 */
	public Widget getParent()
	{
		return parent;
	}
	
	/**
	 * @return widget's parent container.
	 * Can be null if the widget is a root, or if its parent is not a container.
	 */
	public WidgetContainer getParentContainer()
	{
		if(WidgetContainer.class.isInstance(parent))
			return (WidgetContainer)parent;
		return null;
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
	
	public void setAlignX(byte alignX)
	{
		setAlignX(alignX, 0);
	}
	
	public void setAlignY(byte alignY)
	{
		setAlignX(alignY, 0);
	}
	
	public void setAlignX(byte alignX, int marginX)
	{
		setAlign(alignX, ALIGN_NONE, marginX, 0);
	}
	
	public void setAlignY(byte alignY, int marginY)
	{
		setAlign(ALIGN_NONE, alignY, 0, marginY);
	}
	
	public void alignToCenter()
	{
		setAlign(ALIGN_CENTER, ALIGN_CENTER, 0, 0);
	}
	
	public byte getAlignX()
	{
		return alignX;
	}
	
	public byte getAlignY()
	{
		return alignY;
	}
	
	public int getMarginX()
	{
		return marginX;
	}
	
	public int getMarginY()
	{
		return marginY;
	}
	
	public void setMargins(int marginX, int marginY)
	{
		this.marginX = marginX;
		this.marginY = marginY;
		updateAlign();
	}
	
	public void setAlign(byte alignX, byte alignY, int marginX, int marginY)
	{
		this.alignX = alignX;
		this.alignY = alignY;
		this.marginX = marginX;
		this.marginY = marginY;
		updateAlign();
	}
	
	/**
	 * Keeps the widget inside its parent.
	 * Margins are supported.
	 */
	public void keepInsideParent()
	{
		if(parent == null)
			return;
		
		if(posX - marginX < 0)
			posX = marginX;
		if(posX + width + marginX >= parent.getWidth())
			posX = parent.getWidth() - width - marginX;
		
		if(posY - marginY < 0)
			posY = marginY;
		if(posY + width + marginY >= parent.getHeight())
			posY = parent.getHeight() - height - marginY;
	}
	
	public void updateAlignX()
	{
		switch(alignX)
		{
		case ALIGN_LEFT :	posX = marginX;	break;
		case ALIGN_CENTER :	posX = (parent.getWidth() - getWidth()) / 2; break;
		case ALIGN_RIGHT :  posX = parent.getWidth() - getWidth() - marginX; break;
		default : break;
		}
	}
	
	public void updateAlignY()
	{
		switch(alignY)
		{
		case ALIGN_TOP :	posY = marginY; break;
		case ALIGN_CENTER : posY = (parent.getHeight() - getHeight()) / 2; break;
		case ALIGN_BOTTOM : posY = parent.getHeight() - getHeight() - marginY; break;
		default : break;
		}
	}
	
	private void updateAlign()
	{
		if(parent == null)
			return;
		
		WidgetContainer p = getParentContainer();
		if(p != null && p.getLayout() != null)
			return;
		
		updateAlignX();
		updateAlignY();
	}
		
	/**
	 * Updates widget's positionning towards its parent.
	 * Called when the parent widget is resized.
	 */
	public void layout()
	{
		updateAlign();
	}
	
	/**
	 * If true, the widget will intercept events if the mouse cursor is contained in.
	 * Example : if there is two superposed containers and one receives an event, the second one will not
	 * be notified of this event. In another case, a window hidden over another will not receive mouse clicks.
	 * (this is useful mainly for containers)
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
		WidgetContainer p = getParentContainer();
		if(p != null)
			p.popupChild(this);
	}
	
	/*
	 * Each of these methods below return a boolean.
	 * If true, the event will be consumed by the GUI.
	 * If false, it will be forwarded to the game.
	 * Each coordinate is screen-relative.
	 */
		
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




