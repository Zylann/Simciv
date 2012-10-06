package backend;

import java.io.Serializable;

import org.newdawn.slick.Graphics;

import simciv.Game;


/**
 * 2D camera with translation and scaling.
 * @author Marc
 *
 */
public class View implements Serializable, IView
{
	private static final long serialVersionUID = 1L;
		
	protected float originX; // in pixels
	protected float originY;
	protected int scale;
	protected int containerWidth;
	protected int containerHeight;
	
	public View(float x, float y, int scale)
	{
		originX = x;
		originY = y;
		this.scale = scale;
	}
	
	public void setContainerSize(int w, int h)
	{
		containerWidth = w;
		containerHeight = h;
	}	
	
	@Override
	public float getOriginX()
	{
		return originX;
	}
	
	@Override
	public float getOriginY()
	{
		return originY;
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
	
	@Override
	public void getBounds(IntRange2D range)
	{
		range.set(
				(int)originX,
				(int)originY, 
				(int)originX + containerWidth,
				(int)originY + containerHeight);
		range.divide(scale);
	}
	
	public int getScale()
	{
		return scale;
	}
	
	public void setCenter(float x, float y)
	{
		// TODO use real center
		// TODO make generic
		setOrigin(x - 16, y - 16);
	}

	@Override
	public void setOrigin(float x, float y)
	{
		// TODO make generic
		originX = scale * Game.tilesSize * x;
		originY = scale * Game.tilesSize * y;
	}

}


