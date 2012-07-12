package simciv;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 * An object that can be rendered, and also defining a drawing order.
 * @author Marc
 *
 */
public interface IRenderable
{
	/**
	 * Draws the object on the screen
	 * @param gc
	 * @param game
	 * @param gfx
	 */
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx);
	
	/**
	 * Is the object must be drawn?
	 * @return
	 */
	public boolean isVisible();
	
	/**
	 * Returns the drawing order of the object
	 * @return
	 */
	public int getDrawOrder();
}
