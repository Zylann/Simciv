package simciv;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import backend.IntRange2D;
import backend.geom.Vector2i;
import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;
import backend.ui.INotificationListener;
import backend.ui.Notification;

import simciv.builds.Build;
import simciv.builds.BuildFactory;
import simciv.content.Content;
import simciv.units.Unit;

/**
 * City build interface (do not contains GUI)
 * @author Marc
 *
 */
public class CityBuilder
{
	// Constants
	public static final int erasingCost = 1;
	private static final int ROAD_PATHFINDING_DISTANCE = 4096;
 	private static Color canPlaceColor = new Color(64, 255, 64, 128);
 	private static Color cannotPlaceColor = new Color(255, 64, 64, 128);
 	
 	// Modes
 	public static final int MODE_CURSOR = 0;
 	public static final int MODE_ERASE = 1;
 	public static final int MODE_ROAD = 2;
	public static final int MODE_BUILDS = 3;
		
 	// Map cursors
	
	/** Current pointed cell pos **/
	private Vector2i pos = new Vector2i();
	
	/** Pointed cell pos on last mouse press **/
 	private Vector2i lastClickPos = new Vector2i();
 	
 	/** Cell position where the selected build will be placed
 	 * (centered to the pointed cell pos) 
 	 */
 	private Vector2i buildPos = new Vector2i();
 	
 	/** buildPos on last mouse click **/
 	private Vector2i lastClickBuildPos = new Vector2i();

 	/** Build zone computed from buildPos and lastClickBuildPos **/
 	private IntRange2D buildZone = new IntRange2D();
 	
 	/** The map we are currently editing **/
	private transient Map mapRef;
	
	/** Enables sending notifications to the player **/
	private transient INotificationListener notifListener;
	
	/** Edit mode **/
	private int mode;
	
	/** String identifier for the selected build **/
	private String buildString = "";
	
	/** Selected build (enables access to build properties) **/
	private Build build;
	
	/** Informations about what we are pointing at **/
	private String infoLine = "---";
	
	/** Is the mouse button pressed ? **/
	private boolean cursorPress = false;
	
	/** Last pressed mouse button **/
	private int cursorButton;
	
	/** Path followed by the road (in ROAD_MODE) **/
	private List<Vector2i> roadPath;
	
	public CityBuilder(Map mapRef) throws SlickException
	{
		this.mapRef = mapRef;
		setMode(MODE_CURSOR);
		setBuildString("House");
	}
	
	public Map getMap()
	{
		return mapRef;
	}
	
	public void setNotificationListener(INotificationListener n)
	{
		notifListener = n;
	}
	
	public String getInfoLine()
	{
		return infoLine;
	}
	
	/**
	 * Sets the current build mode
	 * @param mode
	 * @return object for chaining
	 * @throws SlickException 
	 */
	public CityBuilder setMode(int mode)
	{
		this.mode = mode;
		return this;
	}
	
	/**
	 * Sets the current building from its class name.
	 * If the build couldn't be created, this method does nothing, and an error will appear in the console.
	 * @param bstr : class name
	 * @return : object for chaining
	 * @throws SlickException 
	 */
	public CityBuilder setBuildString(String bstr)
	{
		try
		{
			build = BuildFactory.createFromName(bstr, mapRef);
			buildString = bstr;
		} catch (SlickException e)
		{
			Log.error("Unrecognized build string '" + bstr + "'");
			e.printStackTrace();
		}
		return this;
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public void update(GameContainer gc)
	{
		updateInfoLine();
	}
	
	private void updateBuildZone()
	{
		int w = 1;
		int h = 1;
		Vector2i startPos = lastClickPos;
		Vector2i endPos = pos;
		
		if(mode == MODE_BUILDS)
		{
			w = build.getWidth();
			h = build.getHeight();
			startPos = lastClickBuildPos;
			endPos = buildPos;
		}
		
		int nbX = Math.abs(startPos.x - endPos.x) / w;
		int nbY = Math.abs(startPos.y - endPos.y) / h;
		
		int kx = startPos.x <= endPos.x ? 1 : -1;
		int ky = startPos.y <= endPos.y ? 1 : -1;
		
		int endX = startPos.x + kx * nbX * w;
		int endY = startPos.y + ky * nbY * h;
				
		buildZone.set(startPos.x, startPos.y, endX, endY);		
	}
	
	protected void updateInfoLine()
	{
		Build pointedBuild = mapRef.getBuild(pos.x, pos.y);
		if(pointedBuild == null)
		{
			// TODO enlarge hover area (more convenient with moving units)
			Unit u = mapRef.getUnit(pos.x, pos.y);
			if(u != null)
				infoLine = u.getInfoLine();
			else
				infoLine = "";
		}
		else
			infoLine = pointedBuild.getInfoLine();
	}

	private void updateRoadPath()
	{
		mapRef.multiPathFinder.setFindBlockedTargets(false);
		mapRef.multiPathFinder.setMaxDistance(ROAD_PATHFINDING_DISTANCE);
		
		roadPath = mapRef.multiPathFinder.findPath(
				lastClickPos.x, lastClickPos.y, 
				new RoadCompliantFloor(), new CursorTarget());
	}

	public void render(Graphics gfx)
	{
		// Pointed cell
		if(mapRef.grid.contains(pos.x, pos.y))
		{
			gfx.pushTransform();
			gfx.scale(Game.tilesSize, Game.tilesSize);
						
			if(mode == MODE_BUILDS)
			{
				if(!cursorPress)
					renderPlaceCursor(gfx, buildPos.x, buildPos.y);
				else if(build != null)
				{
					if(build.getProperties().isRepeatable)
						renderPlaceZone(gfx);
				}
			}
			else if(mode == MODE_ERASE)
			{
				if(!cursorPress)
					renderPlaceCursor(gfx, pos.x, pos.y);
				else
					renderPlaceZone(gfx);
				
				gfx.drawImage(Content.sprites.uiRedCross,
						pos.x, pos.y, 
						pos.x+1, pos.y+1, 
						0, 0,
						Content.sprites.uiRedCross.getWidth(),
						Content.sprites.uiRedCross.getHeight());
			}
			else if(mode == MODE_ROAD)
			{
				if(cursorPress)
					renderRoadPath(gfx);
				else
					renderPlaceCursor(gfx, pos.x, pos.y);
			}
			
			gfx.popTransform();
		}
	}
	
	private void renderPlaceCursor(Graphics gfx, int x, int y)
	{
		int w = 1, h = 1;
		
		if(mode == MODE_ERASE)
		{
			gfx.setColor(cannotPlaceColor);
		}
		else if(mode == MODE_BUILDS)
		{
			w = build.getWidth();
			h = build.getHeight();
			
			if(build.canBePlaced(mapRef.grid, x, y))
				gfx.setColor(canPlaceColor);
			else
				gfx.setColor(cannotPlaceColor);
		}
		else if(mode == MODE_ROAD)
		{
			if(mapRef.isWalkable(x, y))
				gfx.setColor(canPlaceColor);
			else
				gfx.setColor(cannotPlaceColor);
		}
		
		gfx.fillRect(x, y, w, h);
	}
	
	private void renderPlaceZone(Graphics gfx)
	{
		int w = 1;
		int h = 1;
		if(mode == MODE_BUILDS)
		{
			w = build.getWidth();
			h = build.getHeight();
		}
		for(int y = buildZone.minY(); y <= buildZone.maxY(); y += h)
		{
			for(int x = buildZone.minX(); x <= buildZone.maxX(); x += w)
				renderPlaceCursor(gfx, x, y);
		}
	}
	
	private void renderRoadPath(Graphics gfx)
	{
		if(roadPath == null)
		{
			renderPlaceCursor(gfx, pos.x, pos.y);
			return;
		}
		
		for(Vector2i p : roadPath)
			renderPlaceCursor(gfx, p.x, p.y);
	}
	
	public void renderDebugInfo(Graphics gfx)
	{
		gfx.setColor(Color.white);
		gfx.drawString("Mode: " + mode + " / build: " + buildString, 100, 30);
	}
		
	public void cursorPressed(int button, Vector2i mapPos) throws SlickException
	{
		cursorPress = true;
		cursorButton = button;
		lastClickPos.set(mapPos);
		lastClickBuildPos.set(buildPos);

		if(button == Input.MOUSE_RIGHT_BUTTON)
			setMode(MODE_CURSOR);
		else if(button != Input.MOUSE_LEFT_BUTTON)
			return;

		if(mode == MODE_ERASE)
			erase(pos.x, pos.y);
		else if(mode == MODE_BUILDS)
		{
			if(!placeBuild(buildPos.x, buildPos.y))
			{
				if(notifListener != null)
				{
					notifListener.notify(
						Notification.TYPE_ERROR,
						"You can't build this here.", 3000);
				}
			}
		}
		else if(mode == MODE_ROAD)
		{
			roadPath = null;
			placeRoad();
		}
	}
	
	public void cursorMoved(Vector2i mapPos)
	{
		if(!pos.equals(mapPos))
		{
			pos.set(mapPos);
			onPointedCellChanged();
		}
		
		if(mode == MODE_BUILDS && build != null)
		{
			// The cursor must be at the center of the building to place
			buildPos.x = pos.x - build.getWidth()/2;
			buildPos.y = pos.y - build.getHeight()/2;
			build.setPosition(buildPos.x, buildPos.y);
			
			if(build.getProperties().isRepeatable)
				updateBuildZone();
		}
		else if(mode == MODE_ERASE)
		{
			updateBuildZone();
		}
	}
	
	private void onPointedCellChanged()
	{
		updateInfoLine();
		
		if(mode == MODE_ROAD)
		{
			if(cursorPress)
				updateRoadPath();
		}
	}

	public void cursorReleased() throws SlickException
	{
		if(cursorPress && cursorButton == Input.MOUSE_LEFT_BUTTON)
		{
			if((mode == MODE_BUILDS) && build != null)
			{
				if(build.getProperties().isRepeatable)
					placeBuildsFromZone();
			}
			else if(mode == MODE_ERASE)
				eraseFromZone();
			else if(mode == MODE_ROAD)
				placeRoad();
		}
		cursorPress = false;
	}
	
	// Actions
		
	private void placeRoad()
	{
		int length = 0;
		
		if(roadPath != null)
		{
			for(Vector2i p : roadPath)
			{
				if(mapRef.grid.placeRoad(p.x, p.y))
					length++;
			}
		}
		else if(mapRef.grid.placeRoad(pos.x, pos.y))
			length++;
				
		if(length != 0)
		{
			mapRef.playerCity.buy(Road.cost * length);
			Content.sounds.uiPlace.play();
		}
	}
	
	private boolean erase(int x, int y)
	{
		return erase(x, y, true);
	}
	
	/**
	 * This is the main erase method. All things erasing may use it.
	 * @param x
	 * @param y
	 * @param playSound : if we erased something, play a sound
	 * @return true if something has been erased
	 */
	private boolean erase(int x, int y, boolean playSound)
	{
		boolean res = false;
		if(mapRef.grid.eraseRoad(x, y))
			res = true;
		else
		{
			Build b = mapRef.getBuild(x, y);
			if(b != null)
			{
				if(b.canBeErasedByPlayer() || Cheats.isSuperEraser())
				{
					if(Cheats.isBurnOnErase())
						b.destroy(true);
					else
						b.dispose();
					res = true;
				}
			}			
		}
		
		if(res)
		{
			mapRef.playerCity.buy(erasingCost);
			if(playSound)
				Content.sounds.uiErase.play();
		}
		return res;
	}
	
	private int eraseFromZone()
	{
		int nbErased = 0;
		for(int y = buildZone.minY(); y <= buildZone.maxY(); y++)
		{
			for(int x = buildZone.minX(); x <= buildZone.maxX(); x++)
			{
				if(erase(x, y, false))
					nbErased++;
			}
		}
		if(nbErased != 0)
			Content.sounds.uiErase.play();
		return nbErased;
	}
	
	private boolean placeBuild(int x, int y) throws SlickException
	{
		return placeBuild(x, y, true);
	}
	
	/**
	 * The main build placing method. All others may use this one.
	 * @param x
	 * @param y
	 * @param notify : if the build has been placed, play a sound
	 * @return true if the build has been placed, false otherwise
	 * @throws SlickException 
	 */
	private boolean placeBuild(int x, int y, boolean notify) throws SlickException
	{
		// Create a new building
		Build b = BuildFactory.createFromName(buildString, mapRef);

		if(b != null)
		{
			// Place it if possible
			if(mapRef.placeBuild(b, x, y))
			{
				mapRef.playerCity.buy(b.getProperties().cost);
				if(notify)
				{
					Content.sounds.uiPlace.play();
					onPointedCellChanged();
				}
				return true;
			}
			else if(notify)
				Content.sounds.uiPlaceDenied.play(1, 0.3f);
		}
		return false;
	}
		
	// SUGG add a tick delay to zone-placed buildings (because they are created at the same time) ?
	
	private int placeBuildsFromZone() throws SlickException
	{
		int nbPlaced = 0;
		for(int y = buildZone.minY(); y <= buildZone.maxY(); y += build.getHeight())
		{
			for(int x = buildZone.minX(); x <= buildZone.maxX(); x += build.getWidth())
			{
				if(placeBuild(x, y, false))
					nbPlaced++;
			}
		}
		if(nbPlaced != 0)
		{
			Content.sounds.uiPlace.play();
			onPointedCellChanged();
		}
		return nbPlaced;
	}
		
	private class RoadCompliantFloor implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.grid.canPlaceObject(x, y) || mapRef.grid.isRoad(x, y);
		}	
	}
	
	private class CursorTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return pos.equals(x, y);
		}	
	}

}


