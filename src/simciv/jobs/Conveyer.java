package simciv.jobs;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.Direction2D;
import simciv.Game;
import simciv.ResourceSlot;
import simciv.builds.Build;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.maptargets.BuildMapTarget;
import simciv.maptargets.FreeWarehouseMapTarget;
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
	private static SpriteSheet unitSprites;
	
//	private PathFinder pathFinder;
	private ResourceSlot carriedResource;
	
	public Conveyer(Citizen citizen, Workplace workplace)
	{
		super(citizen, workplace);
		if(unitSprites == null)
			unitSprites = new SpriteSheet(Content.images.unitConveyer, Game.tilesSize, Game.tilesSize);
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
	public byte getID()
	{
		return Job.CONVEYER;
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
		
		if(!me.isOut())
			return;
		
		if(me.getState() == Unit.THINKING)
			return;
		
		if(!carriedResource.isEmpty() && !me.isMovement())
		{
			me.findAndGoTo(new FreeWarehouseMapTarget());
		}
		
		if(me.isMovementFinished())
		{
			onPathFinished();
		}
		else if(me.isMovementBlocked())
		{
			me.findAndGoTo(me.getMovementTarget());
		}
		else
		{
			tickDefault();
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
			if(me.getMovementTarget() == null || getTargetBuildingID() != workplaceRef.getID())
				me.findAndGoTo(workplaceRef);
		}
		else // I have resource to distribute
		{
			distributeResources(); // distribute it
		}
	}
		
	protected void onPathFinished()
	{
		if(!carriedResource.isEmpty())
			distributeResources(); // distribute resources
		if(!carriedResource.isEmpty()) // if still resources left, search another warehouse
			me.findAndGoTo(new FreeWarehouseMapTarget());
		
		if(getTargetBuildingID() == workplaceRef.getID())
		{
			// End of mission, ready for the next
			me.setMovement(null);
			me.enterBuilding(workplaceRef);
		}
	}
	
	private void distributeResources()
	{
		List<Build> buildingsAround = me.getWorld().getBuildsAround(me.getX(), me.getY());
		for(Build b : buildingsAround)
		{
			if(b.isAcceptResources())
				b.storeResource(carriedResource);
		}
		if(carriedResource.isEmpty()) // if no resources left,
			me.findAndGoTo(workplaceRef); // Back to my workplace
	}
	
	private int getTargetBuildingID()
	{
		if(BuildMapTarget.class.isInstance(me.getMovementTarget()))
			return ((BuildMapTarget)(me.getMovementTarget())).buildingID;
		return -1;
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

	@Override
	public int getIncome()
	{
		return 14;
	}

}



