package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import simciv.Road;
import simciv.Unit;
import simciv.World;
import simciv.buildings.House;

public class Citizen extends Unit
{
	private static Image sprite = null;
	
	House houseRef;

	public static final void loadContent() throws SlickException
	{
		sprite = new Image("data/citizen.png");
		sprite.setFilter(Image.FILTER_NEAREST);
	}

	public Citizen(World w)
	{
		super(w);
	}
	
	@Override
	public void render(Graphics gfx)
	{
		defaultRender(gfx, sprite);
	}

	@Override
	public void tick()
	{
		increaseTicks();
		move(Road.getAvailableDirections(worldRef.map, posX, posY));
	}
	
	public void setHouse(House h)
	{
		houseRef = h;
	}
}
