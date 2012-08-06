package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Entity;
import simciv.Game;
import simciv.Map;
import simciv.ResourceSlot;
import simciv.World;
import simciv.content.Content;

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
			constructionSprite = Content.images.buildConstructing1x1;
		state = NORMAL;
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
	
	public int getZHeight()
	{
		return getProperties().zHeight;
	}
	
	public boolean is1x1()
	{
		return getProperties().width == 1 && getProperties().height == 1;
	}
	
	public void renderAsConstructing(Graphics gfx)
	{
		// TODO handle size upper than 1x1
		gfx.drawImage(constructionSprite, 0, 0);
	}

	public boolean isHouse()
	{
		return false;
	}

	public boolean isWorkplace()
	{
		return false;
	}
	
	@Override
	public boolean isVisible()
	{
		return true;
	}
	
	/**
	 * Returns a brief information about the building
	 * @return
	 */
	public abstract String getInfoString();

	/**
	 * Return true if the building can store resources
	 * @return
	 */
	public boolean isAcceptResources()
	{
		return false;
	}

	/**
	 * Stores a resource in the building. Depending on if the building is accepting
	 * resources, the given slot will or will not be modified.
	 * @param carriedResource
	 */
	public void storeResource(ResourceSlot r)
	{
	}
	
	@Override
	public final void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		gfx.pushTransform();
		gfx.translate(posX * Game.tilesSize, posY * Game.tilesSize);
		
		renderBuilding(gc, game, gfx);
		
		gfx.popTransform();
	}
	
	protected void renderDefault(Graphics gfx, Image sprite)
	{
		gfx.drawImage(sprite, 0, -getZHeight() * Game.tilesSize);
	}

	protected abstract void renderBuilding(GameContainer gc, StateBasedGame game, Graphics gfx);

	public boolean canBePlaced(Map map, int x, int y)
	{
		return map.canPlaceObject(x, y, getWidth(), getHeight());
	}
	
}

