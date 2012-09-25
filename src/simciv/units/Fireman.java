package simciv.units;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Graphics;

import backend.geom.Vector2i;
import backend.pathfinding.IMapSpec;
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
	private byte firemanState;
	private byte lastFiremanState;
	
	public Fireman(Map m, Workplace w)
	{
		super(m, w);
		firemanState = PATROL;
	}
	
	/**
	 * Sets the onMission flag.
	 * If true, the fireman will increase its tick speed.
	 * If false, the fireman will reset its tick speed and movement.
	 * @param m
	 */
	private void setFiremanState(byte state)
	{
		firemanState = state;
		if(firemanState == FIND_FIRE || firemanState == FIGHT_FIRE)
			setTickTimeWithRandom(3 * getTickTime() / 5);
		else
			setTickTimeWithRandom(Citizen.TICK_TIME_BASIC);
	}
	
	private void goBackToRoad()
	{
		// Find the nearest road if I am not on it
		if(!isOnRoad())
		{
			findAndGoTo(
				new WalkableFloor(),
				new RoadTarget(),
				PATHFINDING_DISTANCE);
		}
	}
	
	private boolean isOnRoad()
	{
		return mapRef.grid.isRoad(getX(), getY());
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
		
		byte lastState = firemanState;

		switch(firemanState)
		{
		case PATROL : tickPatrol(); break;
		case FIND_WATER : tickFindWater(); break;
		case FIND_FIRE : tickFindFire(); break;
		case FIGHT_FIRE : tickFightFire(); break;
		case FIND_ROAD : tickFindRoad(); break;
		}
		
		lastFiremanState = lastState;
	}
	
	public void onFireAlert(LinkedList<Vector2i> pathToFire)
	{
		if(isReadyForMission())
		{
			setFiremanState(FIND_FIRE);
			followPath(pathToFire);
		}
	}
	
	private void tickPatrol()
	{
		if(lastFiremanState != firemanState)
			setMovement(new RandomRoadMovement());
				
		if(waterCharge == 0)
			setFiremanState(FIND_WATER);

		if(isMovementBlocked() && !isOnRoad())
			setFiremanState(FIND_ROAD);
	}

	private void tickFindWater()
	{
		// On enter or movement end
		if(lastFiremanState != firemanState || isMovementFinished())
		{
			if(tryRechargeWater())
				setFiremanState(PATROL);
			else
				findAndGoTo(new WaterSourceTarget(), PATHFINDING_DISTANCE);
		}
		
		if(isMovementBlocked() && !isOnRoad())
			setFiremanState(FIND_ROAD);
	}

	private void tickFindFire()
	{
		if(isMovementFinished())
			setFiremanState(FIGHT_FIRE);
		
		if(isMovementBlocked() && !isOnRoad())
			setFiremanState(FIND_ROAD);
	}

	private void tickFightFire()
	{
		if(waterCharge == 0 || !fightFire())
			setFiremanState(FIND_ROAD);
	}

	private void tickFindRoad()
	{
		// On enter
		if(lastFiremanState != firemanState || isMovementBlocked())
			goBackToRoad();
				
		if(isMovementFinished())
			setFiremanState(PATROL);
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
					setFiremanState(FIGHT_FIRE);					
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
		return hasWater() && (firemanState == PATROL || firemanState == FIND_ROAD);
	}
	
	public boolean isFightingFire()
	{
		return firemanState == FIGHT_FIRE;
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, Content.sprites.unitFireman);
	}

	public boolean hasWater()
	{
		return waterCharge != 0;
	}
	
	private class RoadTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return mapRef.grid.isRoad(x, y);
		}
	}
	
	private class WalkableFloor implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.isWalkable(x, y);
		}
	}
	
	private class WaterSourceTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			Build b = mapRef.getBuild(x, y);
			if(WaterSource.class.isInstance(b))
			{
				if(((WaterSource)b).getState() == Build.STATE_ACTIVE)
					return true;
			}
			return false;
		}
	}
	
}




