package backend;


/**
 * 2D view (camera)
 * @author Marc
 *
 */
public interface IView
{
	/**
	 * Returns the X coordinate of the top-left corner of the view in pixels
	 * @return
	 */
	public float getOriginX();
	
	/**
	 * Returns the Y coordinate of the top-left corner of the view in pixels
	 * @return
	 */
	public float getOriginY();
	
	/**
	 * Sets the given range object to the bounds of the view in pixels
	 * @param range
	 */
	public void getBounds(IntRange2D range);
	
	/**
	 * Moves the view to have its origin at the given pos.
	 */
	public void setOrigin(float x, float y);

	/**
	 * Moves the view to have its center at the given pos.
	 * @param x
	 * @param y
	 */
	public void setCenter(float x, float y);

}
