package simciv;

import java.util.ArrayList;

public abstract class Building extends Entity
{
	public static final byte CONSTRUCTION = 0;
	public static final byte NORMAL = 1;
	public static final byte FIRE = 2;
	public static final byte RUINS = 3;
	
	int width;
	int height;
	ArrayList<Unit> units = new ArrayList<Unit>();
	
	public Building(World w)
	{
		super(w);
		width = 1;
		height = 1;
		state = CONSTRUCTION;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}

