package simciv;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

/**
 * World view : allows for conversions between pixel coordinates
 * and world coordinates, and scrolling.
 * @author Marc
 *
 */
public class View
{
	static final float acceleration = 5000.f;
	static final float velocityMax = 750.f;
	
	Vector2f origin;
	Vector2f velocity;
	int scale;
	
	public View(float x, float y, int scale)
	{
		origin = new Vector2f(x, y);
		velocity = new Vector2f();
		this.scale = scale;
	}
	
	public void update(GameContainer gc, float delta)
	{
		Input input = gc.getInput();
		
		float a = acceleration * delta;
		Vector2f va = new Vector2f();
		int border = 4;
		
		if(!gc.isFullscreen())
			border = -1;
		
		/* Move the view with keyboard or mouse */
		
		if(input.isKeyDown(Input.KEY_Q) || input.getMouseX() < border)
			va.x = -a;
		else if(input.isKeyDown(Input.KEY_D) || input.getMouseX() > gc.getWidth() - border)
			va.x = a;
		else
			velocity.x = MathHelper.diminishVelocity(velocity.x, a);
		
		if(input.isKeyDown(Input.KEY_Z) || input.getMouseY() < border)
			va.y = -a;
		else if(input.isKeyDown(Input.KEY_S) || input.getMouseY() > gc.getHeight() - border)
			va.y = a;
		else
			velocity.y = MathHelper.diminishVelocity(velocity.y, a);
		
		velocity.add(va);
		
		// Limit speed
		float speed = velocity.length();
		if(speed > velocityMax)
		{
			velocity.normalise();
			velocity.scale(velocityMax);
		}
		
		// Apply movement
		origin.x += velocity.x * delta;
		origin.y += velocity.y * delta;
		
		// Limit origin to world bounds
		if(origin.x < 0)
			origin.x = 0;
		if(origin.y < 0)
			origin.y = 0;
	}

	/**
	 * Configures the drawing context before rendering the world.
	 * @param gfx : context
	 */
	public void configureGraphicsForWorldRendering(Graphics gfx)
	{
		gfx.resetTransform();
		gfx.translate(-origin.x, -origin.y);
		gfx.scale(scale, scale);
	}
	
	public Vector2i convertCoordsToMap(int x, int y)
	{
		Vector2i pos = new Vector2i(
				(x + (int)(origin.x)) / (Game.tilesSize * scale),
				(y + (int)(origin.y)) / (Game.tilesSize * scale));
		if(x + origin.x < 0)
			pos.x -= 1;
		if(y + origin.y < 0)
			pos.y -= 1;
		return pos;
	}
	
	public IntRange2D getRange(GameContainer gc)
	{
		IntRange2D r = new IntRange2D(
				(int)origin.x,
				(int)origin.y, 
				(int)origin.x + gc.getWidth(),
				(int)origin.y + gc.getHeight());
		r.divide(scale);
		return r;
	}
	
	public IntRange2D getMapRange(GameContainer gc)
	{
		IntRange2D r = getRange(gc);
		r.divide(Game.tilesSize);
		return r;
	}
}


