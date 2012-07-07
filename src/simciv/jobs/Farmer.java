package simciv.jobs;

import org.newdawn.slick.Image;

import simciv.Job;
import simciv.units.Citizen;

public class Farmer extends Job
{
	public Farmer(Citizen citizen)
	{
		super(citizen);
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
	public byte getID()
	{
		return Job.FARMER;
	}

}
