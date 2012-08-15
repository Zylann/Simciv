package simciv.buildings;

import simciv.World;

/**
 * A passive workplace doesn't produce anything.
 * @author Marc
 *
 */
public abstract class PassiveWorkplace extends Workplace
{
	public PassiveWorkplace(World w)
	{
		super(w);
	}

	@Override
	public int getProductionProgress()
	{
		return 0; // No production
	}

	@Override
	protected void tickActivity()
	{
		// No time-dependant-activity
	}

	@Override
	protected int getTickTime()
	{
		return 1000;
	}
	
	@Override
	public String getInfoString()
	{
		return "[" + getProperties().name + "] employees : " + getNbEmployees() + "/" + getMaxEmployees();
	}

}
