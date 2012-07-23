package simciv.movements;

import simciv.Direction2D;
import simciv.maptargets.IMapTarget;
import simciv.units.Unit;

public class RandomMovement implements IMovement
{
	protected int ticksBeforeNextMove;
	protected int ticksBeforeNextStop;
	
	public RandomMovement()
	{
		randomize();
	}
	
	private void randomize()
	{
		ticksBeforeNextMove = (int) (5 + 10.f * Math.random());
		ticksBeforeNextStop = (int) (5 + 10.f * Math.random());
	}

	@Override
	public void tick(Unit u)
	{
		if(ticksBeforeNextStop > 0)
		{
			ticksBeforeNextStop--;
			u.move(u.getWorld().map.getAvailableDirections(u.getX(), u.getY()));
		}
		else
		{
			u.setDirection(Direction2D.random());
			ticksBeforeNextMove--;
			if(ticksBeforeNextMove == 0)
				randomize();
		}
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



