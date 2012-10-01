package simciv;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import backend.GameComponent;
import backend.GameComponentMap;
import backend.IntRange2D;
import backend.pathfinding.MultiSeedPathFinder;
import backend.ui.INotificationListener;
import backend.ui.Notification;

import simciv.builds.Build;
import simciv.content.Content;
import simciv.effects.VisualEffect;
import simciv.rendering.SortedRender;
import simciv.units.Unit;

/**
 * The map contains a terrain and a city (units and buildings).
 * This is the main object of the game, as almost all the data is stored in it.
 * @author Marc
 *
 */
public class Map
{
	/** Factor used when the time speed-up option is enabled **/ 
	private static final int FAST_FORWARD_SPEED_FACTOR = 4;
	
	/** Terrain of the map **/
	public MapGrid grid;
	
	/** View clip */
	public ScrollView view;
	
	/** Information about player's city **/
	public PlayerCity playerCity;
	
	/** Date **/
	public WorldTime time;
	
	/** All builds on the map. ONLY Builds may be stored in it. **/
	private EntityMap builds;
	
	/** All units on the map. ONLY Units may be stored in it. **/
	private EntityMap units;
	
	/** All visual effects on the map **/
	private transient GameComponentMap graphicalEffects;
	
	/** If true, the map will speed-up its updates **/
	private transient boolean fastForward;
	
	/** Notifications **/
	private transient INotificationListener notifListener;
	
	/** Multi-seed best-path finder **/
	public transient MultiSeedPathFinder multiPathFinder;
	
	/**
	 * Constructs an empty map from given size
	 * @param width : map size X in cells
	 * @param height : map size Y in cells
	 */
	public Map(int width, int height)
	{
		grid = new MapGrid(width, height);
		playerCity = new PlayerCity();
		time = new WorldTime();
		builds = new EntityMap();
		units = new EntityMap();
		graphicalEffects = new GameComponentMap();
		view = new ScrollView(0, 0, 2);
		view.setMapSize(width, height);
		multiPathFinder = new MultiSeedPathFinder(grid.getWidth(), grid.getHeight());
	}
	
	/**
	 * Sets the notifications listener that will be used
	 * to send notifications to the player
	 * @param notifListener
	 */
	public void setNotificationListener(INotificationListener notifListener)
	{
		this.notifListener = notifListener;
	}
	
	/**
	 * Sends a notification message to the player using the notifications listener
	 * @param type : Notification type
	 * @param message : readable message
	 */
	public void sendNotification(byte type, String message)
	{
		if(notifListener != null)
		{
			notifListener.notify(type, message);
			playNotificationSound(type);
		}
	}
	
	/**
	 * Sends a notification message to the player using the notifications listener
	 * @param type : Notification type
	 * @param message : readable message
	 * @param timeVisible : visibility duration in milliseconds
	 */
	public void sendNotification(byte type, String message, int timeVisible)
	{
		if(notifListener != null)
		{
			notifListener.notify(type, message, timeVisible);
			playNotificationSound(type);
		}
	}
	
	/**
	 * Plays a sound corresponding to one type of notification send by the map.
	 * @param type : Notification type
	 */
	private void playNotificationSound(byte type)
	{
		switch(type)
		{
		case Notification.TYPE_INFO : Content.sounds.uiNotificationInfo.play(); break;
		case Notification.TYPE_WARNING : Content.sounds.uiNotificationWarning.play(); break;
		default : break;
		}
	}

	/**
	 * Tests if fast forward is enabled.
	 * @return fastForward flag
	 */
	public boolean isFastForward()
	{
		return fastForward;
	}

	/**
	 * Sets fast forward. If true, the map will speed-up its updates.
	 * @param e
	 */
	public void setFastForward(boolean e)
	{
		fastForward = e;
	}
	
	/**
	 * Updates the whole map for the given time duration
	 * @param delta : update time in milliseconds.
	 */
	public void update(GameContainer gc, StateBasedGame game, int delta)
	{
		// Update view (not affected by fast forward)
		view.update(gc, delta / 1000.f);
		
		if(fastForward)
			delta *= FAST_FORWARD_SPEED_FACTOR;
		
		// Update world time
		time.update(delta, this);
		
		// Update city global management
		playerCity.update(this, delta);

		// Update units
		units.updateAll(gc, game, delta);
		
		// Update builds
		builds.updateAll(gc, game, delta);
		
		// Update effects
		graphicalEffects.updateAll(gc, game, delta);		
	}
	
	/**
	 * Adds a graphical effect.
	 * Note : do not call this method from a VisualEffect's method
	 * (concurrent modification is not supported).
	 * @param e : effect
	 */
	public void addGraphicalEffect(VisualEffect e)
	{
		graphicalEffects.stageComponent(e);
	}
	
	/**
	 * Spawns an unit on the map at (x,y).
	 * It will be active on next update.
	 * @param u : new unit
	 * @param x : x coordinate in map cells
	 * @param y : y coordinate in map cells
	 */
	public void spawnUnit(Unit u, int x, int y)
	{
		u.setPosition(x, y);
		units.stageEntity(u);
	}
	
	/**
	 * Spawns an unit on the map at (x,y).
	 * It will be active on next update.
	 * @param u : new unit
	 */
	public void spawnUnit(Unit u)
	{
		units.stageEntity(u);
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
			builds.stageEntity(b);
			return true;
		}
		return false;
	}

	/**
	 * Finds and returns the unit having the given ID.
	 * @param ID : ID of the unit
	 * @return the unit having the ID, null if not found
	 */
	public Unit getUnit(int ID)
	{
		if(ID <= 0)
			return null;
		return (Unit)(units.get(ID));
	}
	
	/**
	 * Finds and returns the build having the given ID.
	 * @param ID : ID of the unit
	 * @return the build having the ID, null if not found
	 */
	public Build getBuild(int ID)
	{
		if(ID <= 0)
			return null;
		return (Build)(builds.get(ID));
	}
	
	/**
	 * Gets the building occupying the cell at (x, y).
	 * Returns null if there is no building.
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
	
	/**
	 * Gets one of the units occupying the cell at (x, y).
	 * Returns null if there is no unit.
	 * @param x
	 * @param y
	 * @return
	 */
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
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		view.configureGraphicsForWorldRendering(gfx);
		
		IntRange2D mapRange = new IntRange2D();
		IntRange2D cmpRange = new IntRange2D();
		
		view.getMapBounds(mapRange);
		
		SortedRender renderMgr = new SortedRender();
		
		grid.registerElementsForSortedRender(mapRange, renderMgr);

		if(!gc.getInput().isKeyDown(Input.KEY_1))
		{
			// Register buildings
			for(GameComponent b : builds.asCollection())
			{
				if(b.isVisible())
				{
					b.getRenderBounds(cmpRange);
					if(mapRange.intersects(cmpRange))
						renderMgr.add(b);
				}
			}
		}

		if(!gc.getInput().isKeyDown(Input.KEY_2))
		{
			// Register units
			for(GameComponent u : units.asCollection())
			{
				if(u.isVisible())
				{
					u.getRenderBounds(cmpRange);
					if(mapRange.intersects(cmpRange))
						renderMgr.add(u);
				}
			}
		}
		
//		long timeBefore = System.currentTimeMillis(); // for debug
		
		// Draw elements in the right order
		
		// Ground at first
		grid.renderGround(mapRange, gc, gfx); 
		
		// Objects, builds, units
		renderMgr.render(gc, game, gfx); 
		
		// Draw effects on the top
		Collection<GameComponent> effects = graphicalEffects.asCollection();
		for(GameComponent e : effects)
			e.render(gc, game, gfx);
		
//		long time = System.currentTimeMillis() - timeBefore; // for debug
//		if(gc.getInput().isKeyDown(Input.KEY_T))
//			System.out.println(time);
		
		if(gc.getInput().isKeyDown(Input.KEY_NUMPAD1))
		{
			// Draws the state of the path finder
			gfx.pushTransform();
			gfx.scale(Game.tilesSize, Game.tilesSize);
			gfx.setColor(new Color(0, 0, 0, 128));
			gfx.setLineWidth(3.f);
			multiPathFinder.renderMatrix(gfx);
			gfx.popTransform();
		}		
	}
	
	/**
	 * Get a list of buildings around the given position
	 * @param x
	 * @param y
	 * @return list of buildings
	 */
	public ArrayList<Build> getBuildsAround(int x, int y)
	{
		/*
		 * Neighborhood :
		 * - O -
		 * O x O
		 * - O -
		 */
		
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
	
	/**
	 * Get a list of buildings around the given rect.
	 * @param x0
	 * @param y0
	 * @param w
	 * @param h
	 * @return list of buildings
	 */
	public ArrayList<Build> getBuildsAround(int x0, int y0, int w, int h)
	{
		/*
		 * Neighborhood :
		 * O O O O O
		 * O x x x O
		 * O x x x O
		 * O x x x O
		 * O O O O O
		 */
		
		Build b;
		ArrayList<Build> list = new ArrayList<Build>();
		int x, y;
		
		for(x = x0-1; x <= x0 + w; x++)
		{
			// Top
			y = y0 - 1;
			b = getBuild(x, y);
			if(b != null && !list.contains(b))
				list.add(b);
			
			// Bottom
			y = y0 + h;
			b = getBuild(x, y);
			if(b != null && !list.contains(b))
				list.add(b);
		}

		for(y = y0; y <= y0 + h; y++)
		{
			// Left
			x = x0 - 1;
			b = getBuild(x, y);
			if(b != null && !list.contains(b))
				list.add(b);

			// Right
			x = x0 + w;
			b = getBuild(x, y);
			if(b != null && !list.contains(b))
				list.add(b);
		}
				
		return list;
	}
	
	/**
	 * Returns true if there is a fire burning at the
	 * specified cell position.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isFire(int x, int y)
	{
		Build b = getBuild(x, y);
		if(b != null)
			return b.isFireBurning();
		return false;
	}
	
	/**
	 * Returns true if the given cell position is walkable.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isWalkable(int x, int y)
	{
		if(!grid.isWalkable(x, y))
		{
			Build b = getBuild(x, y);
			if(b != null)
				return b.isWalkable();
			return false;
		}
		return true;
	}
	
	// Saving
	
	/**
	 * Saves the map in an output stream
	 */
	public void writeToSave(DataOutputStream dos) throws IOException
	{
		ObjectOutputStream oos = new ObjectOutputStream(dos);
		
		// Save grid
		Log.info("Saving terrain...");
		oos.writeObject(grid);
		
		// Save view
		Log.info("Saving view info...");
		oos.writeObject(view);
		
		// Save builds
		Log.info("Saving builds...");
		oos.writeObject(builds);
		
		// Save units
		Log.info("Saving units...");
		oos.writeObject(units);
		
		// Save city
		Log.info("Saving city info...");
		oos.writeObject(playerCity);
		
		// Save world time
		Log.info("Saving time info...");
		oos.writeObject(time);
		
		oos.flush();
	}
	
	/**
	 * Loads map's content by reading an input stream,
	 * recomputes transient data and performs an integrity check.
	 * @param dis : data input stream
	 * @throws IOException if the stream is not readable
	 * @throws ClassNotFoundException if the stream is corrupted
	 * @throws SlickException if the map is corrupted
	 */
	public void readFromSave(DataInputStream dis) 
			throws IOException, ClassNotFoundException, SlickException
	{
		ObjectInputStream ois = new ObjectInputStream(dis);
		
		// Load grid
		Log.info("Loading terrain...");
		grid = (MapGrid)ois.readObject();
		
		// Load view
		Log.info("Loading view info...");
		view = (ScrollView)ois.readObject();

		// Load builds
		Log.info("Loading builds...");
		builds = (EntityMap)ois.readObject();
		
		// Load units
		Log.info("Loading units...");
		units = (EntityMap)ois.readObject();
		
		// Load city
		Log.info("Loading city info...");
		playerCity = (PlayerCity)ois.readObject();
		
		// Load world time
		Log.info("Loading time info...");
		time = (WorldTime)ois.readObject();
		
		Log.info("Recomputing data...");		
		recomputeData();
		
		if(!checkIntegrity())
			throw new SlickException("Map is corrupted !");
	}
	
	/**
	 * Recomputes computed transient data.
	 * Must be called after deserialisation.
	 */
	private void recomputeData()
	{
		view.setMapSize(grid.getWidth(), grid.getHeight());
		
		// Set map for units
		Collection<GameComponent> entities = units.asCollection();
		for(GameComponent e : entities)
			((Entity)e).setMap(this);

		entities = builds.asCollection();
		for(GameComponent e : entities)
			((Entity)e).setMap(this);
		
		playerCity.recomputeData(builds.asCollection());
		
		multiPathFinder = new MultiSeedPathFinder(grid.getWidth(), grid.getHeight());
	}
	
	/**
	 * Test method checking if all game components contained in the map have an unique ID.
	 * @return
	 */
	public boolean checkIntegrity()
	{
		Log.debug("Checking Map integrity...");
		
		Collection<GameComponent> unitsCollection = this.units.asCollection();
		Collection<GameComponent> buildsCollection = this.builds.asCollection();
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		boolean res = true;
		int maxID = 0, minID = Integer.MAX_VALUE;
		
		for(GameComponent cmp : unitsCollection) {
			if(cmp.getID() > maxID)
				maxID = cmp.getID();
			if(cmp.getID() < minID)
				minID = cmp.getID();
			if(!ids.contains(cmp.getID()))
				ids.add(cmp.getID());
			else {
				res = false;
				break;
			}
		}
		
		for(GameComponent cmp : buildsCollection) {
			if(cmp.getID() > maxID)
				maxID = cmp.getID();
			if(cmp.getID() < minID)
				minID = cmp.getID();
			if(!ids.contains(cmp.getID()))
				ids.add(cmp.getID());
			else {
				res = false;
				break;
			}
		}
		
		int nextID = GameComponent.makeUniqueID();
		String idGenInfo =
			"cmpCount=" + (builds.size() + units.size()) + ", " +
			"minID=" + minID + ", " +
			"maxID=" + maxID + ", " +
			"nextID=" + nextID;
		
		if(res) {
			if(nextID <= maxID) {
				Log.error("Map corrupted, ID generation will collide existing IDs ! " + idGenInfo);
				return false;
			}
			else {
				Log.debug("Done. No double IDs. " + idGenInfo);
				return true;
			}
		}
		else {
			Log.error("Map corrupted, contains double IDs ! " + idGenInfo);
			return false;
		}
	}

}




