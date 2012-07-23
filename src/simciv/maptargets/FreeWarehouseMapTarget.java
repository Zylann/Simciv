package simciv.maptargets;

import simciv.World;

/**
 * Returns true if there is a free warehouse around the given position
 * @author Marc
 *
 */
public class FreeWarehouseMapTarget implements IMapTarget
{
	@Override
	public boolean evaluate(World world, int x, int y)
	{
		return world.getFreeWarehouse(x, y) != null;
	}

}




