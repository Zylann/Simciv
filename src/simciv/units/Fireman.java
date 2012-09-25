package simciv.units;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Graphics;

import backend.geom.Vector2i;
import backend.pathfinding.IMapTarget;
import simciv.Map;
import simciv.builds.Build;
import simciv.builds.WaterSource;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.effects.AnimEffect;
import simciv.movement.RandomRoadMovement;

/**
 * Firemen fight fires and prevent them to happen.
 * They need water to do their job.
 * @author Marc
 *
 */
public class Fireman extends Citizen
{
	private static final long serialVersionUID = 1L;
	public static final byte WATER_CHARGE_MAX = 4;
	private static final int PATHFINDING_DISTANCE = 128;
	
	private static final byte PATROL = 0;
	private static final byte FIND_WATER = 1;
	private static final byte FIND_FIRE = 2;
	private static final byte FIGHT_FIRE = 3;
	private static final byte FIND_ROAD = 4;
	
	private byte waterCharge;
	private byte lastState;
	
	public Fireman(Map m, Workplace w)
	{
		super(m, w);
		state = PATROL;
	}
	
	@Override
	public void setState(byte state)
	{
		super.setState(state);
		if(state == FIND_FIRE || state == FIGHT_FIRE)
			setTickTimeWithRandom(3 * getTickTime() / 5);
		else
			setTickTimeWithRandom(Citizen.TICK_TIME_BASIC);
	}
	
	@Override
	public void tick()
	{
		/*
		 * Behavior :
		 * 1) If I haven't water, find it. Otherwise, go to 2).
		 * 2) Once I have water, patrol. If I'm not on a road, go to roads.
		 * 3) If there is a fire, go to it. If there is not, go to 1)
		 * 4) Once at the fire, extinguish it, go to 3)
		 */
		
		// Prevent fires
		if(waterCharge != 0)
			preventFires();
		
		byte stateTemp = state;

		switch(state)
		{
		case PATROL : tickPatrol(); break;
		case FIND_WATER : tickFindWater(); break;
		case FIND_FIRE : tickFindFire(); break;
		case FIGHT_FIRE : tickFightFire(); break;
		case FIND_ROAD : tickFindRoad(); break;
		}
		
		lastState = stateTemp;
	}
	
	public void onFireAlert(LinkedList<Vector2i> pathToFire)
	{
		if(isReadyForMission())
		{
			setState(FIND_FIRE);
			followPath(pathToFire);
		}
	}
	
	private void tickPatrol()
	{
		if(lastState != state)
			setMovement(new RandomRoadMovement());
				
		if(waterCharge == 0)
			setState(FIND_WATER);

		if(isMovementBlocked() && !isOnRoad())
			setState(FIND_ROAD);
	}

	private void tickFindWater()
	{
		// On enter or movement end
		if(lastState != state || isMovementFinished())
		{
			if(tryRechargeWater())
				setState(PATROL);
			else
				findAndGoTo(new WaterSourceTarget(), PATHFINDING_DISTANCE);
		}
		
		if(isMovementBlocked() && !isOnRoad())
			setState(FIND_ROAD);
	}

	private void tickFindFire()
	{
		if(isMovementFinished())
			setState(FIGHT_FIRE);
		
		if(isMovementBlocked() && !isOnRoad())
			setState(FIND_ROAD);
	}

	private void tickFightFire()
	{
		if(waterCharge == 0 || !fightFire())
			setState(FIND_ROAD);
	}

	private void tickFindRoad()
	{
		// On enter
		if(lastState != state || isMovementBlocked())
			goBackToRoad(PATHFINDING_DISTANCE);
		
		if(isMovementFinished())
			setState(PATROL);
	}

	private boolean tryRechargeWater()
	{
		// Check if I can get water here
		List<Build> builds = mapRef.getBuildsAround(getX(), getY());
		for(Build b : builds)
		{
			if(WaterSource.class.isInstance(b))
			{
				waterCharge = WATER_CHARGE_MAX; // Recharge !
				return true;
			}
		}
		return false;
	}
	
	private void preventFires()
	{
		Vector2i npos = new Vector2i();
		
		// For each direction
		for(byte d = 0; d < 4; d++)
		{
			// Compute neighbor position
			npos.set(getX(), getY());
			npos.addDirection(d);
			
			// Get neighboring build (if one)
			Build b = mapRef.getBuild(npos.x, npos.y);
			
			if(b != null)
			{
				// If the build is not burning
				if(!b.isFireBurning() && waterCharge != 0)
				{
					// Prevent fires
					b.reduceFireLevel((byte) (b.is1x1() ? 2 : 1));
				}
			}
		}
	}
	
	/**
	 * Prevents fires around the fireman.
	 * If there is a build on fire, the fireman will extinguish it.
	 */
	private boolean fightFire()
	{
		Vector2i npos = new Vector2i();		
		for(byte d = 0; d < 4; d++)
		{
			// Compute neighbor position
			npos.set(getX(), getY());
			npos.addDirection(d);
			
			// Get neighboring build (if one)
			Build b = mapRef.getBuild(npos.x, npos.y);
			
			if(b != null)
			{
				if(b.isFireBurning())
				{
					setMovement(null);
					setDirection(d); // Look at the flames
					
					// Make a splash
					mapRef.addGraphicalEffect(
						new AnimEffect.Splash(
							b.getX(), b.getY()));
					
					// Extinguish fire
					if(Math.random() < 0.2f)
					{
						b.extinguishFire();
						waterCharge--;
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isReadyForMission()
	{
		return hasWater() && (state == PATROL || state == FIND_ROAD);
	}
	
	public boolean isFightingFire()
	{
		return state == FIGHT_FIRE;
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		renderDefault(gfx, 
				Content.sprites.unitFireman, 
				isFightingFire() ? 4 : 0);
	}

	public boolean hasWater()
	{
		return waterCharge != 0;
	}
	
	private class WaterSourceTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			Build b = mapRef.getBuild(x, y);
			if(b != null && WaterSource.class.isInstance(b))
			{
				if(((WaterSource)b).getState() == Build.STATE_ACTIVE)
					return true;
			}
			return false;
		}
	}
	
}




