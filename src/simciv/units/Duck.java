package simciv.units;

import org.newdawn.slick.Graphics;
import simciv.World;
import simciv.content.Content;
import simciv.movements.RandomMovement;

public class Duck extends Unit
{	
	public Duck(World w)
	{
		super(w);
		setMovement(new RandomMovement());
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, Content.images.unitDuck);
	}

	@Override
	protected void tick()
	{
	}

	@Override
	public void onDestruction()
	{
	}

}


