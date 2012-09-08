package simciv.gamestates;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.persistence.GameLoaderThread;
import simciv.persistence.GameSaveData;

public class GameLoadingScreen extends GameInitScreen
{
	private GameSaveData gameData;
	private GameLoaderThread gameLoader;
	
	public GameLoadingScreen(int stateID)
	{
		super(stateID);
	}
	
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException
	{
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		super.enter(container, game);
		
		gameData = new GameSaveData("map");
		gameLoader = new GameLoaderThread(gameData);
		gameLoader.start();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics gfx)
			throws SlickException
	{
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException
	{
		if(gameLoader.isFinished())
		{
			if(gameLoader.isSuccess())
			{
				simciv.Game game = (simciv.Game)sbg;
				game.cityView.setMap(gameData.map);
				game.cityView.setIsGameBeginning(true);
				game.enterState(Game.STATE_CITY_VIEW);
			}
			else
			{
				System.out.println("Couldn't load saved game.");
				sbg.enterState(Game.STATE_MAIN_MENU);
			}
		}
	}
		
}


