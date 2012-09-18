package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;

import simciv.Map;
import simciv.builds.Build;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.maptargets.IMapTarget;
import simciv.maptargets.WaterSourceMapTarget;
import simciv.movements.RandomRoadMovement;

/**
 * Firemen fight fires and prevent them to happen.
 * @author Marc
 *
 */
public class Fireman extends Citizen
{
	private static final long serialVersionUID = 1L;
	private static final byte WATER_CHARGE_MAX = 4;
	
	private byte waterCharge;

	public Fireman(Map m, Workplace w)
	{
		super(m, w);
	}

	@Override
	public void tick()
	{
		/*
		 * Behavior :
		 * 1) If I haven't water, find it. Otherwise, go to 2).
		 * 2) Once I have water, patrol.
		 * 3) If there is a fire, go to it. If there is not, go to 1)
		 * 4) Once at the fire, extinguish it, go to 3)
		 */
		
		if(getState() == Unit.THINKING)
			return;

		// If I haven't water
		if(waterCharge == 0)
		{
			if(!isMovement() || isMovementFinished())
			{
				IMapTarget water = new WaterSourceMapTarget();
			
				// Check if I can get water here
				if(water.evaluate(mapRef, getX(), getY()))
				{
					waterCharge = WATER_CHARGE_MAX; // Recharge !
					setMovement(new RandomRoadMovement());
				}
				else
				{
					findAndGoTo(water); // Search for water otherwise
				}
			}
		}
		else // I have water
		{
			// TODO fight fires with a specific animation
			
			// Prevent fires
			List<Build> builds = mapRef.getBuildsAround(getX(), getY());
			for(Build b : builds)
			{
				if(b.isFireBurning())
					b.extinguishFire();
				else
					b.reduceFireLevel((byte) 1);
			}
		}
	}

	@Override
	public byte getJobID()
	{
		return Jobs.FIREMAN;
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, Content.sprites.unitFireman);
	}
	
}




