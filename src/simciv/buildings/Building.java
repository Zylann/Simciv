package simciv.buildings;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import simciv.Entity;
import simciv.Game;
import simciv.World;

public abstract class Building extends Entity
{
	private static Image constructionSprite;
	
	public static final byte CONSTRUCTION = 0;
	public static final byte NORMAL = 1;
	public static final byte FIRE = 2;
	public static final byte RUINS = 3;
		
	public static void loadContent() throws SlickException
	{
		constructionSprite = new Image("data/buildplace.png");
		constructionSprite.setFilter(Image.FILTER_NEAREST);
	}
	
	public Building(World w)
	{
		super(w);
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
}

