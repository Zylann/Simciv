package simciv.builds;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import backend.geom.Vector2i;
import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;

import simciv.Game;
import simciv.Map;
import simciv.Resource;
import simciv.ResourceSlot;
import simciv.content.Content;
import simciv.units.Jobs;
import simciv.units.Lumberjack;

/**
 * This build sends lumberjacks to extract logs from environning trees.
 * Will not function if there is not enough trees nearby.
 * @author Marc
 *
 */
public class Loggers extends Workplace
{
	private static final long serialVersionUID = 1L;
	
	private static BuildProperties properties;
	private static final int MAX_LOG_STACKS = 4;
	private static final int PATHFINDING_DISTANCE = 1024;
	private static final ResourceSlot renderLogs; // Used for rendering
	
	static
	{
		properties = new BuildProperties("Loggers");
		properties.setCost(150).setCategory(BuildCategory.INDUSTRY)
			.setSize(3, 3, 1).setUnitsCapacity(8);
		
		renderLogs = new ResourceSlot(Resource.LOGS, 100);
	}
	
	private byte logStacks;

	public Loggers(Map m)
	{
		super(m);
	}

	@Override
	public int getProductionProgress()
	{
		return 0;
	}
	
	private boolean checkMapForWood()
	{
		IMapSpec walkableFloor = new WalkableFloor();
		ArrayList<Vector2i> positionsAround = getPositionsAround(walkableFloor);
		if(positionsAround.isEmpty())
			return false;
		
		mapRef.multiPathFinder.setMaxDistance(PATHFINDING_DISTANCE);
		mapRef.multiPathFinder.setFindBlockedTargets(true);
		
		Vector2i pos = positionsAround.get(0);
		List<Vector2i> path = mapRef.multiPathFinder.findPath(
				pos.x, pos.y, walkableFloor, new TreeTarget());
				
		return path != null;
	}
	
	private void sendLumberjack()
	{
		if(units.size() < 2)
		{
			Log.debug("Sending lumberjack");
			addAndSpawnUnitsAround(Jobs.LUMBERJACK, 1);
		}
	}

	@Override
	protected void onActivityStart()
	{
		if(logStacks != MAX_LOG_STACKS && checkMapForWood())
			sendLumberjack();
	}

	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
	}

	@Override
	protected void tickActivity()
	{
		if(getTicks() % 8 == 0)
		{
			if(logStacks != MAX_LOG_STACKS && checkMapForWood())
				sendLumberjack();
		}
	}
	
	public void onLumberjackReturn(boolean withLogs)
	{
		if(withLogs)
		{
			Log.debug("lumberjack is back with logs.");
			if(logStacks == MAX_LOG_STACKS)
				Log.debug("--- But the storage is full. Will be lost.");
			else
				logStacks++;
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
		renderDefault(gfx, Content.sprites.buildLoggers);
		renderLogs(gfx);
	}
	
	public boolean containsLogs()
	{
		return logStacks != 0;
	}
	
	public boolean retrieveLogs(ResourceSlot slot)
	{
		ResourceSlot logs = new ResourceSlot(Resource.LOGS, 100);
		return slot.addAllFrom(logs);
	}
	
	private void renderLogs(Graphics gfx)
	{
		if(logStacks >= 1)
		{
			gfx.pushTransform();
			gfx.translate(3, Game.tilesSize);
			
			renderLogs.renderStorage(gfx, 0, 0);
			if(logStacks >= 2)
			{
				renderLogs.renderStorage(gfx, 0, Game.tilesSize / 2);
				if(logStacks >= 3)
				{
					gfx.translate(2 * Game.tilesSize - 5, 0);
					
					renderLogs.renderStorage(gfx, 0, 0);
					if(logStacks == MAX_LOG_STACKS)
						renderLogs.renderStorage(gfx, 0, Game.tilesSize / 2);
				}
			}
			
			gfx.popTransform();
		}
	}
	
	private class WalkableFloor implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.isWalkable(x, y);
		}	
	}
	
	private class TreeTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return Lumberjack.isCompliantTree(x, y, mapRef);
		}		
	}

}


