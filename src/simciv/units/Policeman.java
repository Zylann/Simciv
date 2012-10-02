package simciv.units;

import org.newdawn.slick.Graphics;

import simciv.Entity;
import simciv.Map;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.movement.RandomRoadMovement;

public class Policeman extends Citizen
{
	private static final long serialVersionUID = 1L;
	
	// States
	private static final byte PATROL = 0;
	
	private byte lastState;

	public Policeman(Map m, Workplace w)
	{
		super(m, w);
	}

	@Override
	public void tick()
	{
		byte stateTemp = getState();
		
		switch(getState())
		{
		case PATROL : tickPatrol(); break;
		}
		
		if(getState() == Entity.DEFAULT_STATE)
			setState(PATROL);
		
		lastState = stateTemp;
	}
	
	private void tickPatrol()
	{
		if(getState() != lastState || !isMovement())
			setMovement(new RandomRoadMovement());		
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



