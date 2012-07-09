package simciv;

import org.newdawn.slick.Graphics;

/**
 * An entity is a located game element that can update and draw itself.
 * In this game, the update is discretized into ticks :
 * update() uses real-time parameters, tick() does not.
 * Tick times are specific to each entity.
 * @author Marc
 *
 */
public abstract class Entity
{
	// Used to generate unique IDs
	// It MUST start with 1 (map storage convenience)
	private static int nextEntityID = 1;
	
	protected int posX;
	protected int posY;
	private int ID;
	private int lifeTime;
	private int nbTicks;
	private int timeBeforeNextTick;
	protected byte state; // different means depending on units or buildings
	protected short healthPoints;
	protected byte direction;
	protected transient World worldRef;
	
	public Entity(World w)
	{
		worldRef = w;
		ID = nextEntityID++;
		direction = Direction2D.SOUTH;
		timeBeforeNextTick = getTickTime();
	}
	
	public int getID()
	{
		return ID;
	}
	
	public byte getState()
	{
		return state;
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
		posX = x;
		posY = y;
	}
	
	public int getX()
	{
		return posX;
	}
	
	public int getY()
	{
		return posY;
	}
	
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
	
	/**
	 * Called regularly by the game update method.
	 * @param deltaMs
	 */
	public final void update(int deltaMs)
	{
		lifeTime += deltaMs;
		timeBeforeNextTick -= deltaMs;
		
		if(timeBeforeNextTick < 0)
		{
			timeBeforeNextTick += getTickTime();
			if(timeBeforeNextTick < 0)
				timeBeforeNextTick = 0;
			tick();
			nbTicks++;
		}
	}
	
	/**
	 * Returns the tick ratio.
	 * This value is always increasing, reaching 1 before its tick(),
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
	
	/**
	 * Called to draw the entity
	 * @param gfx
	 */
	public abstract void render(Graphics gfx);
		
	/**
	 * Called just before the entity is destroyed
	 */
	public abstract void onDestruction();
}

