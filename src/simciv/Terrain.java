package simciv;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import simciv.content.Content;

public class Terrain
{
	/* Static part */
	
	public static final byte VOID = 0;
	public static final byte WATER = 1;
	public static final byte GRASS = 2;
	public static final byte DUST = 3;
	public static final int count = 4;
	
	private static Terrain terrains[];
	
	public static void initialize() throws SlickException
	{
		terrains = new Terrain[count];
		
		// Create terrains
		set(new Terrain(VOID, "void", null)).setMinimapColor(Color.black);
		set(new Terrain(WATER, "water", Content.sprites.terrainWater));
		set(new Terrain(GRASS, "grass", Content.sprites.terrainGrass));
		set(new Terrain(DUST, "dust", Content.sprites.terrainDust));
		
		// Load content		
		for(int i = 0; i < count; i++)
			terrains[i].loadContent();
	}
	
	public static void updateTerrains(int delta)
	{
		for(int i = 0; i < count; i++)
			terrains[i].update(delta);
	}
	
	private static Terrain set(Terrain t)
	{
		terrains[t.ID] = t;
		return t;
	}
	
	public static Terrain get(byte ID)
	{
		return terrains[ID];
	}
	
	/* Member part */

	byte ID;
	Image texture;
	Animation anim;
	Color minimapColor;
	String name;
	
	Terrain(byte ID, String name, Image texture)
	{
		this.ID = ID;
		this.texture = texture;
		this.name = name;
	}
	
	public Terrain setMinimapColor(Color clr)
	{
		minimapColor = clr;
		return this;
	}
	
	private void loadContent() throws SlickException
	{
		if(texture != null)
			minimapColor = MathHelper.mean(texture);
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


