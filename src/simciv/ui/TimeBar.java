package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import simciv.content.Content;
import simciv.ui.base.BasicWidget;
import simciv.ui.base.UIRenderer;
import simciv.ui.base.WidgetContainer;

public class TimeBar extends BasicWidget
{
	private static final int HEIGHT = 16;
	private static final int WIDTH = 110;
	
	private String timeText;
	
	public TimeBar(WidgetContainer parent, int x, int y)
	{
		super(parent, x, y, WIDTH, HEIGHT);
		timeText = "---";
	}
	
	public void update(String timeText)
	{
		this.timeText = timeText;
	}
	
	@Override
	public void layout()
	{
		setPosition(parent.getWidth() - width - 10, 34);
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		gfx.pushTransform();
		gfx.translate(getAbsoluteX(), getAbsoluteY());
	
		// Background
		int b = height;
		UIRenderer.instance().renderBar(gfx, Content.sprites.uiTimeBar, 0, 0, width, height, b, 0);
		
		gfx.setColor(Color.black);
		gfx.drawString(timeText, 4, 2);
		
		gfx.popTransform();
		
	}
	
}


