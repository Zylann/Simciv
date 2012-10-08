package simciv.builds;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import backend.MathHelper;
import backend.geom.Vector2i;
import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;

import simciv.Game;
import simciv.Map;
import simciv.content.Content;
import simciv.resources.Resource;
import simciv.resources.ResourceSlot;
import simciv.units.Conveyer;
import simciv.units.GetConveyer;
import simciv.units.StoreConveyer;

/**
 * This building manufactures wood from logs.
 * @author Marc
 *
 */
public class WoodManufacture extends Workplace
{
	private static final long serialVersionUID = 1L;
	
	private static final int PATHFINDING_DISTANCE = 1024;
	private static BuildProperties properties;
	
	/** Wood production time in seconds **/
	private static final int PRODUCE_TIME = 16;
	
	// States
	private static final byte FIND_LOGS = 0;
	private static final byte WAIT_FOR_CONVEYER = 1;
	private static final byte PRODUCE = 2;
	
	private int ticksBeforeExport;
	
	static
	{
		properties = new BuildProperties("Wood manufacture");
		properties.setCategory(BuildCategory.INDUSTRY).setCost(250)
			.setSize(3, 3, 1).setUnitsCapacity(12);
	}

	public WoodManufacture(Map m)
	{
		super(m);
	}

	@Override
	public int getProductionProgress()
	{
		if(getState() == PRODUCE)
			return 100 - MathHelper.percent(
					ticksBeforeExport, secondsToTicks(PRODUCE_TIME));
		return 0;
	}
	
	private boolean checkMapForLogs()
	{
		IMapSpec roads = new RoadTest();
		ArrayList<Vector2i> positionsAround = getPositionsAround(roads);
		if(positionsAround.isEmpty())
			return false;
		
		mapRef.multiPathFinder.setMaxDistance(PATHFINDING_DISTANCE);
		Vector2i pos = positionsAround.get(0);
		List<Vector2i> path = mapRef.multiPathFinder.findPath(
				pos.x, pos.y, roads, new LogsTarget());
		
		return path != null;
	}

	@Override
	protected void onActivityStart()
	{
		setState(FIND_LOGS);
	}

	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
	}

	@Override
	protected void tickActivity()
	{
		switch(getState())
		{
		case FIND_LOGS : tickFindLogs(); break;
		case PRODUCE : tickProduce(); break;
		case WAIT_FOR_CONVEYER : break;
		}
	}

	private void tickFindLogs()
	{
		if(getTicks() % 8 == 0)
		{
			if(checkMapForLogs())
			{
				GetConveyer conv = new GetConveyer(mapRef, this);
				conv.setWantedResource(Resource.LOGS, 100);
				addAndSpawnUnitAround(conv);
				setState(WAIT_FOR_CONVEYER);
			}
		}
	}

	private void tickProduce()
	{
		ticksBeforeExport--;
		if(ticksBeforeExport == 0)
		{
			StoreConveyer conv = new StoreConveyer(mapRef, this);
			conv.addResourceCarriage(new ResourceSlot(Resource.WOOD, 60));
			addAndSpawnUnitAround(conv);
			setState(FIND_LOGS);
		}
	}
	
	@Override
	public void onConveyerIsBack(Conveyer conveyer)
	{
		if(conveyer.getResourceType() == Resource.LOGS &&
			conveyer.getResourceAmount() > 0)
		{
			ticksBeforeExport = secondsToTicks(PRODUCE_TIME);
			setState(PRODUCE);
		}
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		SpriteSheet sprites = Content.sprites.buildWoodManufacture;
		if(isActive())
		{
			if(getState() == PRODUCE)
				gfx.drawImage(sprites.getSprite(2, 0), 0, -Game.tilesSize);
			else
				gfx.drawImage(sprites.getSprite(1, 0), 0, -Game.tilesSize);
		}
		else
			gfx.drawImage(sprites.getSprite(0, 0), 0, -Game.tilesSize);
	}
	
	private class RoadTest implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.grid.isRoad(x, y);
		}
	}
	
	private class LogsTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y)
		{
			Build b = mapRef.getBuild(x, y);
			if(b != null && IResourceHolder.class.isInstance(b))
			{
				IResourceHolder rh = (IResourceHolder)b;
				if(rh.allowsRetrieving())
					return rh.getResourceTotal(Resource.LOGS) > 0;
			}
			return false;
		}
	}

}



