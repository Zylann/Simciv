package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import simciv.ContentManager;

public class ResourceBar extends Widget
{
	private static Image populationIcon;
	private static SpriteSheet background;
	private static final int HEIGHT = 24;
	private static final int WIDTH = 100;
	
	private int population;
	
	public ResourceBar(WidgetContainer parent, int x, int y)
	{
		super(parent, x, y, WIDTH, HEIGHT);
		if(populationIcon == null)
			populationIcon = ContentManager.instance().getImage("ui.indicators.population");
		if(background == null)
			background = new SpriteSheet(ContentManager.instance().getImage("ui.resourceBar"), HEIGHT, HEIGHT);
	}
	
	public void update(int population)
	{
		this.population = population;
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		int x = getAbsoluteX();
		int y = getAbsoluteY();
		int b = height;
		
		// Background
		gfx.drawImage(background.getSprite(0, 0), x, y);
		UIRenderer.instance().renderImageRepeatXY(gfx, background.getSprite(1, 0), x + b, y, width - 3 * b, b);
		gfx.drawImage(background.getSprite(2, 0), x + width - 2 * b, y);
		
		// Icon
		gfx.drawImage(populationIcon, x+4, y+4);
		
		// Text
		String populationText = "" + population;
		gfx.setColor(Color.black);
		gfx.drawString(populationText, x + 24, y + 6);
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
		return contains(x, y);
	}

	@Override
	public boolean mouseReleased(int button, int x, int y)
	{
		return false;
	}

	@Override
	public boolean mouseClicked(int button, int x, int y, int clickCount)
	{
		return contains(x, y);
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
