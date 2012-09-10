package simciv;

/**
 * Classes implementing this interface can be registered by a MapGrid
 * in order to receive notifications.
 * @author Marc
 *
 */
public interface IMapGridListener
{
	/**
	 * Called when a cell of the map has changed
	 * @param cell : concerned cell
	 * @param x : cell position X
	 * @param y : cell position Y
	 */
	public void onCellChange(MapCell cell, int x, int y);
	
}

