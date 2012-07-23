package simciv.movements;

import simciv.maptargets.IMapTarget;
import simciv.units.Unit;

/**
 * Defines how a unit moves
 * @author Marc
 *
 */
public interface IMovement
{
	/**
	 * Makes the unit move
	 * @param u : unit to move
	 */
	public void tick(Unit u);
	
	/**
	 * Returns true if the last movement was impossible
	 * @return
	 */
	public boolean isBlocked();
	
	/**
	 * Returns true if the movement ended
	 * @return
	 */
	public boolean isFinished();
	
	/**
	 * Returns the target of the movement, if it have one
	 * @return target, or null
	 */
	public IMapTarget getTarget();
	
}


