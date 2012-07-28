package simciv;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.state.GameState;

import simciv.content.Content;
import simciv.gamestates.GameLoadingScreen;
import simciv.gamestates.GamePlay;
import simciv.gamestates.ContentLoadingScreen;
import simciv.ui.UIStateBasedGame;

/**
 * The main class
 * @author Marc
 *
 */
public class Game extends UIStateBasedGame
{
	// Game constants
    public static final String title = "Simciv - indev";
    public static final int tilesSize = 16;

    // Video settings
    public static final int screenWidth = 1200;
    public static final int screenHeight = 900;
    public static final int framerate = 60;
    // Graphics settings
    public static boolean renderFancyUnitMovements = true;
    
    // State constants
    public static final int STATE_CONTENT_LOADING = 1;
    public static final int STATE_GAME_LOADING = 3;
    public static final int STATE_GAMEPLAY = 4;
    
    // States
    private List<GameState> states = new ArrayList<GameState>();
    public GamePlay gamePlay; // direct access

	public static void main(String[] args)
	{
		try
		{
			// TODO make the game window resizable
			//CanvasGameContainer gc = new CanvasGameContainer(new Game(title));
			AppGameContainer gc = new AppGameContainer(new Game(title));
			// Note : fullscreen works with 800x600 and 1600x1200
			//gc.setSize(screenWidth, screenHeight);
			gc.setDisplayMode(screenWidth, screenHeight, false);
			gc.setTargetFrameRate(framerate);
			gc.setVSync(true);
			gc.setSmoothDeltas(true);
			gc.setUpdateOnlyWhenVisible(true);

			gc.start();
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
    	addState(new ContentLoadingScreen(STATE_CONTENT_LOADING));
    	addState(new GameLoadingScreen(STATE_GAME_LOADING));
    	gamePlay = new GamePlay(STATE_GAMEPLAY);
    	addState(gamePlay);
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
		// Load global font (minimal)
		Content.loadMinimalContent();
		
		// Load content (deferred)
		Content.loadFromContentFile("data/content.xml");
		
		// Initialize states
    	for(GameState state : states)
    	{
    		state.init(container, this);
    	}
    	
    	// Enter first state
    	enterState(STATE_CONTENT_LOADING);
	}

}


