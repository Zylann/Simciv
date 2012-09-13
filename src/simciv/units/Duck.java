package simciv.units;

import org.newdawn.slick.Graphics;

import simciv.Map;
import simciv.content.Content;
import simciv.movements.RandomMovement;

public class Duck extends Unit
{
	private static final long serialVersionUID = 1L;
	
	public Duck(Map m)
	{
		super(m);
		setMovement(new RandomMovement());		
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, Content.sprites.unitDuck);
	}

	@Override
	public void tick()
	{
	}

	@Override
	public void onDestruction()
	{
	}

}


