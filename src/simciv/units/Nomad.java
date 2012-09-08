package simciv.units;

import org.newdawn.slick.Graphics;
import simciv.Map;
import simciv.content.Content;
import simciv.movements.RandomMovement;

public class Nomad extends Unit
{
	private static final long serialVersionUID = 1L;
	
	public Nomad(Map m)
	{
		super(m);
		setMovement(new RandomMovement());		
	}
	
	@Override
	public void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, Content.sprites.unitNomad);
	}

	@Override
	public void tick()
	{
		// Nomads can disappear after 2min
		if(getLifeTime() > 120000)
		{
			if(Math.random() < 0.1)
				dispose();
		}
	}

	@Override
	public void onDestruction()
	{
	}

	@Override
	protected int getTickTime()
	{
		return 700;
	}
	
}


