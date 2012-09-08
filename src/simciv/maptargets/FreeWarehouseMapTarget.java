package simciv.maptargets;

import simciv.Map;

/**
 * Returns true if there is a free warehouse around the given position
 * @author Marc
 *
 */
public class FreeWarehouseMapTarget implements IMapTarget
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean evaluate(Map m, int x, int y)
	{
		return m.getFreeWarehouse(x, y) != null;
	}

}




