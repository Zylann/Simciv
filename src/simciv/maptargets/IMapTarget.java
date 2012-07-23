package simciv.maptargets;

import simciv.World;

/**
 * A map target is an object that can evaluate a position on the map,
 * and tell if it complies with certain conditions.
 * @author Marc
 *
 */
public interface IMapTarget
{
	public boolean evaluate(World world, int x, int y);
}
