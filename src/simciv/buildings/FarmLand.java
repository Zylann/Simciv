package simciv.buildings;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import simciv.ContentManager;
import simciv.Game;
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
	//private static Image imgCrops;
	private static byte MIN_LEVEL = 0;
	private static byte MAX_LEVEL = 7;
	
	byte level;
	int ticksPerLevel; // how many ticks are needed to increase crops level?
	int ticksBeforeNextLevel; // how many ticks remains before the next level?
	
	static
	{
		properties = new BuildingProperties("Farmland");
		properties.setCapacity(1).setSize(3, 3, 0).setCost(100);
	}
	
	public FarmLand(World w)
	{
		super(w);
		if(imgDirt == null)
			imgDirt = ContentManager.instance().getImage("city.farmland");
		ticksPerLevel = secondsToTicks(60);
		ticksBeforeNextLevel = ticksPerLevel;
		level = MIN_LEVEL;
	}
	
	@Override
	public void tick()
	{
		if(employees.isEmpty())
			return;
		if(level != MAX_LEVEL)
		{
			ticksBeforeNextLevel--;
			if(ticksBeforeNextLevel == 0)
			{
				level++;
				ticksBeforeNextLevel = ticksPerLevel;
			}
		}
	}

	@Override
	public void render(Graphics gfx)
	{
		// Soil
		gfx.drawImage(imgDirt, posX * Game.tilesSize, posY * Game.tilesSize);
		// Crops
		// TODO FarmLand: render crops
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
