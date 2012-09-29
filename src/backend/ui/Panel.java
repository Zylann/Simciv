package backend.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * Simple widget container.
 * If its parent is not an opaque container, it will be displayed as a window without title bar.
 * @author Marc
 *
 */
public class Panel extends WidgetContainer
{
	public Panel(Widget parent, int x, int y, int width, int height)
	{
		super(parent, x, y, width, height);
	}
	
	public Panel(Widget parent, int width, int height)
	{
		this(parent, 0, 0, width, height);
	}
	
	@Override
	public boolean isOpaqueContainer()
	{
		return true;
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		if(!parent.isOpaqueContainer())
			UIRenderer.getTheme().renderPanel(gfx, this);
		super.render(gc, gfx);
	}
	
}



