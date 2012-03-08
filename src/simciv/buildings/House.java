package simciv.buildings;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import simciv.Building;
import simciv.BuildingProperties;
import simciv.Game;
import simciv.World;

public class House extends Building
{
	private static BuildingProperties properties;
	private static Image sprite;
	
	static
	{
		properties = new BuildingProperties("House");
		properties.setCapacity(5).setCost(50).setSize(1, 1, 1);
	}
	
	public static void loadContent() throws SlickException
	{
		sprite = new Image("data/house1.png");
		sprite.setFilter(Image.FILTER_NEAREST);
	}

	public House(World w)
	{
		super(w);
		direction = (byte) (4 * Math.random());
	}

	@Override
	public void tick()
	{
		increaseTicks();
	}

	@Override
	public void render(Graphics gfx)
	{
		if(state == CONSTRUCTION)
		{
			renderAsConstructing(gfx);
		}
		else
		{
			gfx.drawImage(sprite,
					posX * Game.tilesSize,
					(posY - 1)* Game.tilesSize,
					(posX + 1) * Game.tilesSize,
					(posY + 1)* Game.tilesSize,
					direction * Game.tilesSize, 0,
					(direction +1) * Game.tilesSize, 32);
		}
	}

	@Override
	public BuildingProperties getProperties()
	{
		return properties;
	}
}
