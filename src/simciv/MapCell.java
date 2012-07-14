package simciv;

import org.newdawn.slick.Color;
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
	public byte road;		// Road configuration, -1 for none.
	public byte noise;		// Random bits for fancy renderings
	
	// 4 bytes of information about the building occupying the cell (value 0 for none) :
	// oeiiiiii iiiiiiii iiiiiiii iiiiiiii
	// i = 30 lowest bits : building ID
	// o = last bit : isOrigin : is this cell at the origin of the building?
	// e = next bit : isEntryPoint : is this cell at an entry point of the building?
	private int buildingInfo;
	
	private static int BUILDING_INFO_ID_MASK = 0x3fffffff; // 30 bits to 1, 2 higher bits to 0
	private static int BUILDING_INFO_ORIGIN_MASK = 0x80000000; // 2 higher bits : 10
	private static int BUILDING_INFO_ENTRY_MASK = 0x40000000; // 2 higher bits : 01
	
	public MapCell()
	{
		terrainID = Terrain.GRASS;
		road = -1;
		buildingInfo = 0;
	}
	
	public void eraseBuildingInfo()
	{
		buildingInfo = 0;
	}
	
	public void setBuildingInfo(int id, boolean isOrigin, boolean isEntryPoint)
	{
		buildingInfo = id & BUILDING_INFO_ID_MASK;
		if(isOrigin)
			buildingInfo |= BUILDING_INFO_ORIGIN_MASK;
		if(isEntryPoint)
			buildingInfo |= BUILDING_INFO_ENTRY_MASK;
	}
	
	public boolean isBuilding()
	{
		return buildingInfo != 0;
	}
	
	public int getBuildingID()
	{
		return buildingInfo & BUILDING_INFO_ID_MASK;
	}
	
	public boolean isBuildingOrigin()
	{
		return (buildingInfo & BUILDING_INFO_ORIGIN_MASK) != 0;
	}
	
	public boolean isBuildingEntryPoint()
	{
		return (buildingInfo & BUILDING_INFO_ENTRY_MASK) != 0;
	}

	public void set(MapCell other)
	{
		terrainID = other.terrainID;
		nature = other.nature;
		road = other.road;
		buildingInfo = other.buildingInfo;
		noise = other.noise;
	}
	
	public void setBuildingMark(int ID)
	{
		buildingInfo = ID;
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
		if(isBuilding()) // building
			return false;
		return true;
	}
	
	public boolean isCrossable()
	{
		return terrainID != Terrain.WATER && !isBuilding();
	}

	public void renderGround(int x, int y, Graphics gfx)
	{
		int gx = x * Game.tilesSize;
		int gy = y * Game.tilesSize;
		
		Terrain.get(terrainID).render(gfx, x, y);

		if(isRoad())
			Road.render(gfx, road, gx, gy);
	}
	
	public void renderNatureElement(int gx, int gy, Graphics gfx)
	{
		if(nature != Nature.NONE)
			Nature.render(gfx, this, gx, gy);
	}

	public Color getMinimapColor()
	{
		if(isBuilding())
			return new Color(255, 128, 0);
		if(isRoad())
			return new Color(224, 224, 224);
		if(nature != Nature.NONE)
			return new Color(0, 128, 0);
		return Terrain.get(terrainID).minimapColor;
	}
	
}


