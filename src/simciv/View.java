package simciv;

import java.io.Serializable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

/**
 * Determines what region of the map is currently seen by the player.
 * Allows for conversions between pixel coordinates and map coordinates, and scrolling.
 * @author Marc
 *
 */
public class View implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final float acceleration = 5000.f;
	private static final float velocityMax = 750.f;
	
	private float originX; // in pixels
	private float originY;
	private transient Vector2f velocity;
	private transient Vector2i mapSize;
	private int scale;
	
	public View(float x, float y, int scale)
	{
		originX = x;
		originY = y;
		mapSize = new Vector2i();
		this.scale = scale;
	}
	
	public void setMapSize(int sizeX, int sizeY)
	{
		mapSize = new Vector2i();
		mapSize.x = scale * Game.tilesSize * (sizeX > 0 ? sizeX : 0);
		mapSize.y = scale * Game.tilesSize * (sizeY > 0 ? sizeY : 0);
	}
	
	public float getOriginX()
	{
		return originX;
	}
	
	public float getOriginY()
	{
		return originY;
	}
	
	public int getMapX()
	{
		return ((int)originX) / scale / Game.tilesSize;
	}
	
	public int getMapY()
	{
		return ((int)originY) / scale / Game.tilesSize;
	}
		
	public void update(GameContainer gc, float delta)
	{
		Input input = gc.getInput();
				
		float a = acceleration * delta;
		Vector2f va = new Vector2f();
		int border = 4;
		
		// Mouse-at-border scrolling is disabled in windowed mode
		if(!gc.isFullscreen())
			border = -1;
		
		/* Move the view with keyboard or mouse */
		
		if(velocity == null)
			velocity = new Vector2f();
		
		if(input.isKeyDown(Input.KEY_Q) || 
				input.isKeyDown(Input.KEY_LEFT) || 
				input.getMouseX() < border)
			va.x = -a;
		else if(input.isKeyDown(Input.KEY_D) || 
				input.isKeyDown(Input.KEY_RIGHT) || 
				input.getMouseX() > gc.getWidth() - border)
			va.x = a;
		else
			velocity.x = MathHelper.diminishVelocity(velocity.x, a);
		
		if(input.isKeyDown(Input.KEY_Z) || 
				input.isKeyDown(Input.KEY_UP) ||
				input.getMouseY() < border)
			va.y = -a;
		else if(input.isKeyDown(Input.KEY_S) ||
				input.isKeyDown(Input.KEY_DOWN) ||
				input.getMouseY() > gc.getHeight() - border)
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
		originX += velocity.x * delta;
		originY += velocity.y * delta;
		
		// Limit origin to world bounds
		if(originX < 0)
			originX = 0;
		if(originY < 0)
			originY = 0;
		if(originX >= mapSize.x - gc.getWidth())
			originX = mapSize.x - gc.getWidth();
		if(originY >= mapSize.y - gc.getHeight())
			originY = mapSize.y - gc.getHeight();
	}

	/**
	 * Configures the drawing context before rendering the world.
	 * @param gfx : context
	 */
	public void configureGraphicsForWorldRendering(Graphics gfx)
	{
		gfx.resetTransform();
		gfx.translate(-originX, -originY);
		gfx.scale(scale, scale);
	}
	
	/**
	 * Converts screen coordinates into map coordinates,
	 * using the current state of the view
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2i convertCoordsToMap(int x, int y)
	{
		Vector2i pos = new Vector2i(
				(x + (int)(originX)) / (Game.tilesSize * scale),
				(y + (int)(originY)) / (Game.tilesSize * scale));
		if(x + originX < 0)
			pos.x -= 1;
		if(y + originY < 0)
			pos.y -= 1;
		return pos;
	}
	
	public IntRange2D getRange(GameContainer gc)
	{
		IntRange2D r = new IntRange2D(
				(int)originX,
				(int)originY, 
				(int)originX + gc.getWidth(),
				(int)originY + gc.getHeight());
		r.divide(scale);
		return r;
	}
	
	public IntRange2D getMapRange(GameContainer gc)
	{
		IntRange2D r = getRange(gc);
		r.divide(Game.tilesSize);
		return r;
	}

	public int getScale()
	{
		return scale;
	}

	public void setCenter(int mapX, int mapY)
	{
		// TODO use real center
		originX = scale * Game.tilesSize * (mapX - 16);
		originY = scale * Game.tilesSize * (mapY - 16);
	}

	public void setOrigin(float x, float y)
	{
		originX = scale * Game.tilesSize * x;
		originY = scale * Game.tilesSize * y;
	}
	
}


