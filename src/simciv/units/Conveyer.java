package simciv.units;

import org.newdawn.slick.Graphics;

import backend.Direction2D;
import backend.pathfinding.IMapTarget;

import simciv.Entity;
import simciv.Map;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.resources.ResourceSlot;

/**
 * Conveyers carries resources from a place to another.
 * There is two main conveyer types : store and get conveyers.
 * See sublclasses.
 * @author Marc
 *
 */
public abstract class Conveyer extends Citizen
{
	private static final long serialVersionUID = 1L;

	// States
	private static final byte FIND_STORAGE = 0;
	private static final byte DO_TRANSACTION = 1;
	private static final byte BACK_TO_WORKPLACE = 2;
	
	/** Resources carried by the conveyer **/
	protected ResourceSlot carriedResource;

	/** Last state of the conveyer **/
	private byte lastState;
	
	public Conveyer(Map m, Workplace w)
	{
		super(m, w);
		carriedResource = new ResourceSlot();
		setState(FIND_STORAGE);
	}
	
	protected abstract IMapTarget getTransactionPlace();
	
	/**
	 * Executes the resources transactions.
	 * @return true if enough, false if we need another transaction.
	 */
	protected abstract boolean doTransaction();

	protected void onBackToWorkplace()
	{
		getWorkplace().onConveyerIsBack(this);
	}
	
	public byte getResourceType()
	{
		return carriedResource.getType();
	}
	
	public int getResourceAmount()
	{
		return carriedResource.getAmount();
	}

	@Override
	public void tick()
	{
		/*
		 * Current behavior :
		 * The conveyer goes out of its workplace with or without resources.
		 * He moves at random, distributing resources.
		 * When he distributed all his resources, he goes back to its workplace.
		 */
		
		byte lastStateTemp = state;
		
		switch(state)
		{
		case FIND_STORAGE : tickFindStorage(); break;
		case DO_TRANSACTION : tickDoTransaction(); break;
		case BACK_TO_WORKPLACE : tickBackToWorkplace(); break;
		}
		
		lastState = lastStateTemp;
	}
	
	private void tickFindStorage()
	{
		if(state != lastState || isMovementBlocked())
			setMovement(null);
		
		if(!isMovement())
			findAndGoTo(getTransactionPlace(), -1);
		
		if(isMovementFinished())
			state = DO_TRANSACTION;
	}

	private void tickDoTransaction()
	{
		boolean transactionSuccess = false;
		
		if(state != lastState)
			transactionSuccess = doTransaction();
		
		if(transactionSuccess)
			state = BACK_TO_WORKPLACE;
		else
			state = FIND_STORAGE;
	}

	private void tickBackToWorkplace()
	{
		if(state != lastState || isMovementBlocked())
			setMovement(null);
				
		if(isMovement())
		{
			if(isMovementFinished())
			{
				onBackToWorkplace();
				dispose();
			}
		}
		else
		{
			if(isMyWorkplaceNearby())
			{
				onBackToWorkplace();
				dispose();
			}
			else
				findAndGoTo(new WorkplaceTarget(), -1);
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
		case FIND_STORAGE : stateName = "Finding storage"; break;
		case DO_TRANSACTION : stateName = "Doing transaction"; break;
		}
		
		return super.getInfoLine() + " " + stateName;
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
	
	private class WorkplaceTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return mapRef.grid.getBuildID(x, y) == getWorkplaceID();
		}
	}

}
