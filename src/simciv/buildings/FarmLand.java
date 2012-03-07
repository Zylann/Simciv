package simciv.buildings;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import simciv.Building;
import simciv.Game;
import simciv.World;

public class FarmLand extends Building
{
	private static Image imgDirt;
	
	public FarmLand(World w)
	{
		super(w);
		setSize(2, 2);
	}
	
	public static void loadContent() throws SlickException
	{
		imgDirt = new Image("data/farmland.png");
		imgDirt.setFilter(Image.FILTER_NEAREST);
	}

	@Override
	public void tick()
	{
		increaseTicks();
	}

	@Override
	public void render(Graphics gfx)
	{
		gfx.drawImage(imgDirt, posX * Game.tilesSize, posY * Game.tilesSize);
	}
}
