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
 * User build engine
 * @author Marc
 *
 */
// TODO merge this class to GamePlay ?
public class CityBuilder
{
 	private static Sound placeSound;
 	private static Sound eraseSound;
 	private static Color canPlaceColor = new Color(64, 255, 64, 128);
 	private static Color cannotPlaceColor = new Color(255, 64, 64, 128);
 	
 	public static final int MODE_CURSOR = 0;
 	public static final int MODE_ERASE = 1;
 	public static final int MODE_ROAD = 2;
 	public static final int MODE_HOUSE = 3;
	public static final int MODE_BUILDS = 4;
 	//public static final int MODE_COUNT = 5; // used to count modes

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
	
	public static void loadContent() throws SlickException
	{
		placeSound = Content.sounds.uiPlace;
		eraseSound = Content.sounds.uiErase;
	}
	
	public String getInfoText()
	{
		return infoText;
	}
	
	public void setMode(int mode)
	{
		this.mode = mode;
		if(mode == MODE_ERASE)
			modeString = "Erase mode";
		else if(mode == MODE_ROAD)
			modeString = "Road mode";
		else if(mode == MODE_HOUSE)
			modeString = "Building mode";
	}
	
	/**
	 * Sets the current building from its class name
	 * @param bstr : class name
	 */
	public void setBuildingString(String bstr)
	{
		buildingString = bstr;
		building = BuildingFactory.createBuildingFromName(bstr, worldRef);
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
				
				// Red cross
				gfx.setColor(new Color(255,0,0,255));
				gfx.translate(pos.x, pos.y);
				gfx.setLineWidth(8);
				gfx.drawLine(0, 0, 1, 1);
				gfx.drawLine(0, 1, 1, 0);
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
		
		if(mode == MODE_HOUSE)
		{
			if(button == Input.MOUSE_LEFT_BUTTON)
				placeBuilding(buildPos.x, buildPos.y);
		}
		else if(mode == MODE_ERASE)
		{
			erase(pos.x, pos.y);
		}
		else if(mode == MODE_BUILDS)
		{
			if(button == Input.MOUSE_LEFT_BUTTON)
				placeBuilding(buildPos.x, buildPos.y);
		}
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
		if(cursorPress)
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
		if(worldRef.map.placeRoad(pos.x, pos.y))
		{
			placeSound.play();
		}
	}
	
	private boolean erase(int x, int y)
	{
		return erase(x, y, true);
	}
	
	private boolean erase(int x, int y, boolean notify)
	{
		boolean res = false;
		if(worldRef.map.eraseRoad(x, y))
			res = true;
		else if(worldRef.eraseBuilding(x, y))
			res = true;
		if(res && notify)
			eraseSound.play();
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
	
	private boolean placeBuilding(int x, int y, boolean notify)
	{
		// Create a new building
		Building b = BuildingFactory.createBuildingFromName(buildingString, worldRef);
		
		if(b != null)
		{
			if(worldRef.placeBuilding(b, x, y))
			{
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
	
	// TODO Limit nb sounds played at the same time
	
	// SUGG add a tick delay to zone-placed buildings?
	
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


