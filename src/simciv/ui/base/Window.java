package simciv.ui.base;

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
	private WindowCloseButton winCloseButton;
	
	public Window(WidgetContainer parent, int x, int y, int width, int height, String title)
	{
		super(parent, x, y, width, height + WindowTitleBar.height);
		titleBar = new WindowTitleBar(this, title);
		winCloseButton = new WindowCloseButton(this);
		content = new WidgetContainer(this, 0, titleBar.getHeight(), width, height - titleBar.getHeight());
		try
		{
			super.add(winCloseButton);
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
		winCloseButton.posX = width - winCloseButton.getWidth();
	}
	
	public void addOnCloseAction(IActionListener action)
	{
		winCloseButton.addActionListener(action);
	}
	
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
