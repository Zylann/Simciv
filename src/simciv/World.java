package simciv;

import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import simciv.buildings.Building;
import simciv.units.Unit;

/**
 * The world contains the terrain, the city and every game element (except UI)
 * @author Marc
 *
 */
public class World
{	
	public Map map;	
	int time;
	private HashMap<Integer,Unit> units = new HashMap<Integer,Unit>();
	private HashMap<Integer,Building> buildings = new HashMap<Integer,Building>();

	public World(int width, int height)
	{
		map = new Map(width, height);
		
		//spawnUnit(new Citizen(this), 20, 10);
		//spawnUnit(new Nomad(this), 10, 20);
		//placeBuilding(BuildingList.createBuildingFromName("House", this), 10, 10);
	}
		
	/**
	 * Updates the world, and calls the tick() method on buildings
	 * and units at each time interval (tickTime).
	 * @param delta
	 */
	public void update(int delta)
	{
		time += delta;

		ArrayList<Unit> unitsToRemove = new ArrayList<Unit>();
		
		for(Unit u : units.values())
		{
			u.update(delta);
			if(!u.isAlive())
			{
				unitsToRemove.add(u);
			}
		}
		for(Building b : buildings.values())
		{
			b.update(delta);
		}
		// TODO buildings to remove
		for(Unit u : unitsToRemove)
		{
			u.onDestruction();
			removeUnit(u.getID());
		}
	}
	
	/**
	 * Spawns an unit in the world at (x,y).
	 * @param u : unit
	 * @param x
	 * @param y
	 * @return : true if the unit has been spawned.
	 */
	public boolean spawnUnit(Unit u, int x, int y)
	{
		u.setPosition(x, y);
		if(!units.containsKey(u.getID()))
		{
			units.put(u.getID(), u);
			return true;
		}
		return false;
	}
	
	/**
	 * Spawns a new unit to the world
	 * @param unit
	 */
	public void spawnUnit(Unit unit)
	{
		spawnUnit(unit, unit.getX(), unit.getY());
	}
	
	/**
	 * Removes and destroys an unit in the world
	 * @param ID : unit ID
	 * @return true if success
	 */
	public boolean removeUnit(int ID)
	{
		if(ID >= 0)
		{
			Unit u = units.remove(ID);
			if(u != null)
			{
				u.onDestruction();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Places a new building at (x,y).
	 * It may occupy one or more cells on the map.
	 * @param b : new building
	 * @param x : x origin
	 * @param y : y origin
	 * @return : true if the building has been placed
	 */
	public boolean placeBuilding(Building b, int x, int y)
	{
		b.setPosition(x, y);
		if(map.markBuilding(b, true) && !buildings.containsKey(b.getID()))
		{
			buildings.put(b.getID(), b);
			return true;
		}
		return false;
	}
	
	/**
	 * Erases the building assumed to occupy the cell at (x,y).
	 * @param x
	 * @param y
	 * @return : true if the building has been erased.
	 */
	public boolean eraseBuilding(int x, int y)
	{
		int ID = map.getBuildingID(x, y);
		if(ID >= 0)
		{
			Building b = getBuilding(ID);
			if(b != null)
			{
				map.markBuilding(b, false);
				b.onDestruction();
				buildings.remove(ID);
				return true;
			}
		}
		return false;
	}
	
	Unit getUnit(int ID)
	{
		return units.get(ID);
	}
	
	Building getBuilding(int ID)
	{
		return buildings.get(ID);
	}
	
	/**
	 * Draws a part of the world within the specified map range
	 * @param mapRange
	 * @param gc
	 * @param gfx
	 */
	public void render(IntRange2D mapRange, GameContainer gc, Graphics gfx)
	{
		map.render(mapRange, gc, gfx);
		
		for(Building b : buildings.values())
		{
			if(mapRange.contains(b.getX(), b.getY()))
				b.render(gfx);
		}
		for(Unit u : units.values())
		{
			if(u.isOut() && mapRange.contains(u.getX(), u.getY()))
			{
				gfx.pushTransform();

				// Fancy movements
				if(u.getDirection() != Direction2D.NONE)
				{
					float k = -Game.tilesSize * u.getK();
					Vector2i dir = Direction2D.vectors[u.getDirection()];
					gfx.translate(k * dir.x, k * dir.y);
				}
				
				u.render(gfx);
				
				gfx.popTransform();
			}
		}
	}
}

