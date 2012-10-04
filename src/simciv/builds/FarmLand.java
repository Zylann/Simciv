package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Cheats;
import simciv.Game;
import simciv.MapGrid;
import simciv.Resource;
import simciv.ResourceSlot;
import simciv.Map;
import simciv.content.Content;
import simciv.units.Conveyer;

/**
 * Farmlands are used to raise one type of plant.
 * It will grow slowly (level) until it reaches the max level.
 * At max level, the land can be farmed.
 * @author Marc
 *
 */
public class FarmLand extends Workplace
{
	private static final long serialVersionUID = 1L;

	private static BuildProperties properties;

	private static byte MIN_LEVEL = 0;
	private static byte MAX_LEVEL = 7;
	private static byte ROTTEN_LEVEL = 8;
	
	private byte cropsLevel;
	private int ticksBeforeNextLevel; // how many ticks remains before the next level?
	
	static
	{
		properties = new BuildProperties("Farmland");
		properties
			.setUnitsCapacity(5).setSize(3, 3, 0).setCost(10)
			.setCategory(BuildCategory.FOOD)
			.setRepeatable(true)
			.setFlamable(false);
	}
	
	public FarmLand(Map m)
	{
		super(m);
		ticksBeforeNextLevel = getTicksPerCropsLevel();
		cropsLevel = MIN_LEVEL;
	}
	
	/**
	 * Returns how many ticks are needed to increase crops level
	 * @return
	 */
	public int getTicksPerCropsLevel()
	{
		return secondsToTicks(60);
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
			ticksBeforeNextLevel = getTicksPerCropsLevel();
		}
	}
	
	@Override
	protected void tickSolidness()
	{
		// No solidness
	}
	
	private void onLevelUp()
	{
		if(isActive())
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
		
		if(!units.isEmpty()) // The conveyer is already working
			return;
		
		int harvestResult = (int) (50 + 25.f * Math.random());

		Conveyer conveyers[] = new Conveyer[1];
		conveyers[0] = new Conveyer(mapRef, this);
		conveyers[0].addResourceCarriage(new ResourceSlot(Resource.WHEAT, harvestResult));
		
		addAndSpawnUnitsAround(conveyers);
		
		cropsLevel = 0; // The field is cleaned up
	}

	@Override
	public void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		// Soil
		if(!isActive() || needEmployees())
			gfx.drawImage(Content.sprites.buildFarmland.getSprite(0, 0), 0, 0);
		else
			gfx.drawImage(Content.sprites.buildFarmland.getSprite(1, 0), 0, 0);
		
		// Crops
		if(isActive() || cropsLevel != 0)
		{
			for(int j = 0; j < 3; j++)
			{
				for(int i = 0; i < 3; i++)
				{
					if(cropsLevel != ROTTEN_LEVEL)
					{
						gfx.drawImage(
							Content.sprites.buildFarmlandCrops.getSprite(cropsLevel, 0),
							i * Game.tilesSize,
							j * Game.tilesSize);
					}
					else
						gfx.drawImage(
							Content.sprites.buildFarmlandRottenCrops, 
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
		return (int)
			(100.f * ((cropsLevel + 1.f - (float)ticksBeforeNextLevel
				/ getTicksPerCropsLevel()) / (MAX_LEVEL+1)));
	}

	@Override
	public int getTickTime()
	{
		return 500; // 1/2 second
	}

	@Override
	public boolean canBePlaced(MapGrid map, int x, int y)
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
	
	@Override
	public BuildReport getReport()
	{
		BuildReport report = super.getReport();
		
		if(cropsLevel == ROTTEN_LEVEL)
			report.add(BuildReport.PROBLEM_MINOR, "We have lost our crops...");

		return report;
	}
	
}


