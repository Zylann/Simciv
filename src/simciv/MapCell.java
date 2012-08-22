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
	private transient int buildingInfo;
	
	// ID of the last unit on the cell, 0 if none
	private transient int unitInfo;
	
	private static int BUILDING_INFO_ID_MASK = 0x3fffffff; // 30 bits to 1, 2 higher bits to 0
	private static int BUILDING_INFO_ORIGIN_MASK = 0x80000000; // 2 higher bits : 10
	
	public MapCell()
	{
		terrainID = Terrain.GRASS;
		road = -1;
	}
	
	@Override
	public String toString()
	{
		String str = "TID=" + terrainID + ", N=" + nature + ", R=" + road + ", BID=" + getBuildingID();
		if(isBuildingOrigin())
			str += "o";
		return str;
	}
	
	public void eraseBuildingInfo()
	{
		buildingInfo = 0;
	}
	
	public void setUnitInfo(int id)
	{
		unitInfo = id;
	}
	
	public void eraseUnitInfo()
	{
		unitInfo = 0;
	}
	
	public int getUnitID()
	{
		return unitInfo;
	}
	
	public boolean isUnit()
	{
		return getUnitID() != 0;
	}
	
	public void setBuildingInfo(int id, boolean isOrigin)
	{
		buildingInfo = id & BUILDING_INFO_ID_MASK;
		if(isOrigin)
			buildingInfo |= BUILDING_INFO_ORIGIN_MASK;
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
	
	public boolean isArable()
	{
		return terrainID == Terrain.GRASS;
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
		if(isUnit()) // unit
			return false;
		return true;
	}
	
	public boolean isCrossable()
	{
		return terrainID != Terrain.WATER && !isBuilding() && nature != Nature.TREE;
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
	
	/**
	 * Renders debug data
	 * @param gfx
	 * @param x : X cell position
	 * @param y : Y cell position
	 */
	public void renderData(Graphics gfx, int x, int y)
	{
		if(isUnit())
		{
			gfx.setColor(new Color(128, 128, 255, 128));
			gfx.fillRect(x * Game.tilesSize, y * Game.tilesSize, Game.tilesSize, Game.tilesSize);
		}
		if(isBuilding())
		{
			gfx.setColor(new Color(255, 128, 128));
			gfx.setLineWidth(2);
			gfx.drawRect(x * Game.tilesSize, y * Game.tilesSize, Game.tilesSize, Game.tilesSize);
		}
	}

	public Color getMinimapColor(World w)
	{
		if(isBuilding())
		{
			if(w != null)
				return w.getBuilding(getBuildingID()).getMinimapColor();
			else
				return new Color(255, 128, 0);
		}
		if(isRoad())
			return Road.minimapColor;
		if(nature != Nature.NONE)
			return Nature.getMinimapColor(nature);			
		return Terrain.get(terrainID).minimapColor;
	}
	
}


