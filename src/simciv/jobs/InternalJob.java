package simciv.jobs;

import org.newdawn.slick.Image;

import simciv.Direction2D;
import simciv.Job;
import simciv.buildings.Workplace;
import simciv.units.Citizen;

/**
 * Internal jobs are jobs that don't need the citizen to go out of the workplace.
 * They are not visible while they work.
 * @author Marc
 *
 */
public class InternalJob extends Job
{
	byte jobID;
	
	public InternalJob(Citizen citizen, Workplace workplace, byte jobID)
	{
		super(citizen, workplace);
		this.jobID = jobID;
	}

	@Override
	public void tick()
	{
	}

	@Override
	public Image getSprites()
	{
		return null;
	}

	@Override
	public void onBegin()
	{
		me.setDirection(Direction2D.NONE);
	}
	
	@Override
	public byte getID()
	{
		return jobID;
	}

}
