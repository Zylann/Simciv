package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

import backend.Direction2D;
import backend.geom.Vector2i;
import backend.ui.Notification;

import simciv.Entity;
import simciv.Map;
import simciv.builds.Build;
import simciv.builds.House;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.movement.RandomRoadMovement;

public class Policeman extends Citizen
{
	private static final long serialVersionUID = 1L;
	
	private static final int LANE_CHECK_DISTANCE = 16;
	
	// States
	private static final byte PATROL = 0;
	private static final byte CHASE = 1;
	
	private byte lastState;

	public Policeman(Map m, Workplace w)
	{
		super(m, w);
	}
	
	public Unit findBadUnit()
	{
		if(getDirection() == Direction2D.NONE)
			return null;
		
		Vector2i p = new Vector2i(getX(), getY());
		Unit target = null;
		
		for(int d = 0; d < LANE_CHECK_DISTANCE; d++)
		{
			p.addDirection(getDirection());
			
			if(!mapRef.grid.isRoad(p.x, p.y))
				break;
			
			Unit u = mapRef.getUnit(p.x, p.y);
			if(u != null && u.isBad()) {
				target = u;
				break;
			}
		}
		
		return target;
	}
	
	private boolean arrestBadUnit()
	{
		if(getDirection() == Direction2D.NONE)
			return false;
		
		Vector2i p = new Vector2i(getX(), getY());
		p.addDirection(getDirection());
		
		Unit u = mapRef.getUnit(p.x, p.y);
		if(u != null && u.isBad())
		{
			Log.debug(this + " arrested " + u);
			
			if(Robber.class.isInstance(u))
			{
				float regainedMoney = ((Robber)u).getRobbedMoney() / 2;
				
				mapRef.playerCity.gainMoney(regainedMoney);
				
				Log.debug(this + " regained 50% of robbed money (" + regainedMoney);
				mapRef.sendNotification(
					Notification.TYPE_INFO, "A robber has been arrested (+" + regainedMoney + "c)");
			}
			else
				mapRef.sendNotification(
					Notification.TYPE_INFO, "We arrested a bad guy. -- Policemen");
			
			u.dispose();
			return true;
		}
		else
			return false;
	}
	
	private void reduceCriminality()
	{
		List<Build> builds = mapRef.getBuildsAround(getX(), getY());
		for(Build b : builds)
		{
			if(b.isHouse())
			{
				House h = (House)b;
				if(h.is1x1())
					h.decreaseCrimeLevel(2);
				else
					h.decreaseCrimeLevel(1);
			}
		}
	}

	@Override
	public void tick()
	{
		byte stateTemp = getState();
		
		switch(getState())
		{
		case PATROL : tickPatrol(); break;
		case CHASE : tickChase(); break;
		}
		
		if(getState() == Entity.DEFAULT_STATE)
			setState(PATROL);
		
		lastState = stateTemp;
		
		reduceCriminality();
	}
	
	private void tickPatrol()
	{
		if(getState() != lastState || !isMovement())
			setMovement(new RandomRoadMovement());
		
		Unit u = findBadUnit();
		if(u != null)
		{
			if(Robber.class.isInstance(u))
				((Robber)u).onSeenByPoliceman(this);
			setState(CHASE);
		}
	}

	private void tickChase()
	{
		if(getState() != lastState)
			setTickTimeWithRandom(200);
		
		if(arrestBadUnit() || Math.random() < 0.05f)
			setState(PATROL);
	}
	
	@Override
	protected void renderUnit(Graphics gfx)
	{
		renderDefault(gfx, Content.sprites.unitPoliceman);
	}

	@Override
	public String getDisplayableName()
	{
		return "Policeman";
	}

}



