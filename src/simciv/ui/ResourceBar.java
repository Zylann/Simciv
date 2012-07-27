package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.content.Content;

public class ResourceBar extends Widget
{
	private static SpriteSheet background;
	private static final int HEIGHT = 24;
	private static final int WIDTH = 100;
	
	private int population;
	private int populationAtWork;
	
	public ResourceBar(WidgetContainer parent, int x, int y)
	{
		super(parent, x, y, WIDTH, HEIGHT);
		if(background == null)
			background = new SpriteSheet(Content.images.uiResourceBar, HEIGHT, HEIGHT);
	}
	
	public void update(int population, int populationAtWork)
	{
		this.population = population;
		this.populationAtWork = populationAtWork;
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{		
		gfx.pushTransform();
		gfx.translate(getAbsoluteX(), getAbsoluteY());

		int b = height;
	
		// Background
		gfx.drawImage(background.getSprite(0, 0), 0, 0);
		UIRenderer.instance().renderImageRepeatXY(gfx, background.getSprite(1, 0), b, 0, width - 3 * b, b);
		gfx.drawImage(background.getSprite(2, 0), width - 2 * b, 0);
		
		// Icon
		gfx.drawImage(Content.images.uiIndicatorsPopulation, 4, 4);
		
		// Text
		String populationText = "" + population;
		gfx.setColor(Color.black);
		gfx.drawString(populationText, 24, 6);
		
		// Work ratio
		int h = height - 7;
		int x = 20;
		int y = height - 4;
		if(population != 0)
		{
			int t = (int) (h * (float)populationAtWork / (float)population);
			gfx.setColor(new Color(0, 224, 0));
			gfx.fillRect(x, y - t, 2, t);
			gfx.setColor(new Color(224, 160, 0));
			gfx.fillRect(x, y - h, 2, h - t);
		}
		else
		{
			gfx.setColor(Color.darkGray);
			gfx.fillRect(x, y - h, 2, h);
		}
		
		gfx.popTransform();
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
