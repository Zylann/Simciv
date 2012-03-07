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
	transient World worldRef;
	
	public Entity(World w)
	{
		worldRef = w;
		ID = nextEntityID++;
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
	
	protected void increaseTicks()
	{
		nbTicks++;
	}
	
	public abstract void tick();
	
	public abstract void render(Graphics gfx);
}
