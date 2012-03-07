package simciv;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Terrain
{
	/* Static part */
	
	public static final byte VOID = 0;
	public static final byte WATER = 1;
	public static final byte GRASS = 2;
	public static final int count = 3;
	
	static Terrain terrains[];
	
	public static void initialize() throws SlickException
	{
		terrains = new Terrain[count];
		
		// Create terrains
		set(new Terrain(VOID, "void", ""));
		set(new Terrain(WATER, "water", "data/water.png"));
		set(new Terrain(GRASS, "grass", "data/grass.png"));
		
		// Load content		
		for(int i = 0; i < count; i++)
		{
			terrains[i].loadContent();
		}
	}
	
	public static void updateTerrains(int delta)
	{
		for(int i = 0; i < count; i++)
			terrains[i].update(delta);
	}
	
	private static void set(Terrain t)
	{
		terrains[t.ID] = t;
	}
	
	public static Terrain get(byte ID)
	{
		return terrains[ID];
	}
	
	/* Member part */

	byte ID;
	Image texture;
	String textureName;
	Animation anim;
	Color minimapColor;
	String name;
	
	Terrain(byte ID, String name, String texName)
	{
		this.ID = ID;
		this.textureName = texName;
		this.name = name;
	}
	
	private void loadContent() throws SlickException
	{
		if(!textureName.isEmpty())
		{
			texture = new Image(textureName);
			texture.setFilter(Image.FILTER_NEAREST);
		}
	}
	
	public void update(int delta)
	{
		if(anim != null)
			anim.update(delta);
	}
	
	public void render(Graphics gfx, int x, int y)
	{
		if(texture != null)
		{
			if(anim == null)
				gfx.drawImage(texture, Game.tilesSize * x, Game.tilesSize * y);
			else
				anim.draw(Game.tilesSize * x, Game.tilesSize * y);
		}
	}
	
	public byte getID()
	{
		return ID;
	}
}


