package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import simciv.content.Content;
import simciv.ui.base.BasicWidget;
import simciv.ui.base.UIRenderer;
import simciv.ui.base.WidgetContainer;

/**
 * This is a bar displaying main city indicators.
 * @author Marc
 *
 */
public class IndicatorsBar extends BasicWidget
{
	private static final int HEIGHT = 24;
	private static final int WIDTH = 110;
	
	// Note : these attributes are for display only
	private int population;
	private int populationAtWork;
	private int money;
	private float monthProgressRatio;
	
	public IndicatorsBar(WidgetContainer parent, int x, int y)
	{
		super(parent, x, y, WIDTH, HEIGHT);
	}
	
	public void setWorldTime(float monthProgressRatio)
	{
		this.monthProgressRatio = monthProgressRatio;
	}
	
	public void update(int population, int populationAtWork, int money, float monthProgressRatio)
	{
		this.population = population;
		this.populationAtWork = populationAtWork;
		this.money = money;
		this.monthProgressRatio = monthProgressRatio;
	}
	
	@Override
	public void layout()
	{
		setPosition(parent.getWidth() - width - 10, 10);
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{		
		gfx.pushTransform();
		gfx.translate(getAbsoluteX(), getAbsoluteY());
	
		// Background
		int b = height;
		UIRenderer.instance().renderBar(gfx, Content.sprites.uiResourceBar, 0, 0, width, height, b, 0);
		
		gfx.setColor(Color.black);

		// Population
		gfx.drawImage(Content.sprites.uiIndicatorsPopulation, 4, 4); // Icon
		String populationText = "" + population; // Text
		gfx.drawString(populationText, 24, 6);
		
		// Money
		gfx.drawImage(Content.sprites.uiIndicatorsMoney, 50, 4); // Icon
		String moneyText = "" + money;
		if(money < 500)
		{
			gfx.setColor(new Color(0,0,0,128));
			gfx.drawString(moneyText, 71, 7); // Text
			
			if(money >= 200 && money < 500)
				gfx.setColor(Color.orange);
			else if(money < 200)
				gfx.setColor(Color.red);
		}
		gfx.drawString(moneyText, 70, 6); // Text
		
		// Progress bars :
		
		int h = height - 7;
		int y = height - 4;

		// Work ratio
		int x = 20;
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

		// Month progress
		x = 66;
		int t = (int) ((float)h * monthProgressRatio);
		gfx.setColor(new Color(32, 32, 255));
		gfx.fillRect(x, y - t, 2, t);
		gfx.setColor(Color.darkGray);
		gfx.fillRect(x, y - h, 2, h - t);
				
		gfx.popTransform();
		
	}

}
