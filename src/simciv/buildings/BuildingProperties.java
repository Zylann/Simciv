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
	public int zHeight;
	public int capacity;
	public int cost;
	
	public BuildingProperties(String name)
	{
		this.name = name;
		width = 1;
		height = 1;
		zHeight = 1;
		capacity = 0;
		cost = 0;
	}
	
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
	
	public BuildingProperties setCapacity(int capacity)
	{
		this.capacity = capacity >= 0 ? capacity : 0;
		return this;
	}
}


