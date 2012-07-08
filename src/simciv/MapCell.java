package simciv;

import org.newdawn.slick.Graphics;

/**
 * This class represents one cell of the map
 * @author Marc
 *
 */
public class MapCell
{
	public byte terrainID;	// Ground information
	public byte nature;		// Natural objects
	public byte road;		// Road configuration
	public byte noise;		// Random bits for fancy renderings
	public int building;	// ID of the building occupying the cell (fast access)
	
	public MapCell()
	{
		terrainID = Terrain.GRASS;
		road = -1;
		building = -1;
	}
	
	public void set(MapCell other)
	{
		terrainID = other.terrainID;
		nature = other.nature;
		road = other.road;
		building = other.building;
		noise = other.noise;
	}
	
	public void setBuildingMark(int ID)
	{
		building = ID;
	}

	public boolean isRoad()
	{
		return road != -1;
	}

	public boolean canPlaceObject()
	{
		if(nature != 0)
			return false;
		if(isRoad()) // road
			return false;
		if(terrainID == Terrain.WATER) // invalid terrain
			return false;
		if(building >= 0) // building
			return false;
		return true;
	}
	
	public boolean isCrossable()
	{
		return terrainID != Terrain.WATER && building == -1;
	}

	public void render(int x, int y, Graphics gfx)
	{
		int gx = x * Game.tilesSize;
		int gy = y * Game.tilesSize;
		
		Terrain.get(terrainID).render(gfx, x, y);

		if(isRoad())
			Road.render(gfx, road, gx, gy);
		
		if(nature != Nature.NONE)
			Nature.render(gfx, this, gx, gy);
	}
	
}


