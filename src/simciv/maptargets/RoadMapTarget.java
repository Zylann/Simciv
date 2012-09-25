package simciv.maptargets;

import simciv.Map;

public class RoadMapTarget implements IExplicitMapTarget
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean evaluate(Map m, int x, int y)
	{
		return m.grid.isRoad(x, y);
	}

}
