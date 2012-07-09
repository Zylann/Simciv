package simciv.buildings;

/**
 * Common properties for each building.
 * @author Marc
 *
 */
public class BuildingProperties
{
	public String name;
	public int width;
	public int height;
	public int entryX;
	public int entryY;
	public int zHeight;
	public int unitsCapacity; // How many citizen can be contained?
	public int cost;
	
	public BuildingProperties(String name)
	{
		this.name = name;
		width = 1;
		height = 1;
		zHeight = 1;
		unitsCapacity = 0;
		cost = 0;
	}
	
	/**
	 * Sets the dimensions of the building.
	 * @param width
	 * @param height
	 * @param zHeight : virtual height (as we are in 2D), but may be useful later (for rendering).
	 * @return
	 */
	public BuildingProperties setSize(int width, int height, int zHeight)
	{
		this.width = width >= 1 ? width : 1;
		this.height = height >= 1 ? height : 1;
		this.zHeight = zHeight >= 0 ? zHeight : 0;
		return this;
	}
	
	public BuildingProperties setCost(int cost)
	{
		this.cost = cost;
		return this;
	}
	
	public BuildingProperties setUnitsCapacity(int capacity)
	{
		this.unitsCapacity = capacity >= 0 ? capacity : 0;
		return this;
	}
	
	/**
	 * Sets the entry point of the building.
	 * @param x : X-coordinate relative to origin
	 * @param y : Y-coordinate relative to origin
	 * @return properties object itself for chaining
	 */
	public BuildingProperties setEntryPoint(int x, int y)
	{
		entryX = x;
		entryY = y;
		return this;
	}
}


