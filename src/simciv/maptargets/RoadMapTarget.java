package simciv.maptargets;

import simciv.Map;

public class RoadMapTarget implements IMapTarget
{
	@Override
	public boolean evaluate(Map world, int x, int y)
	{
		return world.grid.isRoad(x, y);
	}

}
