package simciv;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

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
	
	private static Image tree;
	
	public static void loadContent()
	{
		tree = ContentManager.instance().getImage("nature.tree");
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
		if(cell.nature == TREE)
		{
			// Apply a little offset to seem more "natural"
			if((cell.noise & 0x01) != 0)
				gx -= 2;
			else if((cell.noise & 0x02) != 0)
				gx += 2;
			else if((cell.noise & 0x03) != 0)
				gy -= 2;
			else if((cell.noise & 0x04) != 0)
				gy += 2;
			
			gfx.drawImage(tree, gx, gy - (tree.getHeight() - Game.tilesSize));
		}
	}
}



