package simciv.effects;

import org.newdawn.slick.Graphics;

public abstract class Particle
{
	protected float posX;
	protected float posY;
	protected float velX;
	protected float velY;
	private float timeLeft;
	private float initialTimeLeft;
	
	/**
	 * Creates a particle
	 * @param x : X position in pixels
	 * @param y : Y position in pixels
	 * @param lifeTime : in seconds
	 */
	public Particle(float x, float y, float lifeTime)
	{
		posX = x;
		posY = y;
		initialTimeLeft = lifeTime;
		timeLeft = initialTimeLeft;
	}
	
	public float getK()
	{
		return (float)timeLeft / (float)initialTimeLeft;
	}
	
	/**
	 * Updates the particle
	 * @param delta : frame time in seconds
	 */
	public void update(float delta)
	{
		timeLeft -= delta;
		if(timeLeft < 0)
			timeLeft = 0;
		
		posX += velX * delta;
		posY += velY * delta;
	}
	
	public abstract void render(Graphics gfx);
	
	public boolean isDead()
	{
		return timeLeft <= 0;
	}
	
}



