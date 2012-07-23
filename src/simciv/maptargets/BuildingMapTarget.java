package simciv.maptargets;

import simciv.World;

/**
 * Returns true if a certain building is around the given pos.
 * @author Marc
 *
 */
public class BuildingMapTarget implements IMapTarget
{
	public int buildingID;

	public BuildingMapTarget(int ID)
	{
		buildingID = ID;
	}

	@Override
	public boolean evaluate(World world, int x, int y)
	{
		return world.map.isBuildingAroundWithID(buildingID, x, y);
	}
	
}


