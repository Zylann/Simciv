package simciv.movement;

import java.io.Serializable;

import simciv.units.Unit;

/**
 * Defines how a unit moves
 * @author Marc
 *
 */
public interface IMovement extends Serializable
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
	
}


