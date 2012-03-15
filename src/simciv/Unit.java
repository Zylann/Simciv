package simciv;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * An unit can move, and is seen as a "living" thing
 * @author Marc
 *
 */
public abstract class Unit extends Entity
{	
	public static final byte NORMAL = 1;
	public static int count = 0;
	
	boolean isAlive;
		
	public Unit(World w)
	{
		super(w);
		
		direction = Direction2D.EAST;
		count++;
		isAlive = true;
	}
	
	/**
	 * Moves the unit according to available directions
	 * @param dirs : available directions
	 */
	protected void move(List<Byte> dirs)
	{		
		if(!dirs.isEmpty())
		{
			if(dirs.size() == 1) // only one direction
			{
				direction = dirs.get(0);
			}
			else if(dirs.size() == 2) // two directions
			{
				// remove U-turn
				if(direction != Direction2D.NONE)
					dirs.remove((Byte)Direction2D.opposite[direction]);
				// use the remaining direction
				direction = dirs.get(0);
			}
			else
			{
				// remove U-turn
				if(direction != Direction2D.NONE)
					dirs.remove((Byte)Direction2D.opposite[direction]);
				// Choose a direction at random
				chooseNewDirection(dirs);
			}
		}
		else
			direction = Direction2D.NONE;
		
		if(direction != Direction2D.NONE)
		{
			posX += Direction2D.vectors[direction].x;
			posY += Direction2D.vectors[direction].y;
		}
	}
	
	protected void chooseNewDirection(List<Byte> dirs)
	{
		if(!dirs.isEmpty())
		{
			// Choosing a direction at random
			direction = dirs.get((byte) (dirs.size() * Math.random()));
		}
	}
	
	public boolean isAlive()
	{
		return isAlive;
	}
	
	public void kill()
	{
		isAlive = false;
	}
	
	/**
	 * Is the unit outside?
	 * @return
	 */
	public boolean isOut()
	{
		return true;
	}
	
	protected final void defaultRender(Graphics gfx, Image sprite)
	{
		gfx.translate(0, - Game.tilesSize / 3);
		gfx.drawImage(sprite,
				posX * Game.tilesSize,
				posY * Game.tilesSize,
				(posX + 1) * Game.tilesSize,
				(posY + 1) * Game.tilesSize,
				0, direction * Game.tilesSize,
				Game.tilesSize, (direction + 1) * Game.tilesSize);
	}
	
	
}

