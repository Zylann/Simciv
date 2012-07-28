package simciv.movements;

import simciv.Road;
import simciv.maptargets.IMapTarget;
import simciv.units.Unit;

public class RandomRoadMovement implements IMovement
{
	public RandomRoadMovement()
	{
	}
	
	@Override
	public void tick(Unit u)
	{
		// TODO memorize intersections to not always take same directions ?
		u.move(Road.getAvailableDirections(u.getWorld().map, u.getX(), u.getY()));
	}

	@Override
	public boolean isBlocked()
	{
		return false;
	}

	@Override
	public boolean isFinished()
	{
		return false;
	}

	@Override
	public IMapTarget getTarget()
	{
		return null;
	}

}
