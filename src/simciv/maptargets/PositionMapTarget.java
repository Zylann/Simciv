package simciv.maptargets;

import simciv.Map;

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
	public boolean evaluate(Map m, int x, int y)
	{
		return targetX == x && targetY == y;
	}
	
}


