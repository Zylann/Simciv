package simciv;

import org.newdawn.slick.Image;

import simciv.buildings.Workplace;
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
	
	protected Citizen me; // The Citizen doing the job
	protected Workplace workplaceRef; // must not be null (otherwise the job may be useless)
	
	public Job(Citizen citizen, Workplace workplace)
	{
		me = citizen;
		workplaceRef = workplace;
	}
	
	public abstract void tick();
	public abstract Image getSprites();
	public abstract byte getID();
	
	public Workplace getWorkplace()
	{
		return workplaceRef;
	}
	
	public void onBegin()
	{
	}
	
	public void onQuit(boolean notifyWorkplace)
	{
		if(workplaceRef != null && notifyWorkplace)
			workplaceRef.removeEmployeeAndMakeRedundant(me.getID());
	}
}
