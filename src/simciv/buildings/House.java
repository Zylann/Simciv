package simciv.buildings;

import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Cheats;
import simciv.ContentManager;
import simciv.Game;
import simciv.Road;
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
	private static Sound newCitizenSound;
	
	static
	{
		properties = new BuildingProperties("House");
		properties.setUnitsCapacity(2).setCost(50).setSize(1, 1, 1);
	}

	// References to citizen living here
	HashMap<Integer,Citizen> inhabitants = new HashMap<Integer,Citizen>();

	public House(World w)
	{
		super(w);
		if(sprite == null)
			sprite = ContentManager.instance().getImage("build.smallHouse");
		if(newCitizenSound == null)
			newCitizenSound = ContentManager.instance().getSound("build.openDoor");
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
		if(state == Building.CONSTRUCTION)
		{
			if((getTicks() > 30 || Cheats.isFastCitizenProduction()) && 
					Road.isAvailableDirections(worldRef.map, posX, posY))
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
			newCitizenSound.play(1.f, 0.25f);
			return true;
		}
		return false;
	}

	public boolean addInhabitant(Citizen c)
	{
		if(inhabitants.size() < getProperties().unitsCapacity)
		{
			if(inhabitants.put(c.getID(), c) == null)
			{
				c.setHouse(this);
				return true;
			}
		}
		return false;
	}

	public void removeInhabitant(int id)
	{
		inhabitants.remove(id);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
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

	@Override
	protected int getTickTime()
	{
		return 500;
	}

	public int getNbInhabitants()
	{
		return inhabitants.size();
	}

	@Override
	public String getInfoString()
	{
		return "[" + getProperties().name + "] inhabitants : " + getNbInhabitants();
	}

	@Override
	public void onInit()
	{
	}

}




