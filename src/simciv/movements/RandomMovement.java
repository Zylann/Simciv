package simciv.movements;

import simciv.Direction2D;
import simciv.MathHelper;
import simciv.maptargets.IMapTarget;
import simciv.units.Unit;

public class RandomMovement implements IMovement
{
	private static final long serialVersionUID = 1L;
	
	protected int ticksBeforeNextMove;
	protected int ticksBeforeNextStop;
	
	public RandomMovement()
	{
		randomize();
	}
	
	private void randomize()
	{
		ticksBeforeNextMove = MathHelper.randInt(5, 15);
		ticksBeforeNextStop = MathHelper.randInt(5, 15);
	}

	@Override
	public void tick(Unit u)
	{
		if(ticksBeforeNextStop > 0)
		{
			ticksBeforeNextStop--;
			u.move(u.getMap().grid.getAvailableDirections(u.getX(), u.getY()));
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



