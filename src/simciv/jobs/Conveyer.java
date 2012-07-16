package simciv.jobs;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Image;

import simciv.ContentManager;
import simciv.Direction2D;
import simciv.Job;
import simciv.PathFinder;
import simciv.ResourceSlot;
import simciv.Vector2i;
import simciv.buildings.Building;
import simciv.buildings.Workplace;
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
	
	private Building targetBuildingRef;
	private PathFinder pathFinder;
	private LinkedList<Vector2i> path;
	private ResourceSlot carriedResource;
	
	public Conveyer(Citizen citizen, Workplace workplace)
	{
		super(citizen, workplace);
		if(unitSprites == null)
			unitSprites = ContentManager.instance().getImage("city.conveyer");
		carriedResource = new ResourceSlot();
	}
	
	@Override
	public void onBegin()
	{
		me.enterBuilding(workplaceRef);
	}
	
	@Override
	public void onQuit(boolean notifyWorkplace)
	{
		me.setState(Unit.NORMAL);
		super.onQuit(notifyWorkplace);
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
		
		//System.out.println("--- " + me.getX() + ", " + me.getY() + " at time " + me.getTicks()); // debug
		if(me.getState() == Unit.THINKING && pathFinder != null)
			tickPathFinding();
		else if(path == null || path.isEmpty()) // I finished my path
			tickNoPathMovement();
		else // I have to follow my path
			tickPathDrivenMovement();
	}
	
	private void tickPathFinding()
	{
		//System.out.println("Thinking..."); // debug
		pathFinder.step(8);
		if(pathFinder.isFinished())
		{
			if(pathFinder.getState() == PathFinder.FOUND)
			{
				path = pathFinder.retrievePath();
				//System.out.println("Path found"); // debug
				if(path != null && !path.isEmpty())
				{
					if(path.getFirst().equals(me.getX(), me.getY()))
						path.pop();
				}
				
				pathFinder = null;
				me.setState(Unit.NORMAL);
			}
			else
				restartPathFinding();
		}
	}
	
	/**
	 * Called when the conveyer has no path to follow (or finished the last one)
	 */
	protected void tickNoPathMovement()
	{
		if(carriedResource.isEmpty()) // I have no resource to distribute
		{
			//System.out.println("R empty"); // debug
			if(targetBuildingRef == workplaceRef)
			{
				// End of mission, ready for the next
				//System.out.println("End of mission"); // debug
				targetBuildingRef = null;
				me.enterBuilding(workplaceRef);
			}
			else
			{
				// Go back to workplace
				//System.out.println("Back to workplace"); // debug
				targetBuildingRef = workplaceRef;
				restartPathFinding();
			}
		}
		else // I have resource to distribute
		{
			//System.out.println("Random move"); // debug
			distributeResources();
			me.moveAtRandomFollowingRoads();
		}
	}
	
	private void tickPathDrivenMovement()
	{
		Vector2i pos = new Vector2i(me.getX(), me.getY());
		Vector2i nextPos = path.pop();
		//System.out.println("Following path (next is " + nextPos + ")"); // debug
		
		me.setDirection(Direction2D.toDirection(pos, nextPos));
		if(!me.moveIfPossible())
		{
			//System.out.println("Can't move (d=" + me.getDirection() + ")"); // debug
			restartPathFinding();
		}
		
		// Debug
//		if(path.isEmpty())
//			System.out.println("End of path");
	}
	
	private void distributeResources()
	{
		List<Building> buildingsAround = me.getWorld().map.getBuildingsAround(me.getWorld(), me.getX(), me.getY());
		for(Building b : buildingsAround)
		{
			if(b.isAcceptResources())
			{
				System.out.println("R distributed");
				b.storeResource(carriedResource);
			}
		}
	}

	private void restartPathFinding()
	{
		System.out.println("Restarting path finding");
		if(targetBuildingRef == null)
			targetBuildingRef = workplaceRef;
		pathFinder = new PathFinder(me.getWorld().map, me.getX(), me.getY(), targetBuildingRef.getID());
		pathFinder.setMaxSteps(-1); // Infinite
		me.setState(Unit.THINKING);
		me.setDirection(Direction2D.NONE);
	}

	@Override
	public Image getSprites()
	{
		return unitSprites;
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

}



