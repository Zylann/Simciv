package simciv.maptargets;

import java.util.List;

import simciv.World;
import simciv.buildings.Building;
import simciv.buildings.Warehouse;

/**
 * Evaluates warehouses that contains resources available for markets
 * @author Marc
 *
 */
public class WarehouseForMarketMapTarget implements IMapTarget
{
	@Override
	public boolean evaluate(World world, int x, int y)
	{
		List<Building> list = world.getBuildingsAround(x, y);
		for(Building b : list)
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
