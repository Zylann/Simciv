package simciv.buildings;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import simciv.Building;
import simciv.Game;
import simciv.World;

public class House extends Building
{
	static Image sprite;
	
	public static void loadContent() throws SlickException
	{
		sprite = new Image("data/house1.png");
		sprite.setFilter(Image.FILTER_NEAREST);
	}

	public House(World w)
	{
		super(w);
	}

	@Override
	public void tick()
	{
		increaseTicks();
	}

	@Override
	public void render(Graphics gfx)
	{
		gfx.drawImage(sprite, posX * Game.tilesSize, (posY - 1)* Game.tilesSize);
	}
}
