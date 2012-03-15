package simciv;

import org.newdawn.slick.Graphics;

public abstract class Entity
{
	private static int nextEntityID = 0;
	
	protected int posX;
	protected int posY;
	private int ID;
	private int nbTicks;
	protected byte state; // different means depending on units or buildings
	protected short healthPoints;
	protected byte direction;
	protected transient World worldRef;
	
	public Entity(World w)
	{
		worldRef = w;
		ID = nextEntityID++;
		direction = Direction2D.SOUTH;
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
		
	public int getTicks()
	{
		return nbTicks;
	}
	
	public byte getDirection()
	{
		return direction;
	}
	
	/**
	 * Increases the tick counter.
	 * It must be called one time in tick() implementation.
	 */
	protected void increaseTicks()
	{
		nbTicks++;
	}
	
	/**
	 * Called regularly to make the entity "live" and execute its tasks.
	 */
	public abstract void tick();
	
	/**
	 * Called to draw the entity
	 * @param gfx
	 */
	public abstract void render(Graphics gfx);
	
	public abstract void onDestruction();
}

