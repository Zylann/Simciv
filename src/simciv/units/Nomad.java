package simciv.units;

import org.newdawn.slick.Graphics;
import simciv.World;
import simciv.content.Content;
import simciv.movements.RandomMovement;

public class Nomad extends Unit
{
	public Nomad(World w)
	{
		super(w);
		setMovement(new RandomMovement());
	}
	
	@Override
	public void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, Content.images.unitNomad);
	}

	@Override
	public void tick()
	{
	}

	@Override
	public void onDestruction()
	{
	}

	@Override
	protected int getTickTime()
	{
		return 500;
	}
	
}


