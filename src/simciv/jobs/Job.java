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
	public void onBegin()
	{
	}
		
	/**
	 * Called when the citizen quits his job or is fired.
	 * Overrides must call this superclass method in order to allow workplace notification.
	 * @param notifyWorkplace : if true, the workplace is notified.
	 */
	public void onQuit(boolean notifyWorkplace)
	{
		if(workplaceRef != null && notifyWorkplace)
			workplaceRef.removeEmployeeAndMakeRedundant(me.getID());
		me.setMovement(new RandomRoadMovement());
	}

	/**
	 * Overrides the default rendering of the citizen
	 * @param citizen
	 */
	public abstract void renderUnit(Graphics gfx);
	
}




