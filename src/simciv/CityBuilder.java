package simciv;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import simciv.buildings.Building;
import simciv.buildings.BuildingList;

/**
 * User build interface
 * @author Marc
 *
 */
public class CityBuilder
{
 	private static Sound placeSound;
 	private static Sound eraseSound;
 	
 	public static final int MODE_ERASE = 0;
 	public static final int MODE_ROAD = 1;
 	public static final int MODE_BUILDING = 2;
 	public static final int MODE_COUNT = 3; // used to count modes

 	// Map cursors
	Vector2i pos = new Vector2i(); // current pointed cell
	Vector2i lastPos = new Vector2i(); // last pointed cell (last cell change)
 	Vector2i lastClickPos = new Vector2i(); // last pointed cell on click
 	Vector2i buildingPos = new Vector2i();
 		 	
	World worldRef;
	
	// State
	int mode;
	String modeString = "";
	String helpString = "";
	Building building; // building to place
	String buildingString = "";
	
	public CityBuilder(World worldRef)
	{
		this.worldRef = worldRef;
		setMode(MODE_BUILDING);
		setBuildingString("House");
		helpString = "Mode : [R]=roads, [B]=buildings, [E]=erase";
	}
	
	public static void loadContent() throws SlickException
	{
		placeSound = new Sound("data/place.ogg");
		eraseSound = new Sound("data/erase.ogg");
	}
	
	public void setMode(int mode)
	{
		this.mode = mode;
		if(mode == MODE_ERASE)
			modeString = "Erase mode";
		else if(mode == MODE_ROAD)
			modeString = "Road mode";
		else if(mode == MODE_BUILDING)
			modeString = "Building mode";
	}
	
	/**
	 * Sets the current building from its class name
	 * @param bstr : class name
	 */
	public void setBuildingString(String bstr)
	{
		buildingString = bstr;
		building = BuildingList.createBuildingFromName(bstr, worldRef);
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public void update(GameContainer gc)
	{
		Input input = gc.getInput();
		
		if(mode == MODE_ROAD)
		{
			if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
				placeRoad();
			else if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON))
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
				gfx.scale(Game.tilesSize, Game.tilesSize);

				if(mode == MODE_BUILDING)
				{
					gfx.translate(buildingPos.x, buildingPos.y);
					gfx.fillRect(0, 0, building.getWidth(), building.getHeight());
				}
				else
				{
					gfx.translate(pos.x, pos.y);
					gfx.fillRect(0, 0, 1, 1);
				}
				
			gfx.popTransform();
		}
		
		// Strings
		gfx.resetTransform();
		gfx.setColor(Color.white);
		gfx.drawString(helpString, 100, 10);
		
		if(mode == MODE_BUILDING)
			gfx.drawString(modeString + " / " + buildingString, 100, 30);
		else
			gfx.drawString(modeString, 100, 30);
	}
	
	public void cursorPressed(int button, Vector2i mapPos)
	{
		lastClickPos.set(mapPos);
		
		if(mode == MODE_BUILDING)
		{
			if(button == Input.MOUSE_LEFT_BUTTON)
				placeBuilding();
			else if(button == Input.MOUSE_RIGHT_BUTTON)
				erase();
		}
		else if(mode == MODE_ERASE)
		{
			erase();
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
		Building b = BuildingList.createBuildingFromName(buildingString, worldRef);
		
		if(b != null)
		{
			if(worldRef.placeBuilding(b, buildingPos.x, buildingPos.y))
			{
				placeSound.play();
			}
		}
	}
}


