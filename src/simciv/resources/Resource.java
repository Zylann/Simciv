package simciv.resources;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import backend.Direction2D;

import simciv.Game;
import simciv.content.Content;

/**
 * Information about gameplay resources (food, raw materials, manufactured...)
 * @author Marc
 *
 */
public class Resource
{
	// IDs
	public static final byte NONE = -1;
	public static final byte WOOD = 0;
	public static final byte WHEAT = 1;
	public static final byte MEAT = 2;
	public static final byte LOGS = 3;
	public static final byte COUNT = 4;
	
	// list
	private static Resource list[];
	
	public static void initialize()
	{		
		list = new Resource[COUNT];
		
		set(new Resource(WOOD, "Wood", 100));
		
		set(new Resource(WHEAT, "Wheat", 100))
			.setSprites(
					Content.sprites.resourceWheatCarriage,
					Content.sprites.resourceWheat)
			.setIsFood(true);
		
		set(new Resource(MEAT, "Meat", 100))
			.setSprites(
					Content.sprites.resourceMeatCarriage,
					Content.sprites.resourceMeat)
			.setIsFood(true);
		
		set(new Resource(LOGS, "Logs", 100))
			.setSprites(
					Content.sprites.resourceLogsCarriage,
					Content.sprites.resourceLogs);
		
		//...
	}
	
	private static Resource set(Resource r)
	{
		list[r.ID] = r;
		return r;
	}
	
	/**
	 * Get properties for the specified resource ID.
	 * The ID MUST be valid.
	 * @param id : resource id in [0, Resource.COUNT]
	 * @return
	 */
	public static Resource get(byte id)
	{
		return list[id];
	}
		
	// Member part
	
	// Attributes
	private byte ID;
	private String name;
	private SpriteSheet carriageSprites;
	private SpriteSheet storageSprites;
	private short stackLimit;
	private boolean isFood;

	private Resource(byte ID, String name, int stackLimit)
	{
		this.ID = ID;
		this.name = name;
		this.stackLimit = (short) stackLimit;
		isFood = false;
	}
		
	private Resource setSprites(Image carriageSpr, Image storageSpr)
	{
		carriageSprites = new SpriteSheet(carriageSpr, Game.tilesSize, Game.tilesSize);
		if(storageSpr != null)
			storageSprites = new SpriteSheet(storageSpr, Game.tilesSize, storageSpr.getHeight());
		return this;
	}
	
	private Resource setIsFood(boolean f)
	{
		isFood = f;
		return this;
	}
	
	public boolean isFood()
	{
		return isFood;
	}
	
	public void renderCarriage(Graphics gfx, int x, int y, int amount, byte direction)
	{
		if(direction == Direction2D.NONE)
			direction = Direction2D.SOUTH;
		gfx.drawImage(carriageSprites.getSprite(0, direction), x, y);
	}
	
	public static void renderEmptyCarriage(Graphics gfx, int x, int y, byte direction)
	{
		if(direction == Direction2D.NONE)
			direction = Direction2D.SOUTH;
		gfx.drawImage(Content.sprites.resourceEmptyCarriage.getSprite(0, direction), x, y);
	}

	public void renderStorage(Graphics gfx, int x, int y, int amount)
	{
		if(storageSprites != null)
		{
			if(amount > 0 && amount < 30)
				gfx.drawImage(storageSprites.getSprite(0, 0), x, y - Game.tilesSize);
			else if(amount >= 30 && amount < 60)
				gfx.drawImage(storageSprites.getSprite(1, 0), x, y - Game.tilesSize);
			else if(amount >= 60 && amount < 100)
				gfx.drawImage(storageSprites.getSprite(2, 0), x, y - Game.tilesSize);
			else if(amount == 100)
				gfx.drawImage(storageSprites.getSprite(3, 0), x, y - Game.tilesSize);
		}
	}
	
	public short getStackLimit()
	{
		return stackLimit;
	}

	public String getName()
	{
		return name;
	}

}


