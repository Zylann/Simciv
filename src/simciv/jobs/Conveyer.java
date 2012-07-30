package simciv.jobs;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import simciv.Direction2D;
import simciv.PathFinder;
import simciv.ResourceSlot;
import simciv.Vector2i;
import simciv.buildings.Building;
import simciv.buildings.Workplace;
import simciv.content.Content;
import simciv.maptargets.BuildingMapTarget;
import simciv.maptargets.FreeWarehouseMapTarget;
import simciv.maptargets.IMapTarget;
import simciv.movements.PathMovement;
import simciv.units.Citizen;
import simciv.units.Unit;

/**
 * A conveyer can carry resources from a place to another.
 * @author Marc
 *
 */
public class Conveyer extends Job
{
	// Sprites
	private static Image unitSprites;
	
	private PathFinder pathFinder;
	private ResourceSlot carriedResource;
	
	public Conveyer(Citizen citizen, Workplace workplace)
	{
		super(citizen, workplace);
		if(unitSprites == null)
			unitSprites = Content.images.unitConveyer;
		carriedResource = new ResourceSlot();
	}
	
	@Override
	public void onBegin()
	{
		me.enterBuilding(workplaceRef);
		me.setMovement(null);
	}
	
	@Override
	public void onQuit(boolean notifyWorkplace)
	{
		me.setState(Unit.NORMAL);
		super.onQuit(notifyWorkplace);
	}
	
	@Override
	public byte getID()
	{
		return Job.CONVEYER;
	}

	public void addResourceCarriage(ResourceSlot r)
	{
		carriedResource.addFrom(r);
	}

	@Override
	public void tick()
	{
		if(!me.isOut())
			return;
		
		// Current behavior :
		// The conveyer goes out of its workplace with resources.
		// He moves at random, distributing resources.
		// When he distributed all his resources, he goes back to its workplace.
		
//		System.out.println("--- " + me.getX() + ", " + me.getY() + " at time " + me.getTicks()); // debug
		if(me.getState() == Unit.THINKING && pathFinder != null)
			tickPathFinding(); // I am searching for a path
		else if(me.isMovementFinished())
			onPathFinished();
		else if(me.isMovementBlocked())
			restartPathFinding(me.getMovementTarget());
		else
			tickDefault();
	}
	
	private void tickPathFinding()
	{
//		System.out.println("Thinking..."); // debug
		pathFinder.step(8);
		if(pathFinder.isFinished())
		{
			if(pathFinder.getState() == PathFinder.FOUND)
			{
				// I found a path !
				LinkedList<Vector2i> path = pathFinder.retrievePath();
//				System.out.println("Path found"); // debug
				
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
	
	/**
	 * Called when the conveyer is on his way
	 */
	protected void tickDefault()
	{
		if(carriedResource.isEmpty()) // I have no resource to distribute
		{
			// Go back to workplace
//			System.out.println("Back to workplace"); // debug
			if(me.getMovementTarget() == null || getTargetBuildingID() != workplaceRef.getID())
				setTarget(workplaceRef);
		}
		else // I have resource to distribute
		{
//			System.out.println("Random move"); // debug
			distributeResources(); // distribute it
		}
	}
		
	protected void onPathFinished()
	{
		if(!carriedResource.isEmpty())
			distributeResources(); // distribute resources
		if(!carriedResource.isEmpty()) // if still left, search another warehouse
			restartPathFinding(new FreeWarehouseMapTarget());
		
		if(getTargetBuildingID() == workplaceRef.getID())
		{
//			System.out.println("End of mission"); // debug
			// End of mission, ready for the next
			me.setMovement(null);
			me.enterBuilding(workplaceRef);
		}
	}
	
	private void distributeResources()
	{
		List<Building> buildingsAround = me.getWorld().getBuildingsAround(me.getX(), me.getY());
		for(Building b : buildingsAround)
		{
			if(b.isAcceptResources())
			{
//				System.out.println("R distributed"); // debug
				b.storeResource(carriedResource);
			}
		}
		if(carriedResource.isEmpty()) // if no resources left,
			setTarget(workplaceRef); // Back to my workplace
	}
	
	private int getTargetBuildingID()
	{
		if(BuildingMapTarget.class.isInstance(me.getMovementTarget()))
			return ((BuildingMapTarget)(me.getMovementTarget())).buildingID;
		return -1;
	}
	
	public void setTarget(Building b)
	{
		setTarget(new BuildingMapTarget(b.getID()));
	}
	
	public void setTarget(IMapTarget target)
	{
		restartPathFinding(target);
	}
		
	private void restartPathFinding(IMapTarget target)
	{
//		System.out.println("Restarting path finding"); // debug
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
		if(me.getDirection() == Direction2D.NORTH)
		{
			carriedResource.renderCarriage(gfx, 0, 0, me.getDirection());
			me.defaultRender(gfx, unitSprites);
		}
		else
		{
			me.defaultRender(gfx, unitSprites);
			carriedResource.renderCarriage(gfx, 0, 0, me.getDirection());
		}
	}

}



