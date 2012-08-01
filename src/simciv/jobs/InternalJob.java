package simciv.jobs;

import org.newdawn.slick.Graphics;
import simciv.Direction2D;
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
	public void onBegin()
	{
		me.setMovement(null);
		me.setDirection(Direction2D.NONE);
		me.enterBuilding(workplaceRef);
	}

	@Override
	public byte getID()
	{
		return jobID;
	}

	@Override
	public final void renderUnit(Graphics gfx)
	{
		// Nothing
	}

	@Override
	public int getIncome()
	{
		return 10;
	}
	
	@Override
	public boolean isInternal()
	{
		return true;
	}

}
