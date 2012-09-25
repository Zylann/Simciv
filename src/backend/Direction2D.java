package backend;

import org.newdawn.slick.Graphics;

import backend.geom.Vector2i;

/**
 * Utility class for handling 2D directions
 * @author Marc
 *
 */
public class Direction2D
{
	public static final byte NONE = -1;
	public static final byte WEST = 0;
	public static final byte EAST = 1;
	public static final byte NORTH = 2;
	public static final byte SOUTH = 3;
			
	public static final Vector2i vectors[];
	public static final byte opposite[] = {EAST, WEST, SOUTH, NORTH};
	
	static
	{
		vectors = new Vector2i[4];

		vectors[WEST] = new Vector2i(-1, 0);
		vectors[EAST] = new Vector2i(1, 0);
		vectors[NORTH] = new Vector2i(0, -1);
		vectors[SOUTH] = new Vector2i(0, 1);
	}
	
	/**
	 * Converts 2 vectors into a directionnal constant (4-connexity)
	 * @param from
	 * @param to
	 * @return directionnal constant
	 */
	public static byte toDirection(Vector2i from, Vector2i to)
	{
		int dx = to.x - from.x;
		int dy = to.y - from.y;
		
		if(dy == 0)
		{
			if(dx < 0)
				return WEST;
			if(dx > 0)
				return EAST;
		}
		else if(dx == 0)
		{
			if(dy < 0)
				return NORTH;
			if(dy > 0)
				return SOUTH;
		}
		
		return NONE;
	}
	
	/**
	 * @return a non-NONE random directionnal constant
	 */
	public static byte random()
	{
		return (byte) (4 * Math.random());
	}
	
	/**
	 * Draws a simple arrow as using an 1:1 scale.
	 * The current transformation matrix will not be altered.
	 * @param gfx
	 * @param x : origin X of the bounding square
	 * @param y : origin Y of the bounding square
	 * @param d : direction of the arrow
	 */
	public static void drawArrow(Graphics gfx, float x, float y, byte d)
	{
		switch(d)
		{
		case Direction2D.WEST :
			// Left arrow
			gfx.drawLine(x, y + 0.5f, x + 1, y + 0.5f);
			gfx.drawLine(x, y + 0.5f, x + 0.25f, y + 0.25f);
			gfx.drawLine(x, y + 0.5f, x + 0.25f, y + 0.75f);
			break;
		case Direction2D.EAST :
			// Right arrow
			gfx.drawLine(x, y + 0.5f, x + 1, y + 0.5f);
			gfx.drawLine(x + 1, y + 0.5f, x + 0.75f, y + 0.25f);
			gfx.drawLine(x + 1, y + 0.5f, x + 0.75f, y + 0.75f);
			break;
		case Direction2D.NORTH :
			// Up arrow
			gfx.drawLine(x + 0.5f, y, x + 0.5f, y + 1);
			gfx.drawLine(x + 0.5f, y, x + 0.25f, y + 0.25f);
			gfx.drawLine(x + 0.5f, y, x + 0.75f, y + 0.25f);
			break;
		case Direction2D.SOUTH :
			// Down arrow
			gfx.drawLine(x + 0.5f, y, x + 0.5f, y + 1);
			gfx.drawLine(x + 0.5f, y + 1, x + 0.25f, y + 0.75f);
			gfx.drawLine(x + 0.5f, y + 1, x + 0.75f, y + 0.75f);
			break;
		default : break;
		}
	}
	
}


