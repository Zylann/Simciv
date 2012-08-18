package simciv.effects;

import org.newdawn.slick.Graphics;

import simciv.Game;

public abstract class VisualEffect
{
	// Graphical position
	protected float posX;
	protected float posY;
		
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
		
	public void render(Graphics gfx)
	{
		gfx.pushTransform();
		gfx.translate(posX, posY);
		
		renderEffect(gfx);
		
		gfx.popTransform();
	}

	public abstract void update(int delta);	
	protected abstract void renderEffect(Graphics gfx);
	public abstract boolean isFinished();
	
}

