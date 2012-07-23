package simciv.effects;

import org.newdawn.slick.Graphics;

import simciv.Game;

public abstract class VisualEffect
{
	// Graphical position
	protected float posX;
	protected float posY;
	// Time control (milliseconds)
	protected int duration;
	protected int time;
	
	protected boolean finished;
	
	/**
	 * Creates a visual effect
	 * @param x : x position in cells
	 * @param y : y position in cells
	 */
	public VisualEffect(int x, int y)
	{
		posX = Game.tilesSize * x;
		posY = Game.tilesSize * y;
	}
	
	protected float getK()
	{
		return (float)time / (float)duration;
	}
	
	public boolean isFinished()
	{
		return finished;
	}
	
	public void update(int delta)
	{
		if(finished)
			return;
		time += delta;
		updateEffect(delta);
		if(time >= duration)
			return;
	}
	
	protected abstract void updateEffect(int delta);
	public abstract void render(Graphics gfx);
}

