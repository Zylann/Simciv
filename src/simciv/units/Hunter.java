package simciv.units;

import org.newdawn.slick.Graphics;

import backend.MathHelper;
import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;

import simciv.Map;
import simciv.builds.Workplace;
import simciv.builds.Hunters;
import simciv.content.Content;

public class Hunter extends Citizen
{
	private static final long serialVersionUID = 1L;
	
	private static final int PATHFINDING_DISTANCE = 4096;
	private static final int TICK_TIME = 300;
	
	// States
	private static final byte FIND_PREY = 0;
	private static final byte RETURN_TO_WORKPLACE = 1;

	private byte lastState;
	private boolean hasPrey;
	
	public Hunter(Map m, Workplace w)
	{
		super(m, w);
		state = FIND_PREY;
		setTickTimeWithRandom(TICK_TIME);
	}

	@Override
	public boolean findAndGoTo(IMapTarget target)
	{
		return super.findAndGoTo(
				new WalkableFloor(), target, PATHFINDING_DISTANCE);
	}

	@Override
	public void tick()
	{
		byte stateTemp = getState();
		
		switch(getState())
		{
		case FIND_PREY : tickFindPrey(); break;
		case RETURN_TO_WORKPLACE : tickReturnToWorkplace(); break;
		}
		
		lastState = stateTemp;
	}
	
	private void tickFindPrey()
	{
		if(lastState != getState())
			setMovement(null);
		
		if(mapRef.getFaunaCount() == 0)
			setState(RETURN_TO_WORKPLACE);
		
		if(!isMovement())
			findAndGoTo(new PreyTarget());
		else if(isMovementFinished())
		{
			setMovement(null);
			if(hunt())
				setState(RETURN_TO_WORKPLACE);
			else
				findAndGoTo(new PreyTarget());
		}

		if(!isMovement())
			setState(RETURN_TO_WORKPLACE);
	}

	private void tickReturnToWorkplace()
	{
		if(lastState != getState())
			setMovement(null);
		
		if(!isMovement() || isMovementBlocked())
		{
			if(isMyWorkplaceNearby())
				onBackToWorkplace();
			else
				findAndGoTo(new WorkplaceTarget());
		}
		else if(isMovementFinished())
			onBackToWorkplace();
	}
	
	private void onBackToWorkplace()
	{
		Workplace w = getWorkplace();
		((Hunters)w).onHunterBack(hasPrey);
		dispose();
	}
	
	private boolean hunt()
	{
		Content.sounds.unitHunterCatch.play(1.f + MathHelper.randS(0.1f), 0.5f);
		
		Unit u = mapRef.getUnit(getX(), getY());
		if(Duck.class.isInstance(u))
		{
			u.kill();
			hasPrey = true;
			return true;
		}
		return false;
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		renderDefault(gfx, Content.sprites.unitHunter);
	}

	@Override
	public String getDisplayableName()
	{
		return "Hunter";
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
	
	private class WorkplaceTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return mapRef.grid.getBuildID(x, y) == getWorkplaceID();
		}
	}

}


