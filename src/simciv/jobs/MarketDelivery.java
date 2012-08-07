package simciv.jobs;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.Direction2D;
import simciv.Game;
import simciv.PathFinder;
import simciv.ResourceSlot;
import simciv.Vector2i;
import simciv.buildings.Building;
import simciv.buildings.House;
import simciv.buildings.Warehouse;
import simciv.buildings.Workplace;
import simciv.content.Content;
import simciv.maptargets.BuildingMapTarget;
import simciv.maptargets.IMapTarget;
import simciv.maptargets.WarehouseForMarketMapTarget;
import simciv.movements.PathMovement;
import simciv.movements.RandomRoadMovement;
import simciv.units.Citizen;
import simciv.units.Unit;

/**
 * Deliver resources to citizen houses
 * @author Marc
 *
 */
public class MarketDelivery extends Job
{
	private static SpriteSheet unitSprites;
	
	private ResourceSlot carriedResource;
	private PathFinder pathFinder;
	
	public MarketDelivery(Citizen citizen, Workplace workplace)
	{
		super(citizen, workplace);
		carriedResource = new ResourceSlot();
		if(unitSprites == null)
			unitSprites = new SpriteSheet(Content.images.unitMarketDelivery, Game.tilesSize, Game.tilesSize);
	}

	@Override
	public void onBegin()
	{
		me.enterBuilding(workplaceRef);
	}
	
	public void addResourceCarriage(ResourceSlot r)
	{
		carriedResource.addAllFrom(r);
	}
	
	@Override
	public void tick()
	{
		if(!me.isOut())
			return;

		/*
		 * Behavior :
		 * 1) Begins mission at workplace ;
		 * 2) If have resources, go to 5).
		 * 3) Go to a valid warehouse
		 * 4) Get food from the warehouse ;
		 * 5) Distribute resources at random while carrying it ;
		 * go to 2).
		 */
		
		if(me.getState() == Unit.THINKING)
		{
			if(pathFinder != null)
				tickPathFinding(); // I am searching for a path
		}
		else
		{
			if(carriedResource.isEmpty() && !me.isMovement())
			{
				if(pathFinder == null)
					restartPathFinding(new WarehouseForMarketMapTarget());
			}
			else
			{
				boolean wasEmpty = carriedResource.isEmpty();
				distributeResources();
				if(!wasEmpty && carriedResource.isEmpty())
				{
					System.out.println("reload2 " + wasEmpty + ", " + carriedResource.isEmpty() + ", " + carriedResource.getAmount());
					restartPathFinding(new WarehouseForMarketMapTarget());
				}
				else
				{				
					if(me.isMovementBlocked())
						restartPathFinding(me.getMovementTarget());
					else if(me.isMovementFinished())
					{
						retrieveResourcesIfPossible();
						if(carriedResource.isEmpty())
							restartPathFinding(me.getMovementTarget());
						else
							me.setMovement(new RandomRoadMovement());
					}
				}
			}
		}
	}
	
	private void distributeResources()
	{
		List<Building> buildings = me.getWorld().getBuildingsAround(me.getX(), me.getY());
		for(Building b : buildings)
		{
			if(b.isHouse())
				((House)b).onDistributedResource(carriedResource);
		}
	}

	private void retrieveResourcesIfPossible()
	{
		List<Building> buildings = me.getWorld().getBuildingsAround(me.getX(), me.getY());
		for(Building b : buildings)
		{
			if(Warehouse.class.isInstance(b))
			{
				Warehouse w = (Warehouse)b;
				w.retrieveResource(carriedResource);
			}
			if(carriedResource.isFull())
				return;
		}
	}

	private void tickPathFinding()
	{
		pathFinder.step(8);
		if(pathFinder.isFinished())
		{
			if(pathFinder.getState() == PathFinder.FOUND)
			{
				// I found a path !
				LinkedList<Vector2i> path = pathFinder.retrievePath();
				
				// Remove first pos if we already are on 
				if(path != null && !path.isEmpty())
				{
					if(path.getFirst().equals(me.getX(), me.getY()))
						path.pop();
				}
				
				// Start following the path
				me.setState(Unit.NORMAL);
				me.setMovement(new PathMovement(path, pathFinder.getTarget()));
				pathFinder = null;
			}
			else
				restartPathFinding(pathFinder.getTarget());
		}
	}
	
	private void restartPathFinding(IMapTarget target)
	{
		if(target == null)
			target = new BuildingMapTarget(workplaceRef.getID());
		
		pathFinder = new PathFinder(me.getWorld(), me.getX(), me.getY(), target);
		
		me.setState(Unit.THINKING);
		me.setDirection(Direction2D.NONE);
		me.setMovement(null);
	}

	@Override
	public void renderUnit(Graphics gfx)
	{
		if(carriedResource.isEmpty())
			me.defaultRender(gfx, unitSprites, 4); // render without bag
		else
			me.defaultRender(gfx, unitSprites); // render with a bag
	}

	@Override
	public byte getID()
	{
		return Job.MARKET_DELIVERY;
	}

	@Override
	public int getIncome()
	{
		return 14;
	}

}


