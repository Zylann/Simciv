package simciv.ui.base;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * A widget container with a frame, a title bar and a close button.
 * @author Marc
 *
 */
public class Window extends WidgetContainer
{
	private WidgetContainer content;
	private WindowTitleBar titleBar; 
	private WindowCloseButton winCloseButton;
	private ArrayList<IActionListener> closeActionListeners;
	private ArrayList<IActionListener> openActionListeners;
	
	/**
	 * Constructs a located, sized and empty window with a title.
	 * @param parent : window's container (parent, usually the root pane)
	 * @param x : parent-relative X coordinate
	 * @param y : parent-relative Y coordinate
	 * @param width : window's width
	 * @param height : windtw's height
	 * @param title : window's title
	 */
	public Window(Widget parent, int x, int y, int width, int height, String title)
	{
		super(parent, x, y, width, height + WindowTitleBar.height);
		titleBar = new WindowTitleBar(this, title);
		winCloseButton = new WindowCloseButton(this);
		content = new WidgetContainer(this, 0, titleBar.getHeight(), width, height - titleBar.getHeight());
		closeActionListeners = new ArrayList<IActionListener>();
		openActionListeners = new ArrayList<IActionListener>();
		
		super.add(winCloseButton);
		super.add(titleBar);
		super.add(content);
	}
	
	public Window(Widget parent, int w, int h, String title)
	{
		this(parent, 0, 0, w, h, title);
	}
	
	/**
	 * Sets text displayed on the title bar
	 * @param title
	 */
	public void setTitle(String title)
	{
		titleBar.setText(title);
	}
	
	@Override
	public void adaptSizeFromChildren()
	{
		content.adaptSizeFromChildren();
		width = content.width;
		height = content.height;
		titleBar.width = width;
		winCloseButton.posX = width - winCloseButton.getWidth();
	}
	
	/**
	 * Shows the window and notifies open action listeners
	 */
	public void open()
	{
		setVisible(true);
		for(IActionListener l : openActionListeners)
			l.actionPerformed(this);
	}
	
	/**
	 * Hides the window and notifies close action listeners
	 */
	public void close()
	{
		setVisible(false);
		for(IActionListener l : closeActionListeners)
			l.actionPerformed(this);
	}
	
	/**
	 * Adds an action that will be performed on window's closing
	 * @param action
	 */
	public void addOnCloseAction(IActionListener action)
	{
		closeActionListeners.add(action);
	}
	
	/**
	 * Adds an actionthat will be performed on window's opening
	 * @param action
	 */
	public void addOnOpenAction(IActionListener action)
	{
		openActionListeners.add(action);
	}

	/**
	 * Set if we can drag the window using its title bar
	 * @param enabled
	 */
	public void setDraggable(boolean enabled)
	{
		titleBar.setDraggable(enabled);
	}
	
	@Override
	public boolean isOpaqueContainer()
	{
		return true;
	}
	
	@Override
	public void add(Widget child)
	{
		// TODO use a content pane

		child.setY(child.getY() + titleBar.getHeight());
		content.add(child);
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderWindow(gfx, this);
		super.render(gc, gfx);
	}

}
