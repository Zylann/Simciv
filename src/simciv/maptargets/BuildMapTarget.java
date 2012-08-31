package simciv.maptargets;

import simciv.Map;

/**
 * Returns true if a certain building is around the given pos.
 * @author Marc
 *
 */
public class BuildMapTarget implements IMapTarget
{
	public int buildingID;

	public BuildMapTarget(int ID)
	{
		buildingID = ID;
	}

	@Override
	public boolean evaluate(Map m, int x, int y)
	{
		return m.grid.isBuildAroundWithID(buildingID, x, y);
	}
	
}


