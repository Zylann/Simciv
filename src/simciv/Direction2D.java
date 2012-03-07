package simciv;

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
}
