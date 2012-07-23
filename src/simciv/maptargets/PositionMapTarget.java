package simciv.maptargets;

import simciv.World;

/**
 * Returns true if the given position is evaluated, false otherwise
 * @author Marc
 *
 */
public class PositionMapTarget implements IMapTarget
{
	public int targetX;
	public int targetY;
	
	public PositionMapTarget(int x, int y)
	{
		targetX = x;
		targetY = y;
	}
	
	@Override
	public boolean evaluate(World world, int x, int y)
	{
		return targetX == x && targetY == y;
	}
	
}


