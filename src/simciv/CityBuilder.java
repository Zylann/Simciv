package simciv;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import simciv.buildings.Building;
import simciv.buildings.BuildingFactory;
import simciv.content.Content;

/**
 * City build engine
 * @author Marc
 *
 */
// TODO merge this class to GamePlay ?
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
 	public static final int MODE_HOUSE = 3;
	public static final int MODE_BUILDS = 4;
	
	public static final int erasingCost = 1;

 	// Map cursors
	private Vector2i pos = new Vector2i(); // current pointed cell
	private Vector2i lastPos = new Vector2i(); // last pointed cell (last cell change)
 	private Vector2i lastClickPos = new Vector2i(); // last pointed cell on click
 	private Vector2i buildPos = new Vector2i();
 	private Vector2i lastClickBuildPos = new Vector2i();
 	private IntRange2D buildZone = new IntRange2D();
 	
 	// World access
	private transient World worldRef;
	
	// State
	private int mode;
	private String modeString = "";
	private Building building; // building to place
	private Building pointedBuilding;
	private String buildingString = "";
	private String infoText = "Testing 1234";
	private boolean cursorPress = false;
	private int cursorButton;
	
	public CityBuilder(World worldRef)
	{
		this.worldRef = worldRef;
		setMode(MODE_CURSOR);
		setBuildingString("House");
	}
	
	public World getWorld()
	{
		return worldRef;
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
	 */
	public CityBuilder setMode(int mode)
	{
		this.mode = mode;
		if(mode == MODE_ERASE)
			modeString = "Erase mode";
		else if(mode == MODE_ROAD)
			modeString = "Road mode";
		else if(mode == MODE_HOUSE)
			modeString = "Building mode";
		return this;
	}
	
	/**
	 * Sets the current building from its class name
	 * @param bstr : class name
	 * @return : object for chaining
	 */
	public CityBuilder setBuildingString(String bstr)
	{
		buildingString = bstr;
		building = BuildingFactory.createFromName(bstr, worldRef);
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
		if(worldRef.map.contains(pos.x, pos.y))
		{
			gfx.pushTransform();
			gfx.scale(Game.tilesSize, Game.tilesSize);
						
			if(mode == MODE_HOUSE || mode == MODE_BUILDS)
			{
				if(!cursorPress)
					renderPlaceCursor(gfx, buildPos.x, buildPos.y);
				else
					renderPlaceZone(gfx);
			}
			else if(mode == MODE_ERASE)
			{
				if(!cursorPress)
					renderPlaceCursor(gfx, pos.x, pos.y);
				else
					renderPlaceZone(gfx);
				
				gfx.drawImage(Content.images.uiRedCross,
						pos.x, pos.y, 
						pos.x+1, pos.y+1, 
						0, 0,
						Content.images.uiRedCross.getWidth(),
						Content.images.uiRedCross.getHeight());
			}
			else if(mode == MODE_ROAD)
			{
				gfx.translate(pos.x, pos.y);
				
				if(worldRef.map.canPlaceObject(pos.x, pos.y))
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
		else if(mode == MODE_BUILDS || mode == MODE_HOUSE)
		{
			w = building.getWidth();
			h = building.getHeight();
			
			if(building.canBePlaced(worldRef.map, x, y))
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
		if(mode == MODE_HOUSE || mode == MODE_BUILDS)
		{
			w = building.getWidth();
			h = building.getHeight();
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
		if(mode == MODE_HOUSE)
			gfx.drawString(modeString + " / " + buildingString, 100, 30);
		else
			gfx.drawString(modeString, 100, 30);
	}
	
	public void cursorPressed(int button, Vector2i mapPos)
	{
		cursorPress = true;
		cursorButton = button;
		lastClickPos.set(mapPos);
		lastClickBuildPos.set(buildPos);

		if(button != Input.MOUSE_LEFT_BUTTON)
			return;

		if(mode == MODE_HOUSE)
			placeBuilding(buildPos.x, buildPos.y);
		else if(mode == MODE_ERASE)
			erase(pos.x, pos.y);
		else if(mode == MODE_BUILDS)
			placeBuilding(buildPos.x, buildPos.y);
	}
	
	public void cursorMoved(Vector2i mapPos)
	{
		if(!pos.equals(mapPos))
		{
			lastPos.set(pos);
			pos.set(mapPos);
			onPointedCellChanged();
		}
		
		if(building != null)
		{
			// The cursor must be at the center of the building to place
			buildPos.x = pos.x - building.getWidth()/2;
			buildPos.y = pos.y - building.getHeight()/2;
			building.setPosition(buildPos.x, buildPos.y);
			
			updateBuildZone();
		}
	}
	
	private void onPointedCellChanged()
	{
		pointedBuilding = worldRef.getBuilding(pos.x, pos.y);
		updateInfoText();
	}
	
	private void updateBuildZone()
	{
		int w = 1;
		int h = 1;
		Vector2i startPos = lastClickPos;
		Vector2i endPos = pos;
		if(mode == MODE_BUILDS || mode == MODE_HOUSE)
		{
			w = building.getWidth();
			h = building.getHeight();
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
		if(pointedBuilding == null)
			infoText = "";
		else
			infoText = pointedBuilding.getInfoString();
	}

	public void cursorReleased()
	{
		if(cursorPress && cursorButton == Input.MOUSE_LEFT_BUTTON)
		{
			if(mode == MODE_BUILDS || mode == MODE_HOUSE)
				placeBuildingsFromZone();
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
		if(worldRef.map.placeRoad(pos.x, pos.y))
		{
			worldRef.playerCity.buy(Road.cost);
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
		if(worldRef.map.eraseRoad(x, y))
			res = true;
		else if(worldRef.eraseBuilding(x, y))
			res = true;
		if(res)
		{
			worldRef.playerCity.buy(erasingCost);
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
	
	private boolean placeBuilding(int x, int y)
	{
		return placeBuilding(x, y, true);
	}
	
	/**
	 * The main build placing method. All others may use this one.
	 * @param x
	 * @param y
	 * @param notify : if the build has been placed, play a sound
	 * @return true if the build has been placed, false otherwise
	 */
	private boolean placeBuilding(int x, int y, boolean notify)
	{
		// Create a new building
		Building b = BuildingFactory.createFromName(buildingString, worldRef);

		if(b != null)
		{
			// Place it if possible
			if(worldRef.placeBuilding(b, x, y))
			{
				worldRef.playerCity.buy(b.getProperties().cost);
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
	
	private int placeBuildingsFromZone()
	{
		int nbPlaced = 0;
		for(int y = buildZone.minY(); y <= buildZone.maxY(); y += building.getHeight())
		{
			for(int x = buildZone.minX(); x <= buildZone.maxX(); x += building.getWidth())
			{
				if(placeBuilding(x, y, false))
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


