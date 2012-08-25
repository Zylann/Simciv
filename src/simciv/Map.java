package simciv;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import simciv.builds.Build;
import simciv.builds.Warehouse;
import simciv.effects.VisualEffect;
import simciv.rendering.SortedRender;
import simciv.units.Unit;

/**
 * The map contains the terrain and the city (units and buildings).
 * All the data concerning a player's game is stored here.
 * Note : units don't all belong to the city (ducks, nomads...)
 * @author Marc
 *
 */
public class Map
{
	public MapGrid grid;
	public PlayerCity playerCity;
	public WorldTime time;
	private EntityMap builds;
	private EntityMap units;
	private transient List<VisualEffect> graphicalEffects;
	private transient boolean fastForward;

	public Map(int width, int height)
	{
		grid = new MapGrid(width, height);
		playerCity = new PlayerCity();
		time = new WorldTime();
		builds = new EntityMap();
		units = new EntityMap();
		graphicalEffects = new ArrayList<VisualEffect>();
	}

	public boolean isFastForward()
	{
		return fastForward;
	}

	public void setFastForward(boolean e)
	{
		fastForward = e;
	}

	/**
	 * Updates the world, and calls the tick() method on buildings
	 * and units at each time interval (tickTime).
	 * @param delta
	 */
	public void update(GameContainer gc, StateBasedGame game, int delta)
	{
		if(fastForward)
			delta *= 4;
		
		time.update(delta);
		
		units.updateAll(gc, game, delta);
		builds.updateAll(gc, game, delta);
		
		// Update graphical effects
		ArrayList<VisualEffect> finishedGraphicalEffects = new ArrayList<VisualEffect>();
		for(VisualEffect e : graphicalEffects)
		{
			e.update(delta);
			if(e.isFinished())
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
	 * It will be active at next world update, because it allows to spawn units
	 * while iterating on the units map.
	 * @param u : unit
	 * @param x : x coordinate in map cells
	 * @param y : y coordinate in map cells
	 */
	public void spawnUnit(Unit u, int x, int y)
	{
		u.setPosition(x, y);
		units.add(u);
	}
	
	public void spawnUnit(Unit unit)
	{
		spawnUnit(unit, unit.getX(), unit.getY());
	}
	
	/**
	 * Places a new building at (x,y) if possible.
	 * It may occupy one or more cells on the map.
	 * @param b : new building
	 * @param x : x origin
	 * @param y : y origin
	 * @return : true if the building has been placed
	 */
	public boolean placeBuild(Build b, int x, int y)
	{
		b.setPosition(x, y);
		if(b.canBePlaced(grid, x, y))
		{
			builds.add(b);
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
	public boolean eraseBuild(int x, int y)
	{
		int ID = grid.getBuildID(x, y);
		if(ID >= 0)
		{
			Build b = getBuild(ID);
			if(b != null)
			{
				b.dispose();
//				map.markBuilding(b, false);
//				b.onDestruction();
//				buildings.remove(ID);
				return true;
			}
		}
		return false;
	}
	
	public Unit getUnit(int ID)
	{
		return (Unit)(units.get(ID));
	}
	
	public Build getBuild(int ID)
	{
		return (Build)(builds.get(ID));
	}
	
	/**
	 * Get the building occupying the cell at (x, y).
	 * Returns null if there is no building.
	 * @param worldRef
	 * @param x
	 * @param y
	 * @return
	 */
	public Build getBuild(int x, int y)
	{
		if(!grid.contains(x, y))
			return null;
		return getBuild(grid.getCellExisting(x, y).getBuildID());
	}
	
	public Unit getUnit(int x, int y)
	{
		if(!grid.contains(x, y))
			return null;
		return getUnit(grid.getCellExisting(x, y).getUnitID());
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
		
		grid.registerElementsForSortedRender(mapRange, renderMgr);

		if(!gc.getInput().isKeyDown(Input.KEY_1))
		{
			// Register buildings
			for(Entity b : builds.asCollection())
			{
				if(mapRange.intersects(
						b.getX(),
						b.getY(),
						b.getX() + b.getWidth() - 1,
						b.getY() + b.getHeight() - 1))
					renderMgr.add(b);
			}
		}

		if(!gc.getInput().isKeyDown(Input.KEY_2))
		{
			// Register units
			for(Entity u : units.asCollection())
			{
				if(u.isVisible() && mapRange.contains(u.getX(), u.getY()))
					renderMgr.add(u);
			}
		}
		
//		long timeBefore = System.currentTimeMillis(); // for debug
		
		// Draw elements in the right order
		grid.renderGround(mapRange, gc, gfx); // Ground at first
		
		renderMgr.render(gc, game, gfx);
		
		// Draw effects on the top
		for(VisualEffect e : graphicalEffects)
			e.render(gfx);
		
//		long time = System.currentTimeMillis() - timeBefore; // for debug
//		if(gc.getInput().isKeyDown(Input.KEY_T))
//			System.out.println(time);
	}

	public Warehouse getFreeWarehouse(int x, int y)
	{
		List<Build> list = getBuildsAround(x, y);
		for(Build b : list)
		{
			if(Warehouse.class.isInstance(b))
			{
				if(b.isAcceptResources())
					return (Warehouse) b;
			}
		}
		return null;
	}
	
	/**
	 * Get a list of buildings around the given position
	 * @param worldRef
	 * @param x
	 * @param y
	 * @return list of buildings
	 */
	public ArrayList<Build> getBuildsAround(int x, int y)
	{
		Build b;
		ArrayList<Build> list = new ArrayList<Build>();
		
		b = getBuild(x-1, y);
		if(b != null)
			list.add(b);
		b = getBuild(x+1, y);
		if(b != null)
			list.add(b);
		b = getBuild(x, y-1);
		if(b != null)
			list.add(b);
		b = getBuild(x, y+1);
		if(b != null)
			list.add(b);
		
		return list;
	}
	
	public ArrayList<Build> getBuildsAround(int x0, int y0, int w, int h)
	{
		Build b;
		ArrayList<Build> list = new ArrayList<Build>();
		int x, y;
		
		for(x = x0-1; x <= x0 + w; x++)
		{
			// Top
			y = y0 - 1;
			b = getBuild(x, y);
			if(b != null)
				list.add(b);
			
			// Bottom
			y = y0 + h;
			b = getBuild(x, y);
			if(b != null)
				list.add(b);
		}

		for(y = y0; y < y0 + h; y++)
		{
			// Left
			x = x0 - 1;
			b = getBuild(x, y);
			if(b != null)
				list.add(b);

			// Right
			x = x0 + w;
			b = getBuild(x, y);
			if(b != null)
				list.add(b);
		}
		
		return list;
	}
	
}




