package simciv;

import org.newdawn.slick.Image;

import simciv.units.Citizen;

/**
 * This is the job of a citizen. It defines a new behavior and appearance.
 * @author Marc
 *
 */
public abstract class Job
{
	// Job IDs
	public static final byte FARMER = 1;
	
	protected Citizen me; // The Citizen doing the job
	
	public Job(Citizen citizen)
	{
		me = citizen;
	}
	
	public abstract void tick();
	public abstract Image getSprites();
	public abstract byte getID();
}
