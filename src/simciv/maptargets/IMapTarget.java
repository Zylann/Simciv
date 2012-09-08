package simciv.maptargets;

import java.io.Serializable;

import simciv.Map;

/**
 * A map target is an object that can evaluate a position on the map,
 * and tell if it complies with certain conditions.
 * @author Marc
 *
 */
public interface IMapTarget extends Serializable
{
	public boolean evaluate(Map m, int x, int y);
}
