package simciv.builds;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import backend.geom.Vector2i;
import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;

import simciv.Entity;
import simciv.Game;
import simciv.Map;
import simciv.Resource;
import simciv.ResourceSlot;
import simciv.content.Content;
import simciv.units.Conveyer;
import simciv.units.Jobs;
import simciv.units.Unit;

public class Hunters extends Workplace
{
	private static final long serialVersionUID = 1L;
	
	private static BuildProperties properties;
	private static int TICKS_PER_FOOD_PRODUCTION = 16;
	private static final int PATHFINDING_DISTANCE = 1024;
	
	// States (when active)
	private static final byte SEARCH_ANIMALS = 0;
	private static final byte WAIT_FOR_HUNTER = 1;
	private static final byte PRODUCE = 2;
	
	static
	{
		properties = new BuildProperties("Hunters");
		properties.setCategory(BuildCategory.FOOD)
			.setCost(100)
			.setSize(2, 2, 1)
			.setUnitsCapacity(6);
	}
	
	private int ticksBeforeFoodExport;

	public Hunters(Map m)
	{
		super(m);
	}

	@Override
	public int getProductionProgress()
	{
		if(getState() == PRODUCE)
		{
			return (int) (100.f * (1.f - 
					(float)ticksBeforeFoodExport 
					/ (float)TICKS_PER_FOOD_PRODUCTION));
		}
		return 0;
	}
	
	private void sendHunterAndWait()
	{
		addAndSpawnUnitsAround(Jobs.HUNTER, 1);
		setState(WAIT_FOR_HUNTER);
	}

	@Override
	protected void onActivityStart()
	{
		if(checkMapForAnimals())
			sendHunterAndWait();
		else
			setState(SEARCH_ANIMALS);
	}
	
	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
		setState(Entity.DEFAULT_STATE);
		ticksBeforeFoodExport = 0;
	}

	@Override
	protected void tickActivity()
	{
		switch(getState())
		{
		case SEARCH_ANIMALS : tickSearchAnimals(); break;
		case WAIT_FOR_HUNTER : break;
		case PRODUCE : tickProduce(); break;
		}
	}
	
	private void tickSearchAnimals()
	{
		if(mapRef.getFaunaCount() > 0 && getTicks() % 4 == 0)
		{
			if(checkMapForAnimals())
				sendHunterAndWait();
		}
	}

	private void tickProduce()
	{
		ticksBeforeFoodExport--;
		if(ticksBeforeFoodExport <= 0)
		{
			exportFood();
			ticksBeforeFoodExport = 0;
			
			if(checkMapForAnimals())
				sendHunterAndWait();
			else
				setState(SEARCH_ANIMALS);
		}
	}
	
	private boolean checkMapForAnimals()
	{
		// TODO set a fauna count limit to let animals breeding?
		if(mapRef.getFaunaCount() == 0)
			return false;
		
		IMapSpec walkableFloor = new WalkableFloor();
		ArrayList<Vector2i> positionsAround = getPositionsAround(walkableFloor);
		if(positionsAround.isEmpty())
			return false;
		
		mapRef.multiPathFinder.setMaxDistance(PATHFINDING_DISTANCE);
		Vector2i pos = positionsAround.get(0);
		List<Vector2i> path = mapRef.multiPathFinder.findPath(
				pos.x, pos.y, walkableFloor, new PreyTarget());
		
		return path != null;
	}
	
	private boolean exportFood()
	{
		for(Integer uID : units)
		{
			Unit u = mapRef.getUnit(uID);
			if(Conveyer.class.isInstance(u)) {
				Log.debug(this + " can't export food, the conveyer is already out");
				return false;
			}
		}
		
		Conveyer conveyer = new Conveyer(mapRef, this);
		conveyer.addResourceCarriage(new ResourceSlot(Resource.MEAT, 50));
		addAndSpawnUnitAround(conveyer);
		
		return true;
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		SpriteSheet sprites = Content.sprites.buildHunters;
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

	public void onHunterBack(boolean withPrey)
	{
		if(withPrey)
		{
			setState(PRODUCE);
			ticksBeforeFoodExport = TICKS_PER_FOOD_PRODUCTION;
		}
		else
			setState(SEARCH_ANIMALS);
	}

	@Override
	public BuildReport getReport()
	{
		BuildReport report = super.getReport();
		
		if(isActive())
		{
			if(getState() == SEARCH_ANIMALS)
				report.add(BuildReport.PROBLEM_MINOR, 
						"There is no animals to hunt around here.");
			else if(getState() == PRODUCE)
				report.add(BuildReport.INFO, 
						"We are preparing meat from catched animals.");
			else if(getState() == WAIT_FOR_HUNTER)
				report.add(BuildReport.INFO, 
						"We are waiting for the hunter to comme back with a catch (we hope so !)");
		}
		
		return report;
	}

	private class PreyTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			Unit u = mapRef.getUnit(x, y);
			if(u != null)
				return u.isAnimal();
			return false;
		}
	}

	private class WalkableFloor implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.isWalkable(x, y);
		}	
	}
	
}


