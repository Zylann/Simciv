package simciv.buildings;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import simciv.ContentManager;
import simciv.Game;
import simciv.Job;
import simciv.World;
import simciv.effects.RisingIcon;
import simciv.jobs.Farmer;
import simciv.units.Citizen;

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
	private static Image imgDirt[] = new Image[2];
	private static Image imgCrops;
	//private static Image imgCrops;
	private static byte MIN_LEVEL = 0;
	private static byte MAX_LEVEL = 7;
	
	byte level;
	int ticksPerLevel; // how many ticks are needed to increase crops level?
	int ticksBeforeNextLevel; // how many ticks remains before the next level?
	
	static
	{
		properties = new BuildingProperties("Farmland");
		properties.setUnitsCapacity(3).setSize(3, 3, 0).setCost(100);
	}
	
	public FarmLand(World w)
	{
		super(w);
		if(imgDirt[0] == null)
			imgDirt[0] = ContentManager.instance().getImage("city.farmland");
		if(imgDirt[1] == null)
			imgDirt[1] = ContentManager.instance().getImage("city.activeFarmland");
		if(imgCrops == null)
			imgCrops = ContentManager.instance().getImage("city.farmland.crops");
		ticksPerLevel = secondsToTicks(60);
		ticksBeforeNextLevel = ticksPerLevel;
		level = MIN_LEVEL;
		state = Building.NORMAL;
	}
	
	@Override
	public void tick()
	{
		if(state == Building.NORMAL)
		{
			if(!needEmployees())
				state = Building.ACTIVE;
		}
		else if(state == Building.ACTIVE)
		{
			if(needEmployees())
				state = Building.NORMAL;
			
			// Crops are growing
			if(level != MAX_LEVEL)
			{
				ticksBeforeNextLevel--;
				if(ticksBeforeNextLevel == 0)
				{
					level++;
					ticksBeforeNextLevel = ticksPerLevel;
				}
			}
			else
			{
				// TODO generate resources
			}
		}
	}

	@Override
	public void render(Graphics gfx)
	{
		int gx = posX * Game.tilesSize;
		int gy = posY * Game.tilesSize;

		// Soil
		if(state == Building.NORMAL || needEmployees())
			gfx.drawImage(imgDirt[0], gx, gy);
		else
			gfx.drawImage(imgDirt[1], gx, gy);
		
		// Crops
		if(state == Building.ACTIVE || level != 0)
		{
			for(int j = 0; j < 3; j++)
			{
				for(int i = 0; i < 3; i++)
				{
					gfx.drawImage(imgCrops,
							gx + i * Game.tilesSize,
							gy + j * Game.tilesSize,
							gx + (i+1) * Game.tilesSize,
							gy + (j+1) * Game.tilesSize,
							level * Game.tilesSize,
							0,
							(level + 1) * Game.tilesSize,
							Game.tilesSize
					);
				}
			}
		}
	}

	@Override
	public BuildingProperties getProperties()
	{
		return properties;
	}

	@Override
	public int getProductionProgress()
	{
		return (int) (level * 100.f / MAX_LEVEL);
	}

	@Override
	protected int getTickTime()
	{
		return 500;
	}

	@Override
	public Job giveNextJob(Citizen citizen)
	{
		if(needEmployees())
		{
			Job job = new Farmer(citizen, this);
			addEmployee(citizen);
			// Visual feedback
			worldRef.addGraphicalEffect(
					new RisingIcon(
							posX + 1, posY + 1,
							ContentManager.instance().getImage("effect.greenStar")));
			return job;
		}
		return null;
	}
}


