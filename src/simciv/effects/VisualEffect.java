package simciv.effects;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import backend.GameComponent;

import simciv.Game;

public abstract class VisualEffect extends GameComponent
{
	private static final long serialVersionUID = 1L;
	
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
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		gfx.pushTransform();
		gfx.translate(posX, posY);
		
		renderEffect(gfx);
		
		gfx.popTransform();
	}

//	@Override
//	public void update(GameContainer gc, StateBasedGame game, int delta)
//	{
//		// TODO Auto-generated method stub
//	}

	protected abstract void renderEffect(Graphics gfx);

	@Override
	public boolean isVisible()
	{
		return true;
	}

	@Override
	public int getDrawOrder()
	{
		return 0;
	}

	@Override
	public void onDestruction()
	{
	}
	
}

