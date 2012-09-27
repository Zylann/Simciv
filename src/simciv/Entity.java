package simciv;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import backend.Direction2D;
import backend.GameComponent;
import backend.IntRange2D;

// TODO write a generic Entity class in backend (for easy re-use) supporting both float and int positions
/**
 * An entity is a located game element that can update and draw itself.
 * In this game, entity coordinates are integers (map grid position)
 * @author Marc
 *
 */
public abstract class Entity extends GameComponent
{
	private static final long serialVersionUID = 1L;
	
	private static final byte DEFAULT_STATE = -1;
	
	/** Position X of the entity in map cells **/
	private int posX;	
	/** Position Y of the entity in map cells **/
	private int posY;
	
	/** Life time of the entity in milliseconds.
	 * Note that it will not evoluate 1 by 1. **/
	private int lifeTime;
	
	/** State of the entity. different meanings depending on the inheriting classes **/
	protected byte state;
	
	/** Health points of the entity. It dies if they reach 0. **/
	protected int healthPoints;
	
	/** Direction of the entity **/
	protected byte direction;
	
	/** Reference to the map where the entity is (parent-child) **/
	protected transient Map mapRef;
	
	/**
	 * Constructs a basic entity linked to a map.
	 * @param m
	 */
	public Entity(Map m)
	{
		super();
		mapRef = m;
		direction = Direction2D.SOUTH;
		state = DEFAULT_STATE;
	}
	
	/**
	 * Sets the map of the entity.
	 * Warning : this method is reserved for loading only.
	 * @param m
	 */
	public void setMap(Map m)
	{
		mapRef = m;
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
	
	public abstract String getDisplayableName();
	
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
	
	@Override
	public void getRenderBounds(IntRange2D range)
	{
		range.set(posX, posY, posX + getWidth() - 1, posY + getHeight() - 1);
	}

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

