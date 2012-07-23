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
	public static final byte DUST = 3;
	public static final int count = 4;
	
	private static Terrain terrains[];
	
	public static void initialize() throws SlickException
	{
		terrains = new Terrain[count];
		
		// Create terrains
		set(new Terrain(VOID, "void")).setMinimapColor(Color.black);
		set(new Terrain(WATER, "water"));
		set(new Terrain(GRASS, "grass"));
		set(new Terrain(DUST, "dust"));
		
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
	String textureName;
	Animation anim;
	Color minimapColor;
	String name;
	
	Terrain(byte ID, String name)
	{
		this.ID = ID;
		if(!name.equals("void")) // void terrains have no texture
			this.textureName = "terrain." + name;
		this.name = name;
	}
	
	public Terrain setMinimapColor(Color clr)
	{
		minimapColor = clr;
		return this;
	}
	
	private void loadContent() throws SlickException
	{
		if(textureName != null)
		{
			texture = ContentManager.instance().getImage(textureName);
			minimapColor = MathHelper.mean(texture);
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


