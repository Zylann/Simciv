package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import simciv.ContentManager;
import simciv.World;
import simciv.movements.RandomMovement;

public class Duck extends Unit
{
	private static Image sprite;
		
	public Duck(World w)
	{
		super(w);
		if(sprite == null)
			sprite = ContentManager.instance().getImage("unit.duck");
		setMovement(new RandomMovement());
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, sprite);
	}

	@Override
	protected void tick()
	{
	}

	@Override
	public void onInit()
	{
	}

	@Override
	public void onDestruction()
	{
	}

}


