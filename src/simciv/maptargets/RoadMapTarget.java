package simciv.maptargets;

import simciv.Map;

public class RoadMapTarget implements IMapTarget
{
	@Override
	public boolean evaluate(Map m, int x, int y)
	{
		return m.grid.isRoad(x, y);
	}

}
