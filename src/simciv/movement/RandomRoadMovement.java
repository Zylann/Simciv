package simciv.movement;

import simciv.Road;
import simciv.units.Unit;

public class RandomRoadMovement implements IMovement
{
	private static final long serialVersionUID = 1L;
	
	private boolean blocked;

	public RandomRoadMovement()
	{
	}
	
	@Override
	public void tick(Unit u)
	{
		blocked = !u.move(Road.getAvailableDirections(u.getMap().grid, u.getX(), u.getY()));
	}

	@Override
	public boolean isBlocked()
	{
		return blocked;
	}

	@Override
	public boolean isFinished()
	{
		return false;
	}

}
