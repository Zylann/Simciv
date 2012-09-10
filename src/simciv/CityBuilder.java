package simciv;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import simciv.builds.Build;
import simciv.builds.BuildFactory;
import simciv.content.Content;

/**
 * City build interface (do not contains GUI)
 * @author Marc
 *
 */
public class CityBuilder
{
	// Media
 	private static Sound placeSound;
 	private static Sound eraseSound;
 	private static Color canPlaceColor = new Color(64, 255, 64, 128);
 	private static Color cannotPlaceColor = new Color(255, 64, 64, 128);
 	
 	// Modes
 	public static final int MODE_CURSOR = 0;
 	public static final int MODE_ERASE = 1;
 	public static final int MODE_ROAD = 2;
	public static final int MODE_BUILDS = 3;
	
	public static final int erasingCost = 1;

 	// Map cursors
	private Vector2i pos = new Vector2i(); // current pointed cell
	private Vector2i lastPos = new Vector2i(); // last pointed cell (last cell change)
 	private Vector2i lastClickPos = new Vector2i(); // last pointed cell on click
 	private Vector2i buildPos = new Vector2i();
 	private Vector2i lastClickBuildPos = new Vector2i();
 	private IntRange2D buildZone = new IntRange2D();
 	
 	// World access
	private transient Map mapRef;
	
	// State
	private int mode;
	private String modeString = "";
	private Build build; // building to place
	private Build pointedBuild;
	private String buildString = "";
	private String infoText = "Testing 1234";
	private boolean cursorPress = false;
	private int cursorButton;
	
	public CityBuilder(Map mapRef) throws SlickException
	{
		this.mapRef = mapRef;
		setMode(MODE_CURSOR);
		setBuildString("House");
	}
	
	public Map getWorld()
	{
		return mapRef;
	}

	public static void loadContent() throws SlickException
	{
		placeSound = Content.sounds.uiPlace;
		eraseSound = Content.sounds.uiErase;
	}
	
	public String getInfoText()
	{
		return infoText;
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
		if(mode == MODE_ERASE)
			modeString = "Erase mode";
		else if(mode == MODE_ROAD)
			modeString = "Road mode";
		else if(mode == MODE_BUILDS)
			modeString = "Builds mode";
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
			System.out.println("ERROR: Unrecognized build string '" + bstr + "'");
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
		if(mode == MODE_ROAD && cursorPress)
		{
			if(cursorButton == Input.MOUSE_LEFT_BUTTON)
				placeRoad();
		}
		this.updateInfoText();
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
				gfx.translate(pos.x, pos.y);
				
				if(mapRef.grid.canPlaceObject(pos.x, pos.y))
					gfx.setColor(canPlaceColor);
				else
					gfx.setColor(cannotPlaceColor);
					
				gfx.fillRect(0, 0, 1, 1);
			}
			
			gfx.popTransform();
		}
	}
	
	private void renderPlaceCursor(Graphics gfx, int x, int y)
	{
		int w = 1;
		int h = 1;
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
	
	public void renderDebugInfo(Graphics gfx)
	{
		gfx.setColor(Color.white);
		if(mode == MODE_BUILDS)
			gfx.drawString(modeString + " / " + buildString, 100, 30);
		else
			gfx.drawString(modeString, 100, 30);
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
			placeBuild(buildPos.x, buildPos.y);
	}
	
	public void cursorMoved(Vector2i mapPos)
	{
		if(!pos.equals(mapPos))
		{
			lastPos.set(pos);
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
		pointedBuild = mapRef.getBuild(pos.x, pos.y);
		updateInfoText();
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
	
	protected void updateInfoText()
	{
		if(pointedBuild == null)
			infoText = "";
		else
			infoText = pointedBuild.getInfoString();
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
		}
		cursorPress = false;
	}
	
	/*
	 * Actions :
	 * (Later, they will cost money and/or resources)
	 */
	
	private void placeRoad()
	{
		// TODO enable auto-pathfinding by letting the mouse pressed
		if(mapRef.grid.placeRoad(pos.x, pos.y))
		{
			mapRef.playerCity.buy(Road.cost);
			placeSound.play();
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
	 * @param notify : if we erased something, play a sound
	 * @return true if something has been erased
	 */
	private boolean erase(int x, int y, boolean notify)
	{
		boolean res = false;
		if(mapRef.grid.eraseRoad(x, y))
			res = true;
		else if(mapRef.eraseBuild(x, y))
			res = true;
		if(res)
		{
			mapRef.playerCity.buy(erasingCost);
			if(notify)
				eraseSound.play();
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
			eraseSound.play();
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
					placeSound.play();
					onPointedCellChanged();
				}
				return true;
			}
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
			placeSound.play();
			onPointedCellChanged();
		}
		return nbPlaced;
	}
	
}


