package simciv;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

import simciv.buildings.FarmLand;
import simciv.buildings.House;
import simciv.units.Citizen;
import simciv.units.Nomad;

public class Game extends StateBasedGame
{
	// Game constants
    public static final int screenWidth = 800;
    public static final int screenHeight = 600;
    public static final int framerate = 60;
    public static final String title = "Simciv - indev";
    public static final int tilesSize = 16;
    
    // States
    public static final int STATE_GAMEPLAY = 4;
    
    List<GameState> states = new ArrayList<GameState>();

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer app = new AppGameContainer(new Game(title));
			// Note : fullscreen works with 800x600 and 1600x1200
			app.setDisplayMode(screenWidth, screenHeight, false);
			app.setTargetFrameRate(framerate);
			app.setVSync(true);
			app.setSmoothDeltas(true);
			app.start();
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}

    public Game(String title)
	{
		super(title);
		
		// Create states
    	addState(new GamePlay(STATE_GAMEPLAY));
    	
    	// Enter first state
    	enterState(STATE_GAMEPLAY);
	}
    
    @Override
    public void addState(GameState state)
    {
    	states.add(state);
    	super.addState(state);
    }

	@Override
	public void initStatesList(GameContainer container) throws SlickException
	{
		Terrain.initialize();
		Road.loadContent();
		Citizen.loadContent();
		House.loadContent();
		CityBuilder.loadContent();
		FarmLand.loadContent();
		Building.loadContent();
		Nomad.loadContent();

		// Initialize states
    	for(GameState state : states)
    	{
    		state.init(container, this);
    	}
    	
    	// Enter first state
    	enterState(STATE_GAMEPLAY);
	}
}


