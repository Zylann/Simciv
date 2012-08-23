package simciv;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

/**
 * An entity is a located game element that can update and draw itself.
 * In this game, the update is discretized into ticks :
 * update() uses real-time parameters, tick() does not.
 * tick() is called at each tick time interval, usually more than the frame time.
 * Tick times are specific to each entity.
 * @author Marc
 *
 */
public abstract class Entity extends GameComponent
{
	private int posX;
	private int posY;
	private int lifeTime;
	private int nbTicks;
	private int timeBeforeNextTick;
	protected byte state; // different means depending on units or buildings
	protected short healthPoints;
	protected byte direction;
	protected transient World worldRef;
	
	public Entity(World w)
	{
		super();
		worldRef = w;
		direction = Direction2D.SOUTH;
		timeBeforeNextTick = getTickTime();
	}
		
	public byte getState()
	{
		return state;
	}
		
	public World getWorld()
	{
		return worldRef;
	}
	
	public void setState(byte newState)
	{
		state = newState;
	}
	
	public short getHealthPoints()
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
	public void dispose()
	{
		untrack();
		super.dispose();
	}

	/**
	 * Sets entity information on the map, when available.
	 */
	protected void track()
	{
		// By default, no need to track
	}
	
	/**
	 * Removes entity information from the map, when available.
	 */
	protected void untrack()
	{
		// By default, no need to untrack
	}
	
	public int getX()
	{
		return posX;
	}
	
	public int getY()
	{
		return posY;
	}
	
	public abstract int getWidth();
	public abstract int getHeight();
	
	/**
	 * Returns the life time of the entity in milliseconds
	 * @return
	 */
	public int getLifeTime()
	{
		return lifeTime;
	}
		
	public int getTicks()
	{
		return nbTicks;
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

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
	{
		lifeTime += delta;
		timeBeforeNextTick -= delta;
		
		if(timeBeforeNextTick < 0)
		{
			timeBeforeNextTick += getTickTime();
			if(timeBeforeNextTick < 0)
				timeBeforeNextTick = 0;
			
			tickEntity();
			
			nbTicks++;
		}
	}
	
	protected void tickEntity()
	{
		tick();
	}
	
	/**
	 * Returns the tick ratio.
	 * This value is always increasing, reaching 1 before next call to tick(),
	 * and turns back to zero after each tick().
	 * For example, if the entity has waited the half of its time before tick again,
	 * getK() will return 0.5.
	 * @return ratio between 0 and 1
	 */
	public final float getK()
	{
		return (float)timeBeforeNextTick / (float)(getTickTime());
	}
	
	/**
	 * Converts seconds into ticks looking towards the entity
	 * @param s : seconds
	 * @return
	 */
	public final int secondsToTicks(float s)
	{
		return (int) ((1000.f * s) / getTickTime());
	}
		
	/**
	 * Returns the time interval between each behavior update.
	 * It can be used to increase its speed for example.
	 * @return : time per tick in milliseconds
	 */
	protected abstract int getTickTime();

	/**
	 * Called regularly to make the entity "live" and execute its tasks.
	 */
	protected abstract void tick();

}

