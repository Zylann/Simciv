package simciv;

import org.newdawn.slick.Graphics;

public class MapCell
{
	public byte terrainID;
	public byte road;
	public int building;

	public MapCell()
	{
		terrainID = Terrain.GRASS;
		road = -1;
		building = -1;
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
		Terrain.get(terrainID).render(gfx, x, y);

		if(isRoad())
			Road.render(road, x, y, gfx);
	}
}


