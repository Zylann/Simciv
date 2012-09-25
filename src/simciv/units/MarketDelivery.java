package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;

import backend.pathfinding.IMapTarget;

import simciv.Map;
import simciv.ResourceSlot;
import simciv.builds.Build;
import simciv.builds.House;
import simciv.builds.Warehouse;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.movement.RandomRoadMovement;

/**
 * Delivers resources to citizen houses.
 * @author Marc
 *
 */
public class MarketDelivery extends Citizen
{
	private static final long serialVersionUID = 1L;
	private static final int PATHFINDING_DISTANCE = 128;
	
	private static final byte FIND_RESOURCES = 0;
	private static final byte GET_RESOURCES = 1;
	private static final byte PATROL = 2;
	private static final byte FIND_ROAD = 3;

	private ResourceSlot carriedResource;
	private byte lastState;
	
	public MarketDelivery(Map m, Workplace workplace)
	{
		super(m, workplace);
		//setMovement(new RandomRoadMovement());
		carriedResource = new ResourceSlot();		
	}
	
	public void addResourceCarriage(ResourceSlot r)
	{
		carriedResource.addAllFrom(r);
	}
	
	@Override
	public void tick()
	{
		/*
		 * Behavior :
		 * 1) Begins mission at workplace ;
		 * 2) If have resources, go to 5).
		 * 3) Go to a valid warehouse
		 * 4) Get food from the warehouse ;
		 * 5) Distribute resources at random while carrying it ;
		 * go to 2).
		 */
		
		byte stateTemp = state;
		
		switch(state)
		{
		case FIND_RESOURCES : tickFindResources(); break;
		case GET_RESOURCES : tickGetResources(); break;
		case PATROL : tickPatrol(); break;
		case FIND_ROAD : tickFindRoad(); break;
		}
		
		lastState = stateTemp;
		
		if(carriedResource.isEmpty())
		{
			if(!isMovement())
				findAndGoTo(new WarehouseForMarketTarget(), PATHFINDING_DISTANCE);
			if(isMovementFinished())
				retrieveResourcesIfPossible();
		}
		else
		{
			if(!isMovement())
				setMovement(new RandomRoadMovement());			
			distributeResources();
		}
	}
	
	private void tickFindResources()
	{
		if(state != lastState || isMovementBlocked())
		{
			setMovement(null);
			if(!isOnRoad())
				state = FIND_ROAD;
		}
		
		if(!isMovement())
			findAndGoTo(new WarehouseForMarketTarget(), PATHFINDING_DISTANCE);
		
		if(isMovementFinished())
			state = GET_RESOURCES;
	}

	private void tickGetResources()
	{
		if(state != lastState)
		{
			setMovement(null);
			retrieveResourcesIfPossible();
		}
		if(carriedResource.isEmpty())
			state = FIND_RESOURCES;
		else
			state = PATROL;
	}

	private void tickPatrol()
	{
		if(state != lastState)
			setMovement(new RandomRoadMovement());
		
		distributeResources();
		
		if(carriedResource.isEmpty())
			state = FIND_RESOURCES;
		
		if(!isOnRoad())
			state = FIND_ROAD;
	}

	private void tickFindRoad()
	{
		if(state != lastState || isMovementBlocked())
			setMovement(null);
		
		if(!isMovement())
			goBackToRoad(32);
		
		if(isMovementFinished())
		{
			if(carriedResource.isEmpty())
				state = FIND_RESOURCES;
			else
				state = PATROL;
		}
	}

	/**
	 * Distributes resources to each house nearby
	 */
	private void distributeResources()
	{
		if(carriedResource.isEmpty())
			return;
		List<Build> buildings = mapRef.getBuildsAround(getX(), getY());
		for(Build b : buildings)
		{
			if(b.isHouse())
				((House)b).onDistributedResource(carriedResource);
		}
	}

	private void retrieveResourcesIfPossible()
	{
		List<Build> buildings = mapRef.getBuildsAround(getX(), getY());
		for(Build b : buildings)
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

	@Override
	public void renderUnit(Graphics gfx)
	{
		if(carriedResource.isEmpty())
			renderDefault(gfx, Content.sprites.unitMarketDelivery, 4); // render without bag
		else
			renderDefault(gfx, Content.sprites.unitMarketDelivery); // render with a bag
	}
	
	private class WarehouseForMarketTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			List<Build> list = mapRef.getBuildsAround(x, y);
			for(Build b : list)
			{
				if(Warehouse.class.isInstance(b))
				{
					if(((Warehouse) b).containsResourcesForMarkets())
						return true;
				}
			}
			return false;
		}
	}

}


