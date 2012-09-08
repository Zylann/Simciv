package simciv;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import simciv.content.Content;

/**
 * Natural elements on the map
 * @author Marc
 *
 */
public class Nature
{
	// Constants
	public static final byte NONE = 0;
	public static final byte TREE = 1;
	public static final byte BUSH = 2;
	// TODO add flowers and tall grass
	
	// TODO use spritesheets
	private static Image treeSprites[] = new Image[3];
	
	public static void initialize()
	{
		treeSprites[0] = Content.sprites.natureTree;
		treeSprites[1] = Content.sprites.natureTree2;
		treeSprites[2] = Content.sprites.natureTree3;
	}
	
	/**
	 * Renders the natural element from the given cell
	 * @param gfx : Graphics object
	 * @param cell
	 * @param gx : graphical X pos (cellX * Game.tilesSize)
	 * @param gy : graphical Y pos (cellY * Game.tilesSize)
	 */
	public static void render(Graphics gfx, MapCell cell, int gx, int gy)
	{
		if(cell.nature != 0)
		{
			// Apply a small offset to seem more "natural"
			if((cell.noise & 0x01) != 0) // 0000 0001
				gx -= 1;
			else if((cell.noise & 0x02) != 0) // 0000 0010
				gx += 1;
			else if((cell.noise & 0x03) != 0) // 0000 0011
				gy -= 1;
			else if((cell.noise & 0x04) != 0) // 0000 0100
				gy += 1;
		}
		
		if(cell.nature == TREE)
		{
			// Visual variants
			int i = 0;
			if((cell.noise & 0x10) != 0) // 0001 0000
				i = 1;
			else if((cell.noise & 0x20) != 0) // 0010 000
				i = 2;
			
			gfx.drawImage(treeSprites[i], gx, gy - (treeSprites[i].getHeight() - Game.tilesSize));
		}
		else if(cell.nature == BUSH)
		{
			// Visual variants
			int i = 3;
			if((cell.noise & 0x40) != 0) // 0100 0000
				i = 0;
			else if((cell.noise & 0x30) != 0) // 0010 000
				i = 2;
			else if((cell.noise & 0x20) != 0) // 0001 000
				i = 1;
			
			gfx.drawImage(Content.sprites.natureBush.getSprite(i, 0), gx, gy);
		}
	}

	public static Color getMinimapColor(byte nature)
	{
		if(nature == BUSH)
			return new Color(16, 96, 16);
		else
			return new Color(0, 64, 0);
	}
	
}



