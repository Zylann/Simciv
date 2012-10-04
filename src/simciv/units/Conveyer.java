package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;
import backend.Direction2D;
import backend.pathfinding.IMapTarget;
import simciv.Entity;
import simciv.Map;
import simciv.ResourceSlot;
import simciv.builds.Build;
import simciv.builds.Warehouse;
import simciv.builds.Workplace;
import simciv.content.Content;

/**
 * A conveyer can carry resources from a place to another.
 * It can be employed in different workplaces.
 * @author Marc
 *
 */
public class Conveyer extends Citizen
{
	private static final long serialVersionUID = 1L;
	private static final int PATHFINDING_DISTANCE = 4092;
	
	// States
	private static final byte FIND_STORAGE = 0;
	private static final byte STORE_RESOURCES = 1;
	private static final byte BACK_TO_WORKPLACE = 2;
	private static final byte FIND_ROAD = 3;
	
	/** Resources carried by the conveyer **/
	private ResourceSlot carriedResource;
	
	/** Last state of the conveyer **/
	private byte lastState;
	
	public Conveyer(Map m, Workplace w)
	{
		super(m, w);
		carriedResource = new ResourceSlot();
		state = FIND_STORAGE;
	}
	
	public void addResourceCarriage(ResourceSlot r)
	{
		carriedResource.addAllFrom(r);
	}
	
	@Override
	public void tick()
	{
		/*
		 * Current behavior :
		 * The conveyer goes out of its workplace with resources.
		 * He moves at random, distributing resources.
		 * When he distributed all his resources, he goes back to its workplace.
		 */
		
		byte lastStateTemp = state;
		
		switch(state)
		{
		case FIND_STORAGE : tickFindStorage(); break;
		case STORE_RESOURCES : tickStoreResources(); break;
		case BACK_TO_WORKPLACE : tickBackToWorkplace(); break;
		case FIND_ROAD : tickFindRoad(); break;
		}
		
		lastState = lastStateTemp;
	}
	
	private void tickFindStorage()
	{
		if(state != lastState || isMovementBlocked())
		{
			setMovement(null);
			if(!isOnRoad())
				state = FIND_ROAD;
		}
		
		if(!isMovement())
			findAndGoTo(new FreeWarehouseTarget(), PATHFINDING_DISTANCE);
		
		if(isMovementFinished())
			state = STORE_RESOURCES;
	}

	private void tickStoreResources()
	{
		if(state != lastState)
			distributeResources();
		
		if(carriedResource.isEmpty())
			state = BACK_TO_WORKPLACE;
		else
			state = FIND_STORAGE;
	}

	private void tickBackToWorkplace()
	{
		if(state != lastState || isMovementBlocked())
		{
			setMovement(null);
			if(!isOnRoad())
				state = FIND_ROAD;
		}
				
		if(isMovement())
		{
			if(isMovementFinished())
				dispose();
		}
		else
		{
			if(isMyWorkplaceNearby())
				dispose();
			else
				findAndGoTo(new WorkplaceTarget(), PATHFINDING_DISTANCE);
		}
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
				state = BACK_TO_WORKPLACE;
			else
				state = FIND_STORAGE;
		}
	}
	
	@Override
	public String getInfoLine()
	{
		String stateName = "";
		
		switch(getState())
		{
		case Entity.DEFAULT_STATE : stateName = "default state"; break;
		case BACK_TO_WORKPLACE : stateName = "back to workplace"; break;
		case FIND_ROAD : stateName = "finding a road"; break;
		case FIND_STORAGE : stateName = "Finding storage"; break;
		case STORE_RESOURCES : stateName = "Storing resources"; break;
		}
		
		return super.getInfoLine() + " " + stateName;
	}

	@Override
	public String getDisplayableName()
	{
		return "Conveyer";
	}

	/**
	 * Distributes resources to neighboring builds if possible
	 */
	private void distributeResources()
	{
		if(carriedResource.isEmpty())
			return;
		List<Build> buildingsAround = getMap().getBuildsAround(getX(), getY());
		for(Build b : buildingsAround)
		{
			if(b.isAcceptResources())
				b.storeResource(carriedResource);
		}
	}
	
	@Override
	public void renderUnit(Graphics gfx)
	{
		if(getDirection() == Direction2D.NORTH)
		{
			carriedResource.renderCarriage(gfx, 0, 0, getDirection());
			renderDefault(gfx, Content.sprites.unitConveyer);
		}
		else
		{
			renderDefault(gfx, Content.sprites.unitConveyer);
			carriedResource.renderCarriage(gfx, 0, 0, getDirection());
		}
	}
	
	private class FreeWarehouseTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			Build b = mapRef.getBuild(x, y);
			if(b != null && Warehouse.class.isInstance(b))
			{
				Warehouse w = (Warehouse)b;
				return w.canStore(carriedResource.getType());
			}
			return false;
		}
	}
	
	private class WorkplaceTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return mapRef.grid.getBuildID(x, y) == getWorkplaceID();
		}
	}

}



