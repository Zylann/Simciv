package simciv.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * A widget container with a frame, a title bar and a close button.
 * @author Marc
 *
 */
public class Window extends WidgetContainer
{
	private WidgetContainer content;
	private WindowTitleBar titleBar; 
	private WindowCloseButton closeButton;
	
	public Window(WidgetContainer parent, int x, int y, int width, int height, String title)
	{
		super(parent, x, y, width, height + WindowTitleBar.height);
		titleBar = new WindowTitleBar(this, title);
		closeButton = new WindowCloseButton(this);
		content = new WidgetContainer(this, 0, titleBar.getHeight(), width, height - titleBar.getHeight());
		try
		{
			super.add(closeButton);
			super.add(titleBar);
			super.add(content);
		} catch (SlickException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void adaptSize()
	{
		content.adaptSize();
		width = content.width;
		height = content.height;
		titleBar.width = width;
		closeButton.posX = width - closeButton.getWidth();
	}
	
	public void setOnCloseAction(IActionListener action)
	{
		closeButton.setActionListener(action);
	}
	
	public void setDraggable(boolean enabled)
	{
		titleBar.setDraggable(enabled);
	}
	
	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		if(super.mousePressed(button, x, y))
			return true;
		if(contains(x, y))
			return true;
		return false;
	}

	@Override
	public boolean mouseClicked(int button, int x, int y, int clickCount)
	{
		if(super.mouseClicked(button, x, y, clickCount))
			return true;
		if(contains(x, y))
			return true;
		return false;
	}
		
	@Override
	public void add(Widget child) throws SlickException
	{
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
