package simciv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.buildings.Building;
import simciv.rendering.SortedRender;
import simciv.units.Unit;

/**
 * The world contains the terrain and the city (units and buildings)
 * @author Marc
 *
 */
public class World
{
	public static boolean renderFancyUnitsMovements = true;
	
	public Map map;	
	private HashMap<Integer,Unit> units = new HashMap<Integer,Unit>();
	private HashMap<Integer,Building> buildings = new HashMap<Integer,Building>();
	private List<VisualEffect> graphicalEffects = new ArrayList<VisualEffect>();
	private int time; // World time in milliseconds

	public World(int width, int height)
	{
		map = new Map(width, height);
	}
		
	/**
	 * Updates the world, and calls the tick() method on buildings
	 * and units at each time interval (tickTime).
	 * @param delta
	 */
	public void update(GameContainer gc, StateBasedGame game, int delta)
	{
		time += delta;
		
		ArrayList<Unit> unitsToRemove = new ArrayList<Unit>();
		for(Unit u : units.values())
		{
			u.update(gc, game, delta);
			if(!u.isAlive())
			{
				unitsToRemove.add(u);
			}
		}
		for(Building b : buildings.values())
		{
			b.update(gc, game, delta);
		}
		// TODO buildings to remove
		for(Unit u : unitsToRemove)
		{
			removeUnit(u.getID());
		}
		
		// Update graphical effects
		ArrayList<VisualEffect> finishedGraphicalEffects = new ArrayList<VisualEffect>();
		for(VisualEffect e : graphicalEffects)
		{
			e.update(delta);
			if(e.finished)
				finishedGraphicalEffects.add(e);
		}
		graphicalEffects.removeAll(finishedGraphicalEffects);
	}
	
	public void addGraphicalEffect(VisualEffect e)
	{
		graphicalEffects.add(e);
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
			u.onInit();
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
		if(b.canBePlaced(map, x, y))
		{
			if(map.markBuilding(b, true) && !buildings.containsKey(b.getID()))
			{
				buildings.put(b.getID(), b);
				return true;
			}
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
	
	public Unit getUnit(int ID)
	{
		return units.get(ID);
	}
	
	public Building getBuilding(int ID)
	{
		return buildings.get(ID);
	}
	
	/**
	 * Get the building occupying the cell at (x, y), 
	 * returns null if there is no building.
	 * @param x
	 * @param y
	 * @return
	 */
	public Building getBuilding(int x, int y)
	{
		return map.getBuilding(this, x, y);
	}
	
	/**
	 * Draws a part of the world within the specified map range
	 * @param mapRange
	 * @param gc
	 * @param gfx
	 */
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx, IntRange2D mapRange)
	{		
		SortedRender renderMgr = new SortedRender();		
				
		map.registerElementsForSortedRender(mapRange, renderMgr);

		// Register buildings
		for(Building b : buildings.values())
		{
			if(mapRange.intersects(
					b.getX(),
					b.getY(),
					b.getX() + b.getWidth() - 1,
					b.getY() + b.getHeight() - 1))
				renderMgr.add(b);
		}

		// Register units
		for(Unit u : units.values())
		{
			if(u.isVisible() && mapRange.contains(u.getX(), u.getY()))
				renderMgr.add(u);
		}
		
//		long timeBefore = System.currentTimeMillis(); // for debug
		
		// Draw elements in the right order
		map.renderGround(mapRange, gc, gfx); // Ground at first
		renderMgr.render(gc, game, gfx);
		
		// Draw effects on the top
		for(VisualEffect e : graphicalEffects)
			e.render(gfx);
		
//		long time = System.currentTimeMillis() - timeBefore; // for debug
//		if(gc.getInput().isKeyDown(Input.KEY_T))
//			System.out.println(time);
	}
	
}

