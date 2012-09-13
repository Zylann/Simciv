package simciv.units;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import backend.Direction2D;
import backend.Vector2i;

import simciv.Game;
import simciv.MapCell;
import simciv.PathFinder;
import simciv.TickableEntity;
import simciv.Map;
import simciv.builds.Build;
import simciv.maptargets.BuildMapTarget;
import simciv.maptargets.IMapTarget;
import simciv.movements.IMovement;
import simciv.movements.PathMovement;

/**
 * An unit can move, and is seen as a "living" thing.
 * Warning : don't move the unit in subclasses, movement is automatically handled by Unit.
 * use setMovement(mvt) to define it.
 * @author Marc
 *
 */
public abstract class Unit extends TickableEntity
{
	private static final long serialVersionUID = 1L;

	// States
	public static final byte NORMAL = 1;
	public static final byte THINKING = 2;
	
	private boolean isAlive;
	private boolean isMoving;
	private IMovement movement;
	private PathFinder pathFinder;
	
	public Unit(Map m)
	{
		super(m);
		direction = Direction2D.random();
		isAlive = true;
		state = NORMAL;
	}
	
	public void setMap(Map m)
	{
		super.setMap(m);
		pathFinder.setMap(m);
	}

	@Override
	protected final void tickEntity()
	{		
		tick(); // Main behavior
		
		// PathFinding
		if(pathFinder != null)
			tickPathFinding();
		
		// Movement
		if(movement != null)
		{
			int lastPosX = getX();
			int lastPosY = getY();
			movement.tick(this);
			isMoving = getX() != lastPosX || getY() != lastPosY;
		}
	}
	
	@Override
	protected final void track()
	{
		mapRef.grid.getCellExisting(getX(), getY()).setUnitInfo(getID());
	}

	@Override
	protected final void untrack()
	{
		MapCell lastCell = mapRef.grid.getCellExisting(getX(), getY());
		if(lastCell.getUnitID() == getID())
			lastCell.eraseUnitInfo();
	}
	
	/**
	 * Starts pathfinding and go to the specified target when a path is found.
	 * The unit will be on the state THINKING while pathfinding.
	 * @param target
	 */
	public void findAndGoTo(IMapTarget target)
	{
		if(target == null)
			return;
		
		pathFinder = new PathFinder(mapRef, getX(), getY(), target);
		
		setState(Unit.THINKING);
		setDirection(Direction2D.NONE);
		setMovement(null);
	}
	
	public void findAndGoTo(Build b)
	{
		findAndGoTo(new BuildMapTarget(b.getID()));
	}
	
	private void tickPathFinding()
	{
		pathFinder.step(8);
		if(pathFinder.isFinished())
		{
			if(pathFinder.getState() == PathFinder.FOUND)
			{
				// I found a path !
				LinkedList<Vector2i> path = pathFinder.retrievePath();
				
				// Remove first pos if we already are on 
				if(path != null && !path.isEmpty())
				{
					if(path.getFirst().equals(getX(), getY()))
						path.pop();
				}
				
				// Start following the path
				setState(Unit.NORMAL);
				setMovement(new PathMovement(path, pathFinder.getTarget()));
				pathFinder = null;
			}
			else
				findAndGoTo(pathFinder.getTarget());
		}
	}
	
	protected void setMovement(IMovement mvt)
	{
		movement = mvt;
	}
	
	public IMapTarget getMovementTarget()
	{
		if(movement == null)
			return null;
		return movement.getTarget();
	}
	
	public boolean isMovement()
	{
		return movement != null;
	}
	
	public boolean isMovementFinished()
	{
		if(movement == null)
			return true;
		return movement.isFinished();
	}
	
	public boolean isMovementBlocked()
	{
		if(movement == null)
			return false;
		return movement.isBlocked();
	}
	
	@Override
	public int getWidth()
	{
		return 1;
	}
	
	@Override
	public int getHeight()
	{
		return 1;
	}
	
	/**
	 * Moves the entity using its current direction, if possible.
	 * @return true if the unit moved, false if not
	 */
	public final boolean moveIfPossible()
	{
		if(direction != Direction2D.NONE)
		{
			int nextPosX = getX() + Direction2D.vectors[direction].x;
			int nextPosY = getY() + Direction2D.vectors[direction].y;
			
			if(mapRef.grid.isCrossable(nextPosX, nextPosY) && 
					mapRef.grid.isRoad(nextPosX, nextPosY))
			{
				move();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Makes the unit move to its current direction (anyways, no collision test !)
	 */
	protected final void move()
	{
		if(direction != Direction2D.NONE)
		{
			setPosition(
					getX() + Direction2D.vectors[direction].x,
					getY() + Direction2D.vectors[direction].y);
		}
	}
	
	/**
	 * Moves the unit according to given available directions
	 * @param dirs : available directions
	 */
	public final void move(List<Byte> dirs)
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
	
	/**
	 * Sets the direction at random from the given list
	 * @param dirs
	 */
	protected void chooseNewDirection(List<Byte> dirs)
	{
		if(!dirs.isEmpty())
			direction = dirs.get((byte) (dirs.size() * Math.random()));
	}
	
	public boolean isAlive()
	{
		return isAlive;
	}
	
	public boolean isMoving()
	{
		return isMoving;
	}
	
	public void kill()
	{
		isAlive = false;
		dispose();
	}
	
	@Override
	public boolean isVisible()
	{
		return true;
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		gfx.pushTransform();
		
		if(Game.settings.renderFancyUnitMovements)
		{
			if(getDirection() != Direction2D.NONE && isMoving())
			{
				float k = -Game.tilesSize * getK();
				Vector2i dir = Direction2D.vectors[getDirection()];
				gfx.translate(k * dir.x, k * dir.y);
			}
		}
		
		gfx.translate(
				getX() * Game.tilesSize,
				getY() * Game.tilesSize - Game.tilesSize / 3);

		renderUnit(gfx);
		
		gfx.popTransform();
		
//		if(pathFinder != null) // debug
//			pathFinder.render(gfx);
	}

	/**
	 * Draws the unit, assuming that graphics are already translated to the good position
	 * @param gfx
	 */
	protected abstract void renderUnit(Graphics gfx);

	@Override
	public int getTickTime()
	{
		return 500;
	}
	
	/**
	 * Renders the unit using a commonly used sprite scheme
	 * @param gfx
	 * @param sprites
	 * @param yShift
	 */
	public final void defaultRender(Graphics gfx, SpriteSheet sprites, int yShift)
	{
		// TODO sprite scheme description
		if(sprites == null)
		{
			// For debug : draws a red quad in place of the sprite
//			gfx.setLineWidth(1);
//			gfx.setColor(Color.red);
//			gfx.drawRect(0, 0, Game.tilesSize, Game.tilesSize);
			return;
		}
		
		if(direction == Direction2D.NONE)
			gfx.drawImage(sprites.getSprite(0, Direction2D.SOUTH + yShift), 0, 0);
		else
			gfx.drawImage(sprites.getSprite(0, direction + yShift), 0, 0);		
	}
	
	public final void defaultRender(Graphics gfx, SpriteSheet sprites)
	{
		defaultRender(gfx, sprites, 0);
	}
		
}

