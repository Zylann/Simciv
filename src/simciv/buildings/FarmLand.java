package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Cheats;
import simciv.ContentManager;
import simciv.Game;
import simciv.Job;
import simciv.Resource;
import simciv.ResourceSlot;
import simciv.World;
import simciv.jobs.Conveyer;
import simciv.jobs.InternalJob;
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
	private static Image imgRottenCrops;
	//private static Image imgCrops;
	private static byte MIN_LEVEL = 0;
	private static byte MAX_LEVEL = 7;
	private static byte ROTTEN_LEVEL = 8;
	
	private byte cropsLevel;
	private transient int ticksPerLevel; // how many ticks are needed to increase crops level?
	private int ticksBeforeNextLevel; // how many ticks remains before the next level?
	
	static
	{
		properties = new BuildingProperties("Farmland");
		properties.setUnitsCapacity(5).setSize(3, 3, 0).setCost(100);
	}
	
	public FarmLand(World w)
	{
		super(w);
		if(imgDirt[0] == null)
			imgDirt[0] = ContentManager.instance().getImage("build.farmland");
		if(imgDirt[1] == null)
			imgDirt[1] = ContentManager.instance().getImage("build.activeFarmland");
		if(imgCrops == null)
			imgCrops = ContentManager.instance().getImage("build.farmland.crops");
		if(imgRottenCrops == null)
			imgRottenCrops = ContentManager.instance().getImage("build.farmland.rottenCrops");
		ticksPerLevel = secondsToTicks(60);
		ticksBeforeNextLevel = ticksPerLevel;
		cropsLevel = MIN_LEVEL;
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
		}
		
		if(Cheats.isFastFarmlandGrow() && ticksBeforeNextLevel > secondsToTicks(1))
			ticksBeforeNextLevel = secondsToTicks(1);

		// Crops are growing
		ticksBeforeNextLevel--;
		if(ticksBeforeNextLevel <= 0)
		{
			onLevelUp();
			ticksBeforeNextLevel = ticksPerLevel;
		}
	}
		
	private void onLevelUp()
	{
		if(state == Building.ACTIVE)
		{
			if(cropsLevel == ROTTEN_LEVEL)
				cropsLevel = 0; // The field is cleaned up
			else if(cropsLevel != MAX_LEVEL)
				cropsLevel++; // Crops are growing normally
			else
				harvest();
		}
		else if(cropsLevel != 0)
			cropsLevel = ROTTEN_LEVEL; // Not enough employees to take care of the field...
	}
	
	private void harvest()
	{
		if(cropsLevel != MAX_LEVEL)
			return;
		
		for(Citizen emp : employees.values())
		{
			if(emp.getJob().getID() == Job.CONVEYER && !emp.isOut())
			{
				Conveyer job = (Conveyer)(emp.getJob());
				job.addResourceCarriage(new ResourceSlot(Resource.WHEAT, 100));
				emp.exitBuilding();
			}
		}

		cropsLevel = 0; // The field is cleaned up
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		int gx = posX * Game.tilesSize;
		int gy = posY * Game.tilesSize;

		// Soil
		if(state == Building.NORMAL || needEmployees())
			gfx.drawImage(imgDirt[0], gx, gy);
		else
			gfx.drawImage(imgDirt[1], gx, gy);
		
		// Crops
		if(state == Building.ACTIVE || cropsLevel != 0)
		{
			for(int j = 0; j < 3; j++)
			{
				for(int i = 0; i < 3; i++)
				{
					if(cropsLevel != ROTTEN_LEVEL)
					{
						gfx.drawImage(imgCrops,
								gx + i * Game.tilesSize,
								gy + j * Game.tilesSize,
								gx + (i+1) * Game.tilesSize,
								gy + (j+1) * Game.tilesSize,
								cropsLevel * Game.tilesSize,
								0,
								(cropsLevel + 1) * Game.tilesSize,
								Game.tilesSize
						);
					}
					else
						gfx.drawImage(imgRottenCrops, gx + i * Game.tilesSize, gy + j * Game.tilesSize);
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
		if(cropsLevel == ROTTEN_LEVEL)
			return 0;
		return (int) (100.f * ((cropsLevel + 1.f - (float)ticksBeforeNextLevel / ticksPerLevel) / (MAX_LEVEL+1)));
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
			// 3 farmers, 2 conveyers
			Job job;
			if(employees.size() <= 3)
				job = new InternalJob(citizen, this, Job.FARMER);
			else
				job = new Conveyer(citizen, this);
			addEmployee(citizen);
			return job;
		}
		return null;
	}

	@Override
	public void onInit()
	{
	}
	
}


