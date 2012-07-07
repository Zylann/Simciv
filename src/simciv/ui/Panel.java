package simciv.ui;

import org.newdawn.slick.Graphics;

public class Panel extends WidgetContainer
{
	public Panel(WidgetContainer parent, int x, int y, int width, int height)
	{
		super(parent, x, y, width, height);
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
	public void render(Graphics gfx)
	{
		UIRenderer.instance().renderFrame(gfx, this);
		super.render(gfx);
	}
}
