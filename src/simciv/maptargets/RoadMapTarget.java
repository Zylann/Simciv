package simciv.maptargets;

import simciv.World;

public class RoadMapTarget implements IMapTarget
{
	@Override
	public boolean evaluate(World world, int x, int y)
	{
		return world.map.isRoad(x, y);
	}

}
