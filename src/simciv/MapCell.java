package simciv;

import java.io.Serializable;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * This class represents one cell of the map
 * @author Marc
 *
 */
public class MapCell implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static int BUILD_INFO_ID_MASK = 0x3fffffff; // 30 bits to 1, 2 higher bits to 0
	private static int BUILD_INFO_ORIGIN_MASK = 0x80000000; // 2 higher bits = 10

	/** Ground information **/
	public byte terrainID;
	
	/** Natural objects **/
	public byte nature;
	
	/** Road configuration, -1 for none. **/
	public byte road;
	
	/** Random bits for fancy renderings **/
	public byte noise;
		
	/**
	 * 4 bytes of information about the building occupying the cell (value 0 for none) :
	 * oiiiiiii iiiiiiii iiiiiiii iiiiiiii 
	 * i = 30 lowest bits : building ID (set by tracking)
	 * e = next bit : isEntryPoint : is this cell at an entry point of the building?
	 */
	private int buildInfo;
	
	/** ID of the last unit being on the cell (set by tracking), 0 if none **/
	private int unitInfo;
		
	public MapCell()
	{
		terrainID = Terrain.GRASS;
		road = -1;
	}
	
	@Override
	public String toString()
	{
		String str = "TID=" + terrainID + ", N=" + nature + ", R=" + road + ", BID=" + getBuildID();
		if(isBuildOrigin())
			str += "o";
		return str;
	}
	
	public void eraseBuildInfo()
	{
		buildInfo = 0;
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
	
	public void setBuildInfo(int id, boolean isOrigin)
	{
		buildInfo = id & BUILD_INFO_ID_MASK;
		if(isOrigin)
			buildInfo |= BUILD_INFO_ORIGIN_MASK;
	}
	
	public boolean isBuild()
	{
		return buildInfo != 0;
	}
	
	public int getBuildID()
	{
		return buildInfo & BUILD_INFO_ID_MASK;
	}
	
	public boolean isBuildOrigin()
	{
		return (buildInfo & BUILD_INFO_ORIGIN_MASK) != 0;
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
		buildInfo = other.buildInfo;
		noise = other.noise;
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
		if(isBuild()) // building
			return false;
		if(isUnit()) // unit
			return false;
		return true;
	}
	
	public boolean isWalkable()
	{
		return terrainID != Terrain.WATER && !isBuild() && nature != Nature.TREE;
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
		if(isBuild())
		{
			gfx.setColor(new Color(255, 128, 128));
			gfx.setLineWidth(2);
			gfx.drawRect(x * Game.tilesSize, y * Game.tilesSize, Game.tilesSize-2, Game.tilesSize-2);
		}
	}

	public Color getMinimapColor(Map w)
	{
		if(isBuild())
		{
			if(w != null)
				return w.getBuild(getBuildID()).getMinimapColor();
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


