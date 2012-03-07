package simciv;

import java.util.List;

public abstract class Unit extends Entity
{	
	public static final byte NORMAL = 1;
	public static int count = 0;

	byte dir;
	
	public Unit(World w)
	{
		super(w);
		
		dir = Direction2D.EAST;
		count++;
	}

	@Override
	public void tick()
	{
		increaseTicks();
		move();
	}
	
	protected void move()
	{
		// Find available directions
		List<Byte> dirs = Road.getAvailableDirections(worldRef.map, posX, posY);
		
		if(!dirs.isEmpty())
		{
			if(dirs.size() == 1) // only one direction
			{
				dir = dirs.get(0);
			}
			else if(dirs.size() == 2) // two directions
			{
				// remove U-turn
				if(dir != Direction2D.NONE)
					dirs.remove((Byte)Direction2D.opposite[dir]);
				// use the remaining direction
				dir = dirs.get(0);
			}
			else
			{
				// remove U-turn
				if(dir != Direction2D.NONE)
					dirs.remove((Byte)Direction2D.opposite[dir]);
				// Choose a direction at random
				chooseNewDirection(dirs);
			}
		}
		else
			dir = Direction2D.NONE;
		
		if(dir != Direction2D.NONE)
		{
			posX += Direction2D.vectors[dir].x;
			posY += Direction2D.vectors[dir].y;
		}
	}
	
	protected void chooseNewDirection(List<Byte> dirs)
	{
		if(!dirs.isEmpty())
		{
			// Choosing a direction at random
			dir = dirs.get((byte) (dirs.size() * Math.random()));
		}
	}
}

