package simciv;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import simciv.buildings.Building;
import simciv.buildings.BuildingFactory;

/**
 * User build engine
 * @author Marc
 *
 */
public class CityBuilder
{
 	private static Sound placeSound;
 	private static Sound eraseSound;
 	
 	public static final int MODE_ERASE = 0;
 	public static final int MODE_ROAD = 1;
 	public static final int MODE_HOUSE = 2;
	public static final int MODE_BUILDS = 3;
 	public static final int MODE_COUNT = 4; // used to count modes

 	// Map cursors
	private Vector2i pos = new Vector2i(); // current pointed cell
	private Vector2i lastPos = new Vector2i(); // last pointed cell (last cell change)
 	private Vector2i lastClickPos = new Vector2i(); // last pointed cell on click
 	private Vector2i buildingPos = new Vector2i();
 	
 	// World access
	private transient World worldRef;
	
	// State
	private int mode;
	private String modeString = "";
	private String helpString = "";
	private Building building; // building to place
	private String buildingString = "";
	private boolean cursorPress = false;
	private int cursorButton;
	
	public CityBuilder(World worldRef)
	{
		this.worldRef = worldRef;
		setMode(MODE_HOUSE);
		setBuildingString("House");
		helpString = "Mode : [R]=roads, [B]=buildings, [E]=erase";
	}
	
	public static void loadContent() throws SlickException
	{
		placeSound = ContentManager.instance().getSound("ui.place");
		eraseSound = ContentManager.instance().getSound("ui.erase");
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
			else if(cursorButton == Input.MOUSE_RIGHT_BUTTON)
				erase();
		}
	}
	
	public void render(Graphics gfx)
	{
		// Pointed cell
		if(worldRef.map.contains(pos.x, pos.y))
		{
			gfx.pushTransform();
			
			gfx.setColor(new Color(255,255,255,64));

			if(mode == MODE_HOUSE)
			{
				gfx.scale(Game.tilesSize, Game.tilesSize);
				gfx.translate(buildingPos.x, buildingPos.y);
				gfx.fillRect(0, 0, building.getWidth(), building.getHeight());
				//building.setPosition(buildingPos.x, buildingPos.y);					
				//building.render(gfx);
			}
			else
			{
				gfx.scale(Game.tilesSize, Game.tilesSize);
				gfx.translate(pos.x, pos.y);
				gfx.fillRect(0, 0, 1, 1);
			}
				
			gfx.popTransform();
		}
	}
	
	public void renderDebugInfo(Graphics gfx)
	{
		gfx.setColor(Color.white);
		gfx.drawString(helpString, 100, 10);
		
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
		
		if(mode == MODE_HOUSE)
		{
			if(button == Input.MOUSE_LEFT_BUTTON)
				placeBuilding();
		}
		else if(mode == MODE_ERASE)
		{
			erase();
		}
		else if(mode == MODE_BUILDS)
		{
			if(button == Input.MOUSE_LEFT_BUTTON)
				placeBuilding();
		}
	}
	
	public void cursorMoved(Vector2i mapPos)
	{
		if(!pos.equals(mapPos))
			lastPos.set(pos);
		pos.set(mapPos);
		
		if(building != null)
		{
			// The cursor must be at the center of the building to place
			buildingPos.x = pos.x - building.getWidth()/2;
			buildingPos.y = pos.y - building.getHeight()/2;
			building.setPosition(buildingPos.x, buildingPos.y);
		}
	}
	
	public void cursorReleased()
	{
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
	
	private void erase()
	{
		if(worldRef.map.eraseRoad(pos.x, pos.y))
		{
			eraseSound.play();
		}
		else if(worldRef.eraseBuilding(pos.x, pos.y))
		{
			eraseSound.play();
		}
	}
	
	private void placeBuilding()
	{
		// Create a new building
		Building b = BuildingFactory.createBuildingFromName(buildingString, worldRef);
		
		if(b != null)
		{
			if(worldRef.placeBuilding(b, buildingPos.x, buildingPos.y))
			{
				placeSound.play();
			}
		}
	}
}


