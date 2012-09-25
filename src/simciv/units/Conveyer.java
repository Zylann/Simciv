package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;

import backend.Direction2D;
import backend.pathfinding.IMapTarget;
import simciv.Map;
import simciv.ResourceSlot;
import simciv.builds.Build;
import simciv.builds.Workplace;
import simciv.content.Content;

/**
 * A conveyer can carry resources from a place to another.
 * @author Marc
 *
 */
public class Conveyer extends Citizen
{
	private static final long serialVersionUID = 1L;
	private static final int PATHFINDING_DISTANCE = 128;
	
	private ResourceSlot carriedResource;
	
	public Conveyer(Map m, Workplace w)
	{
		super(m, w);
		carriedResource = new ResourceSlot();
	}
	
	public void addResourceCarriage(ResourceSlot r)
	{
		carriedResource.addAllFrom(r);
	}
	
	private IMapTarget getCurrentTarget()
	{
		if(carriedResource.isEmpty())
			return new WorkplaceTarget(); // I wanna go back to my workplace
		else
			return new FreeWarehouseTarget(); // I wanna distribute my carriage
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
		
		if(!isMovement() || isMovementBlocked())
			findAndGoTo(getCurrentTarget(), PATHFINDING_DISTANCE);
		
		if(isMovementFinished())
		{
			if(!carriedResource.isEmpty())
				distributeResources(); // distribute resources
			else
				dispose();
		}
	}
		
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
			defaultRender(gfx, Content.sprites.unitConveyer);
		}
		else
		{
			defaultRender(gfx, Content.sprites.unitConveyer);
			carriedResource.renderCarriage(gfx, 0, 0, getDirection());
		}
	}
	
	private class FreeWarehouseTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return mapRef.getFreeWarehouse(x, y) != null;
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



