package simciv.jobs;

import org.newdawn.slick.Graphics;
import simciv.buildings.Workplace;
import simciv.movements.RandomRoadMovement;
import simciv.units.Citizen;

/**
 * This is the job of a citizen. It defines its behavior and appearance.
 * @author Marc
 *
 */
public abstract class Job
{
	// Job IDs
	public static final byte FARMER = 1;
	public static final byte WAREHOUSE_INTERNAL = 2;
	public static final byte CONVEYER = 3;
	public static final byte TAXMEN_OFFICE_INTERNAL = 4;
	public static final byte TAXMAN = 5;
	public static final byte MARKET_INTERNAL = 6;
	public static final byte MARKET_DELIVERY = 7;
	
	protected Citizen me; // The Citizen doing the job
	protected Workplace workplaceRef; // must not be null (otherwise the job may be useless)
	
	public Job(Citizen citizen, Workplace workplace)
	{
		me = citizen;
		workplaceRef = workplace;
	}
	
	/**
	 * Updates behavior of the job on the citizen
	 */
	public abstract void tick();
	
	/**
	 * Gets the numeric identifier of the job (its type)
	 * @return
	 */
	public abstract byte getID();
	
	public Workplace getWorkplace()
	{
		return workplaceRef;
	}
	
	/**
	 * Called when the job begins.
	 */
	public abstract void onBegin();
	
	// TODO missionBegin / missionEnd system
	
	/**
	 * Called when the citizen quits his job or is fired.
	 * Overrides must call this superclass method in order to allow workplace notification.
	 * @param notifyWorkplace : if true, the workplace is notified.
	 */
	public void onQuit(boolean notifyWorkplace)
	{
		if(workplaceRef != null && notifyWorkplace)
			workplaceRef.removeEmployee(me.getID(), false);
		if(!me.isOut())
			me.exitBuilding();
		me.setMovement(new RandomRoadMovement());
	}

	/**
	 * Overrides the default rendering of the citizen
	 * @param citizen
	 */
	public abstract void renderUnit(Graphics gfx);
	
	/**
	 * Returns the amount of money the citizen gets paid of
	 * (Note that citizen don't really earn money, this is just a mean to
	 * evaluate their wealth).
	 * @return
	 */
	public abstract int getIncome();
	
	/**
	 * Gets the tick time modification applied to the unit.
	 * It is applied only when the job begins.
	 * Calculation is : newTickTime = tickTime + getTickTimeOverride().
	 * @return
	 */
	public int getTickTimeOverride()
	{
		return 0;
	}

	public boolean isInternal()
	{
		return false;
	}
	
}




