package simciv;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Resource
{
	// IDs
	public static final byte NONE = 0;
	public static final byte WOOD = 1;
	public static final byte WHEAT = 3;
	public static final byte STRAW = 4;
	public static final byte CLAY = 5;
	public static final byte COUNT = 6;
	
	// list
	private static Resource list[];
	
	// Attributes
	private byte ID;
	private String name;
	private Image carriageSprite;
	private Image storageSprite;
	private short stackLimit;
	
	public static void initialize()
	{
		list = new Resource[COUNT];
		
		ContentManager content = ContentManager.instance();
		
		set(new Resource(NONE, "None", 0));
		set(new Resource(WOOD, "Wood", 100));
		set(new Resource(WHEAT, "Wheat", 100)).setSprites(content.getImage("resource.wheat"));
		set(new Resource(STRAW, "Straw", 100));
		set(new Resource(CLAY, "Clay", 100));
		//...
	}
	
	private static Resource set(Resource r)
	{
		list[r.ID] = r;
		return r;
	}
	
	public static Resource get(byte id)
	{
		return list[id];
	}
		
	// Member part

	private Resource(byte ID, String name, int stackLimit)
	{
		this.ID = ID;
		this.name = name;
		this.stackLimit = (short) stackLimit;
	}
		
	private Resource setSprites(Image sprites)
	{
		// TODO split images with a spritesheet
		carriageSprite = sprites;
		storageSprite = sprites;
		return this;
	}
	
	public void renderCarriage(Graphics gfx, int x, int y, int amount)
	{
		if(ID != NONE)
			gfx.drawImage(carriageSprite, x, y);
	}
	
	public void renderStorage(Graphics gfx, int x, int y, int amount)
	{
		if(ID != NONE)
		{
			if(amount > 0)
				gfx.drawImage(storageSprite, x, y);
			if(amount > 30)
				gfx.drawImage(storageSprite, x, y - 2);
			if(amount > 60)
				gfx.drawImage(storageSprite, x, y - 4);
			if(amount == 100)
				gfx.drawImage(storageSprite, x, y - 6);
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


