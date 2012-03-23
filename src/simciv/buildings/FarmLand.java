package simciv.buildings;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import simciv.Game;
import simciv.Workplace;
import simciv.World;

/**
 * Farmlands are used to raise one type of plant.
 * It will grow slowly (level) until it reaches the max level.
 * At max level, the land can be farmed.
 * @author Marc
 *
 */
public class FarmLand extends Workplace
{	
	private static BuildingProperties properties;
	private static Image imgDirt;
	private static byte MIN_LEVEL = 0;
	private static byte MAX_LEVEL = 7;
	
	byte level;
	int levelTicks; // how many ticks are needed to increase crops level?
	int nextLevelTicks; // how many ticks remains before the next level?
	
	static
	{
		properties = new BuildingProperties("Farmland");
		properties.setCapacity(1).setSize(3, 3, 0).setCost(100);
	}
	
	public FarmLand(World w)
	{
		super(w);
		levelTicks = secondsToTicks(60);
		nextLevelTicks = levelTicks;
		level = MIN_LEVEL;
	}
	
	public static void loadContent() throws SlickException
	{
		imgDirt = new Image("data/farmland.png");
		imgDirt.setFilter(Image.FILTER_NEAREST);
	}

	@Override
	public void tick()
	{
		if(level != MAX_LEVEL)
		{
			nextLevelTicks--;
			if(nextLevelTicks == 0)
			{
				level++;
				nextLevelTicks = levelTicks;
			}
		}
	}

	@Override
	public void render(Graphics gfx)
	{
		gfx.drawImage(imgDirt, posX * Game.tilesSize, posY * Game.tilesSize);
	}

	@Override
	public BuildingProperties getProperties()
	{
		return properties;
	}

	@Override
	public int getProductionProgress()
	{
		return 0;
	}

	@Override
	protected int getTickTime()
	{
		return 500;
	}
}
