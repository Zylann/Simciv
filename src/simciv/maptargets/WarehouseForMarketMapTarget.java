package simciv.maptargets;

import java.util.List;

import simciv.Map;
import simciv.builds.Build;
import simciv.builds.Warehouse;

/**
 * Evaluates warehouses that contains resources available for markets
 * @author Marc
 *
 */
public class WarehouseForMarketMapTarget implements IMapTarget
{
	@Override
	public boolean evaluate(Map world, int x, int y)
	{
		List<Build> list = world.getBuildsAround(x, y);
		for(Build b : list)
		{
			if(Warehouse.class.isInstance(b))
			{
				if(((Warehouse) b).containsResourcesForMarkets())
					return true;
			}
		}
		return false;
	}

}
