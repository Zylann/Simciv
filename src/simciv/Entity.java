package simciv;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

/**
 * An entity is a located game element that can update and draw itself.
 * In this game, entity coordinates are integers (map grid position)
 * @author Marc
 *
 */
public abstract class Entity extends GameComponent
{
	private int posX;
	private int posY;
	private int lifeTime;
	protected byte state; // different means depending on units or buildings
	protected int healthPoints;
	protected byte direction;
	protected transient Map mapRef;
	
	public Entity(Map m)
	{
		super();
		mapRef = m;
		direction = Direction2D.SOUTH;
	}
		
	public byte getState()
	{
		return state;
	}
	
	public Map getMap()
	{
		return mapRef;
	}
	
	public void setState(byte newState)
	{
		state = newState;
	}
	
	public int getHealthPoints()
	{
		return healthPoints;
	}
	
	/**
	 * Set the entity's position.
	 * For buildings, it is used as the origin position.
	 * @param x : X in map cells
	 * @param y : Y in map cells
	 */
	public void setPosition(int x, int y)
	{
		if(!isDisposed() && isInitialized())
		{
			untrack();
			posX = x;
			posY = y;
			track();
		}
		else
		{
			posX = x;
			posY = y;
		}
	}
	
	@Override
	public void onInit()
	{
		super.onInit();
		track();
	}

	@Override
	protected void onDispose()
	{
		super.onDispose();
		untrack();
	}
	
	/**
	 * Get entity position/origin X
	 * @return
	 */
	public int getX()
	{
		return posX;
	}
	
	/**
	 * Get entity position/origin Y
	 * @return
	 */
	public int getY()
	{
		return posY;
	}
	
	/**
	 * Get entity bounding width
	 * @return
	 */
	public abstract int getWidth();
	
	/**
	 * Get entity bounding height
	 * @return
	 */
	public abstract int getHeight();
	
	/**
	 * Returns the life time of the entity in milliseconds
	 * @return
	 */
	public int getLifeTime()
	{
		return lifeTime;
	}
		
	public void setDirection(byte dir)
	{
		direction = dir;
	}
	
	public byte getDirection()
	{
		return direction;
	}

	@Override
	public int getDrawOrder()
	{
		return posY;
	}
	
	public boolean isTrackable()
	{
		return false;
	}

	/**
	 * Sets entity information on the map grid, if available.
	 */
	protected void track()
	{
		// By default, no need to track
	}
	
	/**
	 * Removes entity information from the map grid, if available.
	 */
	protected void untrack()
	{
		// By default, no need to untrack
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
	{
		lifeTime += delta;
	}

}

