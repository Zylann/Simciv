package simciv.units;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import backend.Direction2D;
import backend.geom.Vector2i;
import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;
import simciv.Game;
import simciv.MapCell;
import simciv.TickableEntity;
import simciv.Map;
import simciv.movement.IMovement;
import simciv.movement.PathMovement;

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
	private static final int DEFAULT_MAX_PATHFINDING_DISTANCE = 4096; // Big distance
		
	private boolean isAlive;
	private boolean isMoving;
	private IMovement movement;
	
	public Unit(Map m)
	{
		super(m);
		direction = Direction2D.random();
		isAlive = true;
	}
	
	public void setMap(Map m)
	{
		super.setMap(m);
	}

	@Override
	protected final void tickEntity()
	{		
		tick(); // Main behavior
		
		// Movement
		if(movement != null)
		{
			int lastPosX = getX();
			int lastPosY = getY();
			movement.tick(this);
			isMoving = getX() != lastPosX || getY() != lastPosY;
		}
		else
			isMoving = false;
	}
	
	@Override
	protected final void track()
	{
		MapCell nextCell = mapRef.grid.getCellExisting(getX(), getY());
		if(!nextCell.isUnit())
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
	 * Finds a path from the current position to the given target and makes the unit follow it.
	 * The computed path will pass on cells that complies with the given MapSpec.
	 * This method will have no effect if no path is found.
	 * If the last point of the computed path is not walkable, it will be removed
	 * (but the path will still valid as it will lead nearby the target).
	 * @param mapSpec : cells where we can pass
	 * @param target
	 * @param distance : max length of the path
	 * @return true if success, false if no path found.
	 */
	public boolean findAndGoTo(IMapSpec mapSpec, IMapTarget target, int maxDistance)
	{
		if(maxDistance <= 0)
			Log.error(this + "No paths will be found with maxDistance=" + maxDistance);
		
		// Configure pathfinding
		mapRef.multiPathFinder.setFindBlockedTargets(true);
		mapRef.multiPathFinder.setMaxDistance(maxDistance);
		
		// Do pathfinding
		LinkedList<Vector2i> path =
			mapRef.multiPathFinder.findPath(getX(), getY(), mapSpec, target);
		
		if(path != null)
		{			
			followPath(path);
			return true; // Path found
		}
		else
		{
			Log.debug(this + " path not found (maxDistance=" + maxDistance + ")");
			return false; // Path not found
		}
	}
	
	public boolean findAndGoTo(IMapTarget target, int maxDistance)
	{
		return findAndGoTo(new DefaultPass(), target, maxDistance);
	}
	
	public boolean findAndGoTo(IMapTarget target)
	{
		return findAndGoTo(new DefaultPass(), target, DEFAULT_MAX_PATHFINDING_DISTANCE);
	}
		
	/**
	 * Makes the unit follow a path.
	 * If the path is invalid, the old movement will be cleared (the unit will not move).
	 * @param path : path to follow
	 */
	protected void followPath(LinkedList<Vector2i> path)
	{
		if(path == null || path.isEmpty()) {
			Log.error(this + " Can't follow path. Path is null or empty.");
			return;
		}
		
		setMovement(null);
		
		// Remove first pos (if we already are on)
		Vector2i firstPos = path.getFirst();
		if(firstPos.equals(getX(), getY()))
		{
			path.removeFirst();		
			if(path.isEmpty()) {
				Log.debug(this + " Can't follow path (1) : already on target position.");
				return;
			}
		}
		
		// If the final pos is not walkable, remove it
		Vector2i lastPos = path.getLast();
		if(!mapRef.grid.isWalkable(lastPos.x, lastPos.y))
		{
			path.removeLast();
			if(path.isEmpty()) {
				Log.debug(this + " Can't follow path (2) : already on target position.");
				return;
			}
		}

		// Follow the path
		setMovement(new PathMovement(path));
	}
	
	/**
	 * Sets the movement behavior of the unit.
	 * Note : if you want set a path driven movement, use followPath() instead.
	 * @param mvt
	 */
	protected void setMovement(IMovement mvt)
	{
		movement = mvt;
	}
	
	/**
	 * Returns true if the unit has a defined movement.
	 * @return
	 */
	public boolean isMovement()
	{
		return movement != null;
	}
	
	/**
	 * Returns true if unit's movement is finished or not defined.
	 * @return
	 */
	public boolean isMovementFinished()
	{
		if(movement == null)
			return true;
		return movement.isFinished();
	}
	
	/**
	 * Returns true if the unit is blocked using its current movement definition.
	 * Returns false if the unit is not blocked of if it has no defined movement.
	 * @return
	 */
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
	 * If the direction is not walkable, will do nothing.
	 * @return true if the unit moved, false if not
	 */
	public final boolean moveIfPossible()
	{
		if(direction != Direction2D.NONE)
		{
			int nextPosX = getX() + Direction2D.vectors[direction].x;
			int nextPosY = getY() + Direction2D.vectors[direction].y;
			
			if(mapRef.isWalkable(nextPosX, nextPosY))
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
	protected final boolean move()
	{
		if(direction != Direction2D.NONE)
		{
			setPosition(
					getX() + Direction2D.vectors[direction].x,
					getY() + Direction2D.vectors[direction].y);
			return true;
		}
		return false;
	}
	
	/**
	 * Moves the unit at random according to given available directions
	 * @param dirs : available directions
	 */
	public final boolean move(List<Byte> dirs)
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
			else // N>0 directions
			{
				// remove U-turn
				if(direction != Direction2D.NONE)
					dirs.remove((Byte)Direction2D.opposite[direction]);
				// Choose a direction at random
				chooseNewDirection(dirs);
			}
		}
		else // No direction
			direction = Direction2D.NONE;
		
		// Apply movement
		return move();
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
		
		if(Game.settings.isRenderFancyUnitMovements())
		{
			// Interpolate last pos with next pos using tick time progress
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
	 * @param tyShift : tile Y coord of the sprites to use on the sheet
	 */
	public final void renderDefault(Graphics gfx, SpriteSheet sprites, int tyShift)
	{
		// TODO sprite scheme description		
		if(direction == Direction2D.NONE)
			gfx.drawImage(sprites.getSprite(0, Direction2D.SOUTH + tyShift), 0, 0);
		else
			gfx.drawImage(sprites.getSprite(0, direction + tyShift), 0, 0);		
	}
	
	public final void renderDefault(Graphics gfx, SpriteSheet sprites)
	{
		renderDefault(gfx, sprites, 0);
	}
	
	public String getInfoLine()
	{
		return "{" + getDisplayableName() + "}";
	}
	
	/**
	 * Default map pass predicate for units.
	 * Defines where a unit can move.
	 * @author Marc
	 *
	 */
	private class DefaultPass implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.grid.isWalkable(x, y);
		}	
	}
		
}

