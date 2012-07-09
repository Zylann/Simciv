package simciv.jobs;

import org.newdawn.slick.Image;

import simciv.Direction2D;
import simciv.Job;
import simciv.buildings.FarmLand;
import simciv.units.Citizen;

public class Farmer extends Job
{
	public Farmer(Citizen citizen, FarmLand farmland)
	{
		super(citizen, farmland);
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
		return Job.FARMER;
	}

}
