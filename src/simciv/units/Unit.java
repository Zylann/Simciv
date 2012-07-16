package simciv.units;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Direction2D;
import simciv.Entity;
import simciv.Game;
import simciv.Road;
import simciv.Vector2i;
import simciv.World;

/**
 * An unit can move, and is seen as a "living" thing
 * @author Marc
 *
 */
public abstract class Unit extends Entity
{	
	// States
	public static final byte NORMAL = 1;
	public static final byte THINKING = 2;	
	// Counts all the units
	public static int count = 0;
	
	boolean isAlive;
		
	public Unit(World w)
	{
		super(w);
		
		direction = Direction2D.EAST;
		count++;
		isAlive = true;
		state = NORMAL;
	}
	
	/**
	 * Moves the entity using its current direction, if possible.
	 * @return true if the unit moved, false if not
	 */
	public boolean moveIfPossible()
	{
		if(direction != Direction2D.NONE)
		{
			int nextPosX = posX + Direction2D.vectors[direction].x;
			int nextPosY = posY + Direction2D.vectors[direction].y;
			
			if(worldRef.map.isCrossable(nextPosX, nextPosY) && 
					worldRef.map.isRoad(nextPosX, nextPosY))
			{
				move();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Makes the unit move to its current direction (anyways)
	 */
	protected void move()
	{
		if(direction != Direction2D.NONE)
		{
			posX += Direction2D.vectors[direction].x;
			posY += Direction2D.vectors[direction].y;
		}
	}
	
	public void moveAtRandomFollowingRoads()
	{
		move(Road.getAvailableDirections(worldRef.map, posX, posY));
	}
	
	/**
	 * Moves the unit according to given available directions
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
		
		// Apply movement
		move();
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
	
	@Override
	public boolean isVisible()
	{
		return isOut();
	}
		
	protected final void beginRenderForFancyMovements(Graphics gfx)
	{
		if(getDirection() != Direction2D.NONE)
		{
			float k = -Game.tilesSize * getK();
			Vector2i dir = Direction2D.vectors[getDirection()];
			gfx.translate(k * dir.x, k * dir.y);
		}
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		gfx.pushTransform();
		
		if(Game.renderFancyUnitMovements)
			beginRenderForFancyMovements(gfx);
		
		renderUnit(gfx);
		
		gfx.popTransform();
	}

	protected abstract void renderUnit(Graphics gfx);

	@Override
	protected int getTickTime()
	{
		return 0;
	}

	protected final void defaultRender(Graphics gfx, Image sprite)
	{
		gfx.translate(
				posX * Game.tilesSize,
				posY * Game.tilesSize - Game.tilesSize / 3);

		if(sprite == null)
		{
			// For debug
//			gfx.setLineWidth(1);
//			gfx.setColor(Color.red);
//			gfx.drawRect(0, 0, Game.tilesSize, Game.tilesSize);
			return;
		}
		
		gfx.drawImage(sprite,
				0, 0,
				Game.tilesSize, Game.tilesSize,
				0, direction * Game.tilesSize,
				Game.tilesSize, (direction + 1) * Game.tilesSize);
	}
		
}

