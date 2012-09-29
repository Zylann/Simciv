package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import backend.ui.Widget;
import backend.ui.WidgetContainer;


/**
 * A simple text display line.
 * @author Marc
 *
 */
public class InfoBar extends Widget
{
	public static final int HEIGHT = 16;

	public Color backColor;
	private String text;
	
	public InfoBar(WidgetContainer parent, int x, int y, int width)
	{
		super(parent, x, y, width, HEIGHT);
		backColor = new Color(0, 0, 0, 96);
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void render(GameContainer gc, Graphics gfx)
	{
		if(text != null)
		{
			if(text.length() != 0)
			{
				int gx = getAbsoluteX();
				int gy = getAbsoluteY();
				
				gfx.setColor(backColor);
				gfx.fillRect(gx, gy, getWidth(), getHeight());
				gfx.setColor(Color.white);
				gfx.drawString(text, gx + 2, gy + 2);
			}
		}
	}

	@Override
	public boolean mouseMoved(int oldX, int oldY, int newX, int newY)
	{
		return false;
	}

	@Override
	public boolean mouseDragged(int oldX, int oldY, int newX, int newY)
	{
		return false;
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		return false;
	}

	@Override
	public boolean mouseReleased(int button, int x, int y)
	{
		return false;
	}

	@Override
	public boolean mouseClicked(int button, int x, int y, int clickCount)
	{
		return false;
	}

	@Override
	public boolean mouseWheelMoved(int change)
	{
		return false;
	}

	@Override
	public boolean keyPressed(int key, char c)
	{
		return false;
	}

	@Override
	public boolean keyReleased(int key, char c)
	{
		return false;
	}
	
}


