package simciv.builds;

/**
 * Common properties for each building.
 * @author Marc
 *
 */
public class BuildProperties
{
	public String name; // Displayable name
	public int width;
	public int height;
	public int zHeight;
	public int unitsCapacity; // Base units capacity
	public int cost; // Base cost
	public byte category;
	public boolean isRepeatable; // Can the build be repeated by dragging the cursor?
	public boolean isFlamable;
	
	public BuildProperties(String name)
	{
		this.name = name;
		width = 1;
		height = 1;
		zHeight = 1;
		unitsCapacity = 0;
		cost = 0;
		isRepeatable = false;
		isFlamable = true;
	}
	
	/**
	 * Sets the dimensions of the building.
	 * @param width
	 * @param height
	 * @param zHeight : virtual height (as we are in 2D), but may be useful later (for rendering).
	 * @return object for chaining
	 */
	public BuildProperties setSize(int width, int height, int zHeight)
	{
		this.width = width >= 1 ? width : 1;
		this.height = height >= 1 ? height : 1;
		this.zHeight = zHeight >= 0 ? zHeight : 0;
		return this;
	}
	
	public BuildProperties setCost(int cost)
	{
		this.cost = cost;
		return this;
	}
	
	public BuildProperties setUnitsCapacity(int capacity)
	{
		this.unitsCapacity = capacity >= 0 ? capacity : 0;
		return this;
	}
	
	public BuildProperties setCategory(byte categoryID)
	{
		this.category = categoryID;
		return this;
	}
	
	public BuildProperties setRepeatable(boolean f)
	{
		this.isRepeatable = f;
		return this;
	}
	
	public BuildProperties setFlamable(boolean f)
	{
		this.isFlamable = f;
		return this;
	}
	
}


