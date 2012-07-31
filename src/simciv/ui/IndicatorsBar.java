package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.content.Content;

/**
 * This is a bar displaying main city indicators.
 * @author Marc
 *
 */
public class IndicatorsBar extends BasicWidget
{
	private static SpriteSheet background;
	private static final int HEIGHT = 24;
	private static final int WIDTH = 100;
	
	// Note : these attributes are for display only
	private int population;
	private int populationAtWork;
	private int money;
	
	public IndicatorsBar(WidgetContainer parent, int x, int y)
	{
		super(parent, x, y, WIDTH, HEIGHT);
		if(background == null)
			background = new SpriteSheet(Content.images.uiResourceBar, HEIGHT, HEIGHT);
	}
	
	public void update(int population, int populationAtWork, int money)
	{
		this.population = population;
		this.populationAtWork = populationAtWork;
		this.money = money;
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{		
		gfx.pushTransform();
		gfx.translate(getAbsoluteX(), getAbsoluteY());
	
		// Background
		int b = height;
		UIRenderer.instance().renderBar(gfx, background, 0, 0, width, height, b, 0);
		
		gfx.setColor(Color.black);

		// Population
		gfx.drawImage(Content.images.uiIndicatorsPopulation, 4, 4); // Icon
		String populationText = "" + population; // Text
		gfx.drawString(populationText, 24, 6);
		
		// Money
		gfx.drawImage(Content.images.uiIndicatorsMoney, 50, 4); // Icon
		String moneyText = "" + money;
		gfx.drawString(moneyText, 70, 6); // Text
		
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

}
