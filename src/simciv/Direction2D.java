package simciv;

import java.util.LinkedList;
import java.util.List;

public class Direction2D
{
	public static final byte NONE = -1;
	public static final byte WEST = 0;
	public static final byte EAST = 1;
	public static final byte NORTH = 2;
	public static final byte SOUTH = 3;
			
	public static final Vector2i vectors[];
//	public static final byte bits[] = {1, 2, 4, 8};
	public static final byte opposite[] = {1, 0, 3, 2};
	
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
	 * Converts a series of points into a series of directionnal constants
	 * (uses toDirection())
	 * @param points
	 * @return
	 */
	public static LinkedList<Byte> toDirectionsPath(List<Vector2i> points)
	{
		LinkedList<Byte> path = new LinkedList<Byte>();
		Vector2i lastPos = null;
		for(Vector2i pos : points)
		{
			if(lastPos != null)
				path.add(toDirection(lastPos, pos));
			lastPos = pos;
		}
		return path;
	}
	
	/**
	 * @return a non-NONE random directionnal constant
	 */
	public static byte random()
	{
		return (byte) (4 * Math.random());
	}
	
}


