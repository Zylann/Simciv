package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import simciv.ContentManager;
import simciv.World;
import simciv.movements.RandomMovement;

public class Nomad extends Unit
{
	private static Image sprite = null;
	
	public Nomad(World w)
	{
		super(w);
		if(sprite == null)
			sprite = ContentManager.instance().getImage("unit.nomad");
		setMovement(new RandomMovement());
	}

	@Override
	public void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, sprite);
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

	@Override
	public void onInit()
	{
	}
	
}


