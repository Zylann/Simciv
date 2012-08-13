package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Panel extends WidgetContainer
{
	public Panel(WidgetContainer parent, int x, int y, int width, int height)
	{
		super(parent, x, y, width, height);
	}
	
	@Override
	public boolean isOpaqueContainer()
	{
		return true;
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderPanel(gfx, this);
		super.render(gc, gfx);
	}
	
}



