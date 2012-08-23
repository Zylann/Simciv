package simciv;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import simciv.builds.Build;
import simciv.maptargets.IMapTarget;
import simciv.rendering.RenderNatureElements;
import simciv.rendering.SortedRender;

/**
 * The map is a 2D array that stores terrain, plants and roads information.
 * It is also used to mark places occupied by buildings (see MapCell) and other useful data for game optimization.
 * @author Marc
 *
 */
public class Map
{
	private MapCell cells[]; // 2D access is made using (width * y + x).
	private int width;
	private int height;	
	private boolean renderGrid;
	private List<IMapListener> listeners;

	/**
	 * Creates an empty map
	 * @param width : X dimension 
	 * @param height : Y dimension
	 */
	public Map(int width, int height)
	{
		renderGrid = false;

		this.width = width > 0 ? width : 1;
		this.height = height > 0 ? height : 1;
		int area = getArea();
		
		cells = new MapCell[area];
		for(int i = 0; i < area; i++)
			cells[i] = new MapCell();
		
		listeners = new ArrayList<IMapListener>();
	}
	
	public void addListener(IMapListener l)
	{
		listeners.add(l);
	}
	
	/**
	 * Notifies map listeners for a change at (x,y).
	 * @param x : cell position Y
	 * @param y : cell position X
	 */
	public void onChange(int x, int y)
	{
		if(!contains(x, y))
			return;
		onChange(x, y, getCellExisting(x, y));
	}
	
	/**
	 * Notifies map listeners for a change in a rectangular area.
	 * @param x0 : cells area origin X
	 * @param y0 : cells area origin Y
	 * @param w : area width
	 * @param h : area height
	 */
	public void onChange(int x0, int y0, int w, int h)
	{
		if(!contains(x0, y0, w, h))
			return;
		for(int y = y0; y < y0 + h; y++)
		{
			for(int x = x0; x < x0 + w; x++)
				onChange(x, y, getCellExisting(x, y));
		}
	}

	/**
	 * Notifies map listeners for a change in (x,y).
	 * This position must be valid, and the specified cell must be corresponding one.
	 * @param x
	 * @param y
	 * @param cell
	 */
	private void onChange(int x, int y, MapCell cell)
	{
		for(IMapListener l : listeners)
			l.onCellChange(cell, x, y);
	}

	/**
	 * Get the area of the map. It is also the length of data buffers.
	 * @return area (length)
	 */
	public int getArea()
	{
		return width * height;
	}
	
	/**
	 * Returns the cell at position (x,y). This position MUST be valid.
	 * Warning : listeners will not be notified if the returned cell is modified.
	 * (Manually call onChange(x, y) or use another method to make it done)
	 * @param x
	 * @param y
	 * @return
	 */
	public MapCell getCellExisting(int x, int y)
	{
		return cells[width * y + x];
	}
			
	// Terrain
	
	/**
	 * Get terrain's properties at (x,y)
	 * @param x
	 * @param y
	 * @return terrain properties
	 */
	public Terrain getTerrain(int x, int y)
	{
		if(contains(x, y))
			return Terrain.get(getCellExisting(x,y).terrainID);
		else
			return Terrain.get(Terrain.VOID);
	}
	
	/**
	 * Sets the terrain type at (x,y) and notifies listeners.
	 * @param x
	 * @param y
	 * @param t : terrain type (ID)
	 * @param nature : natural element
	 */
	public void setTerrain(int x, int y, byte t, byte nature)
	{
		if(contains(x, y))
		{
			MapCell c = getCellExisting(x, y);
			c.terrainID = t;
			c.nature = nature;
			onChange(x, y, c);
		}
	}
	
	/**
	 * Fills all cells with a terrain type.
	 * (Listeners are not notified)
	 * @param value : terrain type
	 */
	public void fillTerrain(byte value)
	{
		int size = getArea();
		for(int i = 0; i < size; i++)
			cells[i].terrainID = value;
	}
	
	// Roads
	
	public boolean isRoad(int x, int y)
	{
		if(contains(x, y))
			return getCellExisting(x, y).isRoad();
		return false;
	}
	
	/**
	 * Sets the road index at (x,y).
	 * Listeners are notified.
	 * @param x
	 * @param y
	 * @param i : index (using Road.getIndex(map,x,y))
	 */
	private void setRoad(int x, int y, byte i)
	{
		if(contains(x, y))
		{
			MapCell c = getCellExisting(x,y);
			c.road = i;
			onChange(x, y, c);
		}
	}
	
	/**
	 * Places a road at (x,y).
	 * Listeners are notified.
	 * @param x
	 * @param y
	 * @return true if the road has been successfully placed.
	 */
	public boolean placeRoad(int x, int y)
	{
		if(canPlaceObject(x, y))
		{
			MapCell c = getCellExisting(x,y);
			c.road = Road.getIndex(this, x, y);
			updateRoads(x, y);
			onChange(x, y, c);
			return true;
		}
		return false;
	}
	
	/**
	 * Erases roads at (x,y).
	 * @param x
	 * @param y
	 * @return : true if a road has been erased.
	 */
	public boolean eraseRoad(int x, int y)
	{
		if(isRoad(x, y))
		{
			MapCell c = getCellExisting(x,y);
			c.road = -1;
			updateRoads(x, y);
			onChange(x, y, c);
			return true;
		}
		return false;
	}
	
	/**
	 * Updates roads around the point (x,y).
	 * Must be done after placing a road here, in order to
	 * make the roads join if possible.
	 * @param x
	 * @param y
	 */
	private void updateRoads(int x, int y)
	{
		if(isRoad(x-1, y))
			setRoad(x-1, y, Road.getIndex(this, x-1, y));
		if(isRoad(x+1, y))
			setRoad(x+1, y, Road.getIndex(this, x+1, y));
		if(isRoad(x, y-1))
			setRoad(x, y-1, Road.getIndex(this, x, y-1));
		if(isRoad(x, y+1))
			setRoad(x, y+1, Road.getIndex(this, x, y+1));
	}
	
	// Buildings
	
	/**
	 * Marks cells as occupied by a building, or clears marked cells.
	 * Listeners are notified.
	 * @param b : building to mark
	 * @param mark : true to mark, false to clear marks.
	 */
	public void markBuilding(Build b, boolean mark)
	{
//		// Check if we can place a mark
//		if(mark && !canPlaceObject(b.getX(), b.getY(), b.getWidth(), b.getHeight()))
//			return false;
		
		int x, y;
		int xmax = b.getX() + b.getWidth() - 1;
		int ymax = b.getY() + b.getHeight() - 1;
		boolean isOrigin = true;
		
		for(y = b.getY(); y <= ymax; y++)
		{
			for(x = b.getX(); x <= xmax; x++)
			{
				if(mark)
				{
					getCellExisting(x, y).setBuildInfo(b.getID(), isOrigin);
					if(isOrigin)
						isOrigin = false;
				}
				else
					getCellExisting(x, y).eraseBuildInfo();
			}
		}
		
		onChange(b.getX(), b.getY(), b.getWidth(), b.getHeight());
	}
	
	/**
	 * Get the building ID at (x,y).
	 * @param x
	 * @param y
	 * @return building ID, -1 if there are no building here.
	 */
	public int getBuildID(int x, int y)
	{
		if(contains(x, y))
			return getCellExisting(x,y).getBuildID();
		return -1;
	}
	
	/**
	 * Tests if a building with the given ID is around the given position
	 * @param ID : building ID
	 * @param x
	 * @param y
	 * @return true if the building is detected, false if not
	 */
	public boolean isBuildAroundWithID(int ID, int x, int y)
	{
		if(getBuildID(x-1, y) == ID)
			return true;
		if(getBuildID(x+1, y) == ID)
			return true;
		if(getBuildID(x, y-1) == ID)
			return true;
		if(getBuildID(x, y+1) == ID)
			return true;
		return false;
	}

	// Rendering
	
	/**
	 * Renders the map ground within the specified range.
	 * @param range : map range (in cells)
	 * @param gc
	 * @param gfx
	 */
	public void renderGround(IntRange2D range, GameContainer gc, Graphics gfx)
	{
		gfx.setColor(Color.white);
		int x, y;
		
		for(y = range.minY(); y <= range.maxY(); y++)
		{
			for(x = range.minX(); x <= range.maxX(); x++)
			{
				if(contains(x, y))
				{
					getCellExisting(x, y).renderGround(x, y, gfx);
//					getCellExisting(x, y).renderData(gfx, x, y); // debug
				}
			}
		}
		
		if(renderGrid)
			renderGrid(range, gc, gfx);
	}
	
	public void registerElementsForSortedRender(IntRange2D range, SortedRender mgr)
	{
		for(int y = range.minY(); y <= range.maxY(); y++)
		{
			mgr.add(new RenderNatureElements(this, range.minX(), y, range.maxX()));
		}
	}
	
	/**
	 * Draws a grid clearly showing cells frontiers, for debug use.
	 * @param range : which range should the grid be rendered
	 * @param gc
	 * @param gfx
	 */
	private void renderGrid(IntRange2D range, GameContainer gc, Graphics gfx)
	{
		gfx.pushTransform();
		
		gfx.setColor(Color.black);
		gfx.setLineWidth(1);
		gfx.scale(Game.tilesSize, Game.tilesSize);
		
		for(int x = range.minX(); x <= range.maxX(); x++)
			gfx.drawLine(x, range.minY(), x, range.maxY());
		
		for(int y = range.minY(); y <= range.maxY(); y++)
			gfx.drawLine(range.minX(), y, range.maxX(), y);
		
		gfx.popTransform();
	}
	
	public void toggleRenderGrid()
	{
		renderGrid = !renderGrid;
	}
	
	// Miscellaneous / tests

	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Test if (x,y) is a right position
	 * @param x
	 * @param y
	 * @return : true if the point (x,y) is right
	 */
	public boolean contains(int x, int y)
	{
		return x >= 0 && y >= 0 && x < width && y < height;
	}
	
	/**
	 * Tests if the rectangle defined by the origin (x,y) and size (w,h) is contained in the map.
	 * @param x : rectangle origin X in cells
	 * @param y : rectangle origin Y in cells
	 * @param w : rectangle width
	 * @param h : rectangle height
	 * @return true if the rectangle is contained, false if not
	 */
	public boolean contains(int x, int y, int w, int h)
	{
		return x >= 0 && y >= 0 && x + w <= width && y + h <= height;
	}

	/**
	 * Tests if we can place an object at (x,y).
	 * Here, an object can be a road, a plant or a building.
	 * @param x
	 * @param y
	 * @return : true if we can place an object
	 */
	public boolean canPlaceObject(int x, int y)
	{
		if(!contains(x, y)) // invalid position
			return false;
		return getCellExisting(x, y).canPlaceObject();
	}
	
	/**
	 * Tests if we can place an object in the (xmin,ymin,w,h) area.
	 * @param xmin : X origin
	 * @param ymin : Y origin
	 * @param w : area width
	 * @param h : area height
	 * @return true if all cells in the area are free
	 */
	public boolean canPlaceObject(int xmin, int ymin, int w, int h)
	{
		int x, y, xmax = xmin + w - 1, ymax = ymin + h - 1;
		for(y = ymin; y <= ymax; y++)
		{
			for(x = xmin; x <= xmax; x++)
			{
				if(!canPlaceObject(x, y))
					return false;
			}
		}		
		return true;
	}
	
	public boolean isCrossable(int x, int y)
	{
		if(!contains(x, y))
			return false;
		else
			return getCellExisting(x, y).isCrossable();
	}
	
	/**
	 * Returns available directions complying with the default test
	 * @param x
	 * @param y
	 * @return
	 */
	public ArrayList<Byte> getAvailableDirections(int x, int y)
	{
		ArrayList<Byte> res = new ArrayList<Byte>();
		
		if(isCrossable(x-1, y))
			res.add(Direction2D.WEST);
		if(isCrossable(x+1, y))
			res.add(Direction2D.EAST);
		if(isCrossable(x, y-1))
			res.add(Direction2D.NORTH);
		if(isCrossable(x, y+1))
			res.add(Direction2D.SOUTH);
		
		return res;
	}
	
	/**
	 * Returns positions complying with the given target around a rectangular zone on the map.
	 * @param x0 : rectangle origin X
	 * @param y0 : rectangle origin Y
	 * @param w : rectangle width ( > 0)
	 * @param h : rectangle height ( > 0)
	 * @param target : target to comply
	 * @param world : world reference if needed by the target (Set to null if not)
	 * @return list of positions
	 */
	public ArrayList<Vector2i> getAvailablePositionsAround(
			int x0, int y0, int w, int h, IMapTarget target, World world)
	{
		ArrayList<Vector2i> p = new ArrayList<Vector2i>();
		int x, y;
		
		//	  X X X  
		//	Y       Y
		//	Y       Y
		//	  X X X 
		
		for(x = x0; x < x0 + w; x++) // X
		{
			// Top
			y = y0 - 1;
			if(contains(x, y) && target.evaluate(world, x, y))
				p.add(new Vector2i(x, y));			
			// Bottom
			y = y0 + h;
			if(contains(x, y) && target.evaluate(world, x, y))
				p.add(new Vector2i(x, y));
		}

		for(y = y0; y < y0 + h; y++) // Y
		{
			// Left
			x = x0 - 1;
			if(contains(x, y) && target.evaluate(world, x, y))
				p.add(new Vector2i(x, y));
			// Right
			x = x0 + w;
			if(contains(x, y) && target.evaluate(world, x, y))
				p.add(new Vector2i(x, y));
		}
		
		return p;
	}
	
	public ArrayList<Vector2i> getAvailablePositionsAround(Build b, IMapTarget target, World world)
	{
		return getAvailablePositionsAround(b.getX(), b.getY(), b.getWidth(), b.getHeight(), target, world);
	}
	
	/**
	 * Evaluates if an area is arable or not.
	 * @param x0 : area origin X in cells
	 * @param y0 : area origin Y in cells
	 * @param w : area width
	 * @param h : area height
	 * @return : true if the area is arable, false if not.
	 */
	public boolean isArable(int x0, int y0, int w, int h)
	{
		if(!contains(x0, y0, w, h))
			return false;
		for(int y = 0; y < h; y++)
		{
			for(int x = 0; x < w; x++)
			{
				if(!getCellExisting(x0 + x, y0 + y).isArable())
					return false;
			}
		}
		return true;
	}

}

