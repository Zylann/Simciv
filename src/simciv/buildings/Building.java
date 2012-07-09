package simciv.buildings;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import simciv.ContentManager;
import simciv.Entity;
import simciv.Game;
import simciv.World;

public abstract class Building extends Entity
{
	private static Image constructionSprite;
	
	// Common states
	public static final byte CONSTRUCTION = 0;
	public static final byte NORMAL = 1;
	public static final byte ACTIVE = 2;
	public static final byte FIRE = 3;
	public static final byte RUINS = 4;
	
	public Building(World w)
	{
		super(w);
		if(constructionSprite == null)
			constructionSprite = ContentManager.instance().getImage("city.buildingPlace");
		state = CONSTRUCTION;
	}
	
	public abstract BuildingProperties getProperties();
	
	public int getWidth()
	{
		return getProperties().width;
	}
	
	public int getHeight()
	{
		return getProperties().height;
	}
	
	public void renderAsConstructing(Graphics gfx)
	{
		gfx.drawImage(constructionSprite, Game.tilesSize * posX, Game.tilesSize * posY);
	}

	public boolean isHouse()
	{
		return false;
	}

	public boolean isWorkplace()
	{
		return false;
	}
}

