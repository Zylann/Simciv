package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Cheats;
import simciv.Game;
import simciv.Map;
import simciv.Resource;
import simciv.ResourceSlot;
import simciv.World;
import simciv.content.Content;
import simciv.jobs.Conveyer;
import simciv.jobs.InternalJob;
import simciv.jobs.Job;
import simciv.maptargets.FreeWarehouseMapTarget;
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
	private static BuildProperties properties;
	private static SpriteSheet sprites;
	private static SpriteSheet cropsSprites;

	private static byte MIN_LEVEL = 0;
	private static byte MAX_LEVEL = 7;
	private static byte ROTTEN_LEVEL = 8;
	
	private byte cropsLevel;
	private transient int ticksPerLevel; // how many ticks are needed to increase crops level?
	private int ticksBeforeNextLevel; // how many ticks remains before the next level?
	
	static
	{
		properties = new BuildProperties("Farmland");
		properties.setUnitsCapacity(5).setSize(3, 3, 0).setCost(10).setCategory(BuildCategory.FOOD);
	}
	
	public FarmLand(World w)
	{
		super(w);
		ticksPerLevel = secondsToTicks(60);
		ticksBeforeNextLevel = ticksPerLevel;
		cropsLevel = MIN_LEVEL;
		state = Build.STATE_NORMAL;
		
		if(sprites == null)
		{
			sprites = new SpriteSheet(Content.images.buildFarmland,
					getWidth() * Game.tilesSize,
					getHeight() * Game.tilesSize);
			cropsSprites = new SpriteSheet(Content.images.buildFarmlandCrops,
					Game.tilesSize,
					Game.tilesSize);
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
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
	
	@Override
	protected void tickSolidness()
	{
		// No solidness
	}
	
	private void onLevelUp()
	{
		if(state == Build.STATE_ACTIVE)
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
		
		// Tell free conveyers to distribute crops
		for(Citizen emp : employees.values())
		{
			if(emp.getJob().getID() == Job.CONVEYER && !emp.isOut())
			{
				Conveyer job = (Conveyer)(emp.getJob());
				int harvestResult = (int) (50 + 25.f * Math.random());
				job.addResourceCarriage(new ResourceSlot(Resource.WHEAT, harvestResult));
				emp.exitBuilding();
				emp.findAndGoTo(new FreeWarehouseMapTarget());
			}
		}

		cropsLevel = 0; // The field is cleaned up
	}

	@Override
	public void renderBuilding(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		// Soil
		if(state == Build.STATE_NORMAL || needEmployees())
			gfx.drawImage(sprites.getSprite(0, 0), 0, 0);
		else
			gfx.drawImage(sprites.getSprite(1, 0), 0, 0);
		
		// Crops
		if(state == Build.STATE_ACTIVE || cropsLevel != 0)
		{
			for(int j = 0; j < 3; j++)
			{
				for(int i = 0; i < 3; i++)
				{
					if(cropsLevel != ROTTEN_LEVEL)
					{
						gfx.drawImage(
								cropsSprites.getSprite(cropsLevel, 0),
								i * Game.tilesSize,
								j * Game.tilesSize);
					}
					else
						gfx.drawImage(Content.images.buildFarmlandRottenCrops, 
								i * Game.tilesSize,
								j * Game.tilesSize);
				}
			}
		}
	}

	@Override
	public BuildProperties getProperties()
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
		return 500; // 1/2 second
	}

	@Override
	public Job giveNextJob(Citizen citizen)
	{
		if(needEmployees())
		{
			// 4 farmers, 1 conveyer
			Job job;
			if(employees.size() < 4)
				job = new InternalJob(citizen, this, Job.FARMER);
			else
				job = new Conveyer(citizen, this);
			addEmployee(citizen);
			return job;
		}
		return null;
	}

	@Override
	public boolean canBePlaced(Map map, int x, int y)
	{
		return super.canBePlaced(map, x, y) && map.isArable(x, y, getWidth(), getHeight());
	}

	@Override
	protected void onActivityStart()
	{
	}

	@Override
	protected void onActivityStop()
	{
	}

	@Override
	protected void tickActivity()
	{
	}
	
}


