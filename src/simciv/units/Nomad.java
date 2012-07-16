package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import simciv.ContentManager;
import simciv.World;

public class Nomad extends Unit
{
	private static Image sprite = null;
	
	public Nomad(World w)
	{
		super(w);
		if(sprite == null)
			sprite = ContentManager.instance().getImage("unit.nomad");
	}

	@Override
	public void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, sprite);
	}

	@Override
	public void tick()
	{
		move(worldRef.map.getAvailableDirections(posX, posY));
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
