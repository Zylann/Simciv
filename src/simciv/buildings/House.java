package simciv.buildings;

import java.util.HashMap;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import simciv.Game;
import simciv.World;
import simciv.units.Citizen;

/**
 * Every citizen need a house. Houses produce citizens.
 * @author Marc
 *
 */
public class House extends Building
{
	private static BuildingProperties properties;
	private static Image sprite;
	
	static
	{
		properties = new BuildingProperties("House");
		properties.setCapacity(2).setCost(50).setSize(1, 1, 1);
	}

	// References to citizen living here
	HashMap<Integer,Citizen> inhabitants = new HashMap<Integer,Citizen>();

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
	public boolean isHouse()
	{
		return true;
	}

	@Override
	public void tick()
	{
		increaseTicks();
		if(state == Building.CONSTRUCTION)
		{
			if(getTicks() > 30)
			{
				state = Building.NORMAL;
				produceCitizen();
			}
		}
	}

	protected boolean produceCitizen()
	{
		Citizen c = new Citizen(worldRef);
		if(addInhabitant(c))
		{
			worldRef.spawnUnit(c, posX, posY);
			return true;
		}
		return false;
	}

	public boolean addInhabitant(Citizen c)
	{
		if(inhabitants.size() < getProperties().capacity)
		{
			if(inhabitants.put(c.getID(), c) == null)
			{
				c.setHouse(this);
				return true;
			}
		}
		return false;
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
					(posY + 1) * Game.tilesSize,
					direction * Game.tilesSize, 0,
					(direction + 1) * Game.tilesSize, Game.tilesSize * 2);
		}
	}

	@Override
	public BuildingProperties getProperties()
	{
		return properties;
	}

	@Override
	public void onDestruction()
	{
		for(Citizen c : inhabitants.values())
		{
			c.kill();
		}
	}
}




