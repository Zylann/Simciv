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
			sprite = ContentManager.instance().getImage("city.nomad");
	}

	@Override
	public void render(Graphics gfx)
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
}
