package simciv.maptargets;

import simciv.World;

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
	public boolean evaluate(World world, int x, int y)
	{
		return world.map.isBuildAroundWithID(buildingID, x, y);
	}
	
}


